package sr.obep;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;

public interface OBEPQuery {

	public Set<OWLEquivalentClassesAxiom> getEventDefinitions();
}
