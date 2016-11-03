package sr.obep;

import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Created by Riccardo on 03/11/2016.
 */
public interface OBEPEngine {

    public void setOntology(OWLOntology o);

    public void registerQuery(OBEPQuery q, QueryConsumer c );

    public void sendEvent(SemanticEvent se);
}
