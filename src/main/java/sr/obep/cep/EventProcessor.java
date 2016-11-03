package sr.obep.cep;

import org.semanticweb.owlapi.model.OWLOntology;

import sr.obep.OBEPEngine;
import sr.obep.OBEPQuery;
import sr.obep.OBEPQueryImpl;
import sr.obep.SemanticEvent;

/**
 * Created by Riccardo on 03/11/2016.
 */
public interface EventProcessor {
	
	public void init(OBEPEngine obep);

	public void setOntology(OWLOntology o);

	public void registerQuery(OBEPQuery q);

	public void sendEvent(SemanticEvent se);
}
