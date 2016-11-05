package sr.obep;

import org.semanticweb.owlapi.model.OWLOntology;
import sr.obep.querying.OBEPQueryImpl;
import sr.obep.querying.QueryConsumer;


/**
 * Created by Riccardo on 03/11/2016.
 */
public interface OBEPEngine {

    public void init(OBEPEngine obep);

    public void setOntology(OWLOntology o);

    public void registerQuery(OBEPQueryImpl q, QueryConsumer c);

    public void sendEvent(SemanticEvent se);
}
