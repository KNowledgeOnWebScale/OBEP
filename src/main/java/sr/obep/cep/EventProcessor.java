package sr.obep.cep;

import org.semanticweb.owlapi.model.OWLOntology;
import sr.obep.OBEPEngine;
import sr.obep.OBEPQuery;
import sr.obep.SemanticEvent;

public interface EventProcessor {

    public void init(OBEPEngine obep);

    public void registerQuery(OBEPQuery q);

    public void sendEvent(SemanticEvent se);
}