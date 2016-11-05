package sr.obep.querying;

import org.apache.jena.query.Query;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;

import java.util.Set;

/**
 * Created by Riccardo on 03/11/2016.
 */
public interface OBEPQuery {

    public Set<OWLEquivalentClassesAxiom> getEventDefinitions();

    public Query getQ();
}
