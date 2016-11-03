package sr.obep.abstration;

import org.semanticweb.owlapi.model.OWLOntology;

import lombok.Getter;
import lombok.Setter;
import sr.obep.OBEPEngine;
import sr.obep.querying.OBEPQuery;
import sr.obep.OBEPQueryImpl;
import sr.obep.QueryConsumer;
import sr.obep.SemanticEvent;
import sr.obep.cep.EventProcessor;
import sr.obep.extraction.Extractor;

/**
 * Created by Riccardo on 03/11/2016.
 */

public interface Abstracter {
	
	public void init(OBEPEngine obep);
	
	public void setOntology(OWLOntology o);

    public void registerQuery(OBEPQuery q );

    public void sendEvent(SemanticEvent se);
}
