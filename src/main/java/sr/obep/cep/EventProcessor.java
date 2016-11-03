package sr.obep.cep;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import lombok.extern.log4j.Log4j;
import org.apache.jena.query.Query;
import sr.obep.OBEPQuery;
import sr.obep.SemanticEvent;
import sr.obep.parser.delp.EventCalculusDecl;
import sr.obep.parser.delp.EventDecl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Riccardo on 03/11/2016.
 */
@Log4j
public class EventProcessor {

    private EPServiceProvider epService;
    private Map<EventDecl, Query> filterQueries;


    public EventProcessor() {

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

    public void setCEPQuery(OBEPQuery query) {

        this.filterQueries = new HashMap<EventDecl, Query>();

        for (EventDecl event : query.getEventCalculusDecls()) {
            EventCalculusDecl ecd = (EventCalculusDecl) event;
            log.info("Registering Match clause <" + ecd.toEpl() + ">");
            EPStatement epStatement = epService.getEPAdministrator().create(ecd.toEpl());
            //TODO check inherits
            EventListener eListener = new EventListener();
            // TODO: do we need a new listerner here for each query??
            epStatement.addListener(eListener);

            /*
            String eplProps = converVarsToEPLProps(ifdec.getVars());
            epService.getEPAdministrator().createEPL("create schema " + event + "("+eplProps+") inherits TEvent");*/
        }

    }

    /**
     * Add an OWLMessage to the event processor for further processing using the
     * defined event calculus. (runs in a different thread)
     *
     * @param message

    private void put(SemanticEvent message) {
        Set<String> triggeredFilters = message.getTriggeredFilters();

        for (String trigger : triggeredFilters) {
            Map<String, Object> eventMap = new HashMap<String, Object>();
            String eventType = stripFilterName(trigger);
            if (filterQueries.containsKey(eventType)) {
                List<Map<String, String>> queryResults = qFactory.query(message.getAxioms(),
                        filterQueries.get(eventType));
                String value = "";
                String key = "";
                for (Map<String, String> result : queryResults) {
                    for (Entry<String, String> entryResult : result.entrySet()) {
                        value = entryResult.getValue();
                        key = entryResult.getKey();
                        eventMap.put(key, value);
                    }
                }

                // eventMap.put("eventType", eventType);
                eventMap.put("packedId", message.getPacketID());
                eventMap.put("ts", System.currentTimeMillis());
                eventMap.put("content", message);

                logger.info("Adding Event (" + this + ") " + eventMap);
                epService.getEPRuntime().sendEvent(eventMap, eventType);
            } else {
                logger.error("Event <" + eventType + "> not declared.");
            }
        }

    }  **/
}