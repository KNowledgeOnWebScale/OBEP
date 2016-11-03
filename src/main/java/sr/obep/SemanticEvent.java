package sr.obep;

import lombok.Data;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by Riccardo on 03/11/2016.
 */
@Data
public class SemanticEvent implements Serializable {

    private Set<OWLAxiom> axioms;
    private OWLNamedIndividual message;
    private String packetID;
    private Set<String> triggeredFilterIRIs;
    private long timeStamp;
    private String stream;

}
