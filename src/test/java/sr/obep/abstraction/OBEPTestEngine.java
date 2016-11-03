package sr.obep.abstraction;

import org.semanticweb.owlapi.model.OWLOntology;

import lombok.Getter;
import sr.obep.OBEPEngine;
import sr.obep.OBEPQuery;
import sr.obep.QueryConsumer;
import sr.obep.SemanticEvent;

@Getter
public class OBEPTestEngine implements OBEPEngine{

	private SemanticEvent receivedEvent;
	@Override
	public void setOntology(OWLOntology o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerQuery(OBEPQuery q, QueryConsumer c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEvent(SemanticEvent se) {
		this.receivedEvent = se;
		
	}

}
