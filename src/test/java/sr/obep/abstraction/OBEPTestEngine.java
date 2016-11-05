package sr.obep.abstraction;

import lombok.Getter;
import org.semanticweb.owlapi.model.OWLOntology;
import sr.obep.OBEPEngine;
import sr.obep.SemanticEvent;
import sr.obep.querying.OBEPQuery;
import sr.obep.querying.OBEPQueryImpl;
import sr.obep.querying.QueryConsumer;

@Getter
public class OBEPTestEngine implements OBEPEngine {

    private SemanticEvent receivedEvent;

    @Override
    public void init(OBEPEngine obep) {

    }

    @Override
    public void setOntology(OWLOntology o) {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerQuery(OBEPQueryImpl q, QueryConsumer c) {

    }


    @Override
    public void sendEvent(SemanticEvent se) {
        this.receivedEvent = se;

    }

}
