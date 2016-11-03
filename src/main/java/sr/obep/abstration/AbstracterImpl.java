package sr.obep.abstration;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.NodeSet;

import lombok.Getter;
import lombok.Setter;
import sr.obep.OBEPEngine;
import sr.obep.querying.OBEPQuery;
import sr.obep.querying.OBEPQueryImpl;
import sr.obep.SemanticEvent;

@Setter
@Getter
public class AbstracterImpl implements Abstracter {

	private OWLOntology ontology;
	private Reasoner reasoner;
	private OWLOntologyManager manager;
	private Set<String> eventDefinitions;
	private OBEPEngine obep;
	
	public AbstracterImpl() {
		eventDefinitions = new HashSet<String>();
	}

	public void init(OBEPEngine obep){
		this.obep = obep;
	}
	@Override
	public void setOntology(OWLOntology o) {
		this.ontology = o;
		this.manager = o.getOWLOntologyManager();
		Configuration conf = new Configuration();
		this.reasoner = new Reasoner(conf,ontology);
	}

	@Override
	public void registerQuery(OBEPQuery q) {
		for (OWLEquivalentClassesAxiom eventDef : q.getEventDefinitions()) {
			manager.addAxiom(ontology, eventDef);
			eventDef.namedClasses().forEach(def -> eventDefinitions.add(def.getIRI().toString()));
		}
		reasoner.flush();
	}

	@Override
	public void sendEvent(SemanticEvent se) {
		Set<String> triggeredFilters = new HashSet<String>();
		// add event to ontology
		manager.addAxioms(ontology, se.getAxioms());
		// extract types event
		reasoner.flush();
		
		NodeSet<OWLClass> inferedClasses = reasoner.getTypes(se.getMessage(), false);
		for (OWLClass owlclss : inferedClasses.getFlattened()) {
			String clss = owlclss.getIRI().toString();
			if (eventDefinitions.contains(clss)) {
				triggeredFilters.add(clss);
			}
		}
		se.setTriggeredFilterIRIs(triggeredFilters);

		//send event back to engie
		obep.sendEvent(se);
	}

}
