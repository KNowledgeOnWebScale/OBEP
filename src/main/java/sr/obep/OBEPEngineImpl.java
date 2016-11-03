package sr.obep;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.semanticweb.owlapi.model.OWLOntology;
import sr.obep.abstration.Abstracter;
import sr.obep.cep.EventProcessorImpl;
import sr.obep.extraction.Extractor;

/**
 * Created by Riccardo on 03/11/2016.
 */
@AllArgsConstructor
@Setter
@Getter
public class OBEPEngineImpl implements OBEPEngine{

    private Abstracter abstracter;
    private Extractor extractor;
    private EventProcessorImpl cep;


    public void setOntology(OWLOntology o) {

    }

    public void registerQuery(OBEPQuery q, QueryConsumer c) {

    }

    public void sendEvent(SemanticEvent se) {
    	//TODO add SemanticEvent hierarchy
    	if(se.getTriggeredFilterIRIs() == null ){
    		abstracter.sendEvent(se);
    	}
    	if(se.getTriggeredFilterIRIs()!= null && !se.getTriggeredFilterIRIs().isEmpty() && se.getProperties() == null){
    		extractor.sendEvent(se);
    	}
    	if(se.getProperties() != null){
    		cep.sendEvent(se);
    	}
    }
}
