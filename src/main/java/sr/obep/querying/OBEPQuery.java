package sr.obep.querying;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;

/**
 * Created by Riccardo on 03/11/2016.
 */
public interface OBEPQuery {
	
	public Set<OWLEquivalentClassesAxiom> getEventDefinitions();
}
