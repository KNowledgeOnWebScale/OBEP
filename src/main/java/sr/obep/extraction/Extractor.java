package sr.obep.extraction;

import org.semanticweb.owlapi.model.OWLOntology;

import sr.obep.OBEPEngine;
import sr.obep.querying.OBEPQuery;
import sr.obep.SemanticEvent;

/**
 * Created by Riccardo on 03/11/2016.
 */
public interface Extractor {

	public void init(OBEPEngine obep);

	public void setOntology(OWLOntology o);

	public void registerQuery(OBEPQuery q);

	public void sendEvent(SemanticEvent se);
}
