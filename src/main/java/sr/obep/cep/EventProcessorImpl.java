package sr.obep.cep;

import com.espertech.esper.client.*;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import lombok.extern.log4j.Log4j;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.lang.SyntaxVarScope;
import sr.obep.OBEPEngine;
import sr.obep.SemanticEvent;
import sr.obep.parser.delp.DLEventDecl;
import sr.obep.parser.delp.EventCalculusDecl;
import sr.obep.parser.delp.EventDecl;
import sr.obep.parser.delp.IFDecl;
import sr.obep.querying.OBEPQuery;
import sr.obep.querying.OBEPQueryImpl;

import javax.xml.stream.EventFilter;
import java.util.*;

/**
 * Created by Riccardo on 03/11/2016.
 */
@Log4j
public class EventProcessorImpl implements EventProcessor {

    private EPServiceProvider epService;
    private Map<EventDecl, Query> filterQueries;
    private EPAdministrator cepAdm;

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
     * @param longName The name of the filter containing the prefixes
     * @return
     */
    private String stripFilterName(String longName) {
        if (longName.contains("#")) {
            return longName.substring(longName.lastIndexOf('#') + 1);

        } else {
            return longName.substring(longName.lastIndexOf('/') + 1);
        }
    }

    public void init(OBEPEngine obep) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("packedId", "string");

        Class<SemanticEvent> value = SemanticEvent.class;
        properties.put("content", value);
        properties.put("ts", "long");

        Configuration configuration = new Configuration();
        configuration.addEventType("TEvent", properties);
        configuration.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
        configuration.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
        configuration.getEngineDefaults().getLogging().setEnableTimerDebug(true);
        this.epService = EPServiceProviderManager.getDefaultProvider(configuration);
        this.cepAdm = epService.getEPAdministrator();

        // disable internal clock

    }


    public void registerQuery(OBEPQuery query) {
        this.filterQueries = new HashMap<EventDecl, Query>();

        OBEPQueryImpl q = (OBEPQueryImpl) query;

        if (q.getEventDeclarations() != null) {
            Map<Node, EventDecl> eventDeclarations = q.getEventDeclarations();
            Set<Map.Entry<Node, EventDecl>> entries = eventDeclarations.entrySet();
            for (Map.Entry<Node, EventDecl> entry : entries) {
                EventDecl value = entry.getValue();

                if (value instanceof EventCalculusDecl) {

                    EventCalculusDecl e = (EventCalculusDecl) value;
                    System.out.println(e.toString());

                    Set<Var> joinVariables = e.getJoinVariables();

                    if (q.getEventDeclarations() != null) {
                        for (Map.Entry<Node, EventDecl> en : entries) {
                            EventDecl v = en.getValue();

                            if (v instanceof DLEventDecl) {
                                DLEventDecl dl = (DLEventDecl) v;

                                String s = dl.toEPLSchema(joinVariables);
                                System.out.println(s);
                                cepAdm.createEPL(s);

                            }

                        }
                    }

                    EPStatementObjectModel epStatementObjectModel = e.toEpl();
                    System.out.println(epStatementObjectModel.toEPL());
                    cepAdm.create(epStatementObjectModel);
                }

            }
        }


    }


}
