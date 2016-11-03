package sr.obep.cep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

import lombok.extern.log4j.Log4j;
import sr.obep.OBEPEngine;
import sr.obep.OBEPQuery;
import sr.obep.SemanticEvent;
import sr.obep.parser.delp.DLEventDecl;
import sr.obep.parser.delp.EventCalculusDecl;
import sr.obep.parser.delp.EventDecl;

/**
 * Created by Riccardo on 03/11/2016.
 */
@Log4j
public class EventProcessorImpl implements EventProcessor {

	private EPServiceProvider epService;
	private Map<EventDecl, Query> filterQueries;

	public void init(OBEPEngine obep) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("packedId", "string");

		properties.put("ts", "long");
		properties.put("content", SemanticEvent.class);

		Configuration configuration = new Configuration();
		configuration.addEventType("TEvent", properties);
		configuration.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
		configuration.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
		configuration.getEngineDefaults().getLogging().setEnableTimerDebug(true);
		this.epService = EPServiceProviderManager.getDefaultProvider(configuration);

		// disable internal clock

	}

	public void registerQuery(OBEPQuery q) {
		this.filterQueries = new HashMap<EventDecl, Query>();
		for (EventDecl event : q.getEventCalculusDecls()) {
			EventCalculusDecl ecd = (EventCalculusDecl) event;
			for (Map.Entry<Node, EventDecl> en : q.getEventDeclarations().entrySet()) {
				if (en.getValue() instanceof DLEventDecl) {
					sr.obep.parser.delp.DLEventDecl dl = (DLEventDecl) en.getValue();
					String s = dl.toEPLSchema(ecd.getJoinVariables());
					System.out.println(s);
					epService.getEPAdministrator().createEPL(s);
				}

			}

			
			epService.getEPAdministrator().create(ecd.toEpl());
		}

//		for (EventDecl event : q.getEventCalculusDecls()) {
//			EventCalculusDecl ecd = (EventCalculusDecl) event;
//			log.info("Registering Match clause <" + ecd.toEpl() + ">");
//			EPStatement epStatement = epService.getEPAdministrator().create(ecd.toEpl());
//			// TODO check inherits
//			EventListener eListener = new EventListener();
//			// TODO: do we need a new listerner here for each query??
//			epStatement.addListener(eListener);
//
//			/*
//			 * String eplProps = converVarsToEPLProps(ifdec.getVars());
//			 * epService.getEPAdministrator().createEPL("create schema " + event
//			 * + "("+eplProps+") inherits TEvent");
//			 */
//		}
	}

	public void sendEvent(SemanticEvent se) {
		Set<String> triggeredFilters = se.getTriggeredFilterIRIs();

		for (String trigger : triggeredFilters) {
			String eventType = stripFilterName(trigger);

			Map<String, Object> eventMap = new HashMap<String, Object>();
			eventMap.put("packedId", se.getPacketID());
			eventMap.put("ts", System.currentTimeMillis());
			eventMap.put("content", se);			
			eventMap.putAll(se.getProperties());

			log.info("Adding Event (" + this + ") " + eventMap);
			epService.getEPRuntime().sendEvent(eventMap, eventType);
		}
	}

	/**
	 * Strings the prefixes from the filter e.g. test.prefix/test.owl#Filter
	 * becomes Filter.
	 * 
	 * @param longName
	 *            The name of the filter containing the prefixes
	 * @return
	 */
	private String stripFilterName(String longName) {
		if (longName.contains("#")) {
			return longName.substring(longName.lastIndexOf('#') + 1);

		} else {
			return longName.substring(longName.lastIndexOf('/') + 1);
		}
	}
	/**
	 * Add an OWLMessage to the event processor for further processing using the
	 * defined event calculus. (runs in a different thread)
	 *
	 * @param message
	 * 
	 *            private void put(SemanticEvent message) { Set<String>
	 *            triggeredFilters = message.getTriggeredFilters();
	 * 
	 *            for (String trigger : triggeredFilters) { Map<String, Object>
	 *            eventMap = new HashMap<String, Object>(); String eventType =
	 *            stripFilterName(trigger); if
	 *            (filterQueries.containsKey(eventType)) { List<Map<String,
	 *            String>> queryResults = qFactory.query(message.getAxioms(),
	 *            filterQueries.get(eventType)); String value = ""; String key =
	 *            ""; for (Map<String, String> result : queryResults) { for
	 *            (Entry<String, String> entryResult : result.entrySet()) {
	 *            value = entryResult.getValue(); key = entryResult.getKey();
	 *            eventMap.put(key, value); } }
	 * 
	 *            // eventMap.put("eventType", eventType);
	 *            eventMap.put("packedId", message.getPacketID());
	 *            eventMap.put("ts", System.currentTimeMillis());
	 *            eventMap.put("content", message);
	 * 
	 *            logger.info("Adding Event (" + this + ") " + eventMap);
	 *            epService.getEPRuntime().sendEvent(eventMap, eventType); }
	 *            else { logger.error("Event <" + eventType + "> not
	 *            declared."); } }
	 * 
	 *            }
	 **/
}
