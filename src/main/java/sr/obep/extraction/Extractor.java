package sr.obep.extraction;

import org.semanticweb.owlapi.model.OWLOntology;
import sr.obep.OBEPEngine;
import sr.obep.SemanticEvent;
import sr.obep.querying.OBEPQuery;

/**
 * Created by pbonte on 03/11/2016.
 */
public interface Extractor {

    public void init(OBEPEngine obep);

    public void registerQuery(OBEPQuery q);

    public void setOntology(OWLOntology o);

    public void sendEvent(SemanticEvent se);
}
