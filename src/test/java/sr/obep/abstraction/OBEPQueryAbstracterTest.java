package sr.obep.abstraction;

import org.apache.jena.query.Query;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import sr.obep.querying.OBEPQuery;

import java.util.HashSet;
import java.util.Set;


public class OBEPQueryAbstracterTest implements OBEPQuery {
    private static final String ONT_SSNIOT_IRI = "http://IBCNServices.github.io/Accio-Ontology/SSNiot#";
    private static final String ONT_DUL_IRI = "http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#";
    private static final String ONT_SSN_IRI = "http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#";

    private static OWLEquivalentClassesAxiom createTempFilter() {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLClass owlFilter = factory.getOWLClass(ONT_SSNIOT_IRI + "TemperatureFilter");

        OWLObjectProperty hasContextProp = factory.getOWLObjectProperty(ONT_SSNIOT_IRI + "hasContext");
        OWLObjectProperty observedProperty = factory.getOWLObjectProperty(ONT_SSN_IRI + "observedProperty");
        OWLClass propCls = factory.getOWLClass(ONT_SSNIOT_IRI + "Temperature");

        return factory.getOWLEquivalentClassesAxiom(
                owlFilter,
                factory.getOWLObjectSomeValuesFrom(
                        hasContextProp,
                        factory.getOWLObjectSomeValuesFrom(observedProperty, propCls)
                )
        );
    }

    private static OWLEquivalentClassesAxiom createSmokeFilter() {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLClass owlFilter = factory.getOWLClass(ONT_SSNIOT_IRI + "SmokeFilter");

        OWLObjectProperty hasContextProp = factory.getOWLObjectProperty(ONT_SSNIOT_IRI + "hasContext");
        OWLObjectProperty observedProperty = factory.getOWLObjectProperty(ONT_SSN_IRI + "observedProperty");
        OWLClass propCls = factory.getOWLClass(ONT_SSNIOT_IRI + "Smoke");

        return factory.getOWLEquivalentClassesAxiom(
                owlFilter,
                factory.getOWLObjectSomeValuesFrom(
                        hasContextProp,
                        factory.getOWLObjectSomeValuesFrom(observedProperty, propCls)
                )
        );
    }

    public Set<OWLEquivalentClassesAxiom> getEventDefinitions() {
        Set<OWLEquivalentClassesAxiom> eventDefs = new HashSet<OWLEquivalentClassesAxiom>();
        eventDefs.add(createSmokeFilter());
        eventDefs.add(createTempFilter());
        return eventDefs;
    }

    public Query getQ() {
        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + "PREFIX iot: <http://IBCNServices.github.io/Accio-Ontology/SSNiot#>"
                + "PREFIX ssn: <http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#>"
                + "PREFIX dul: <http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#>"
                + " SELECT * "
                + "	WHERE {"
                + "		?location a dul:Place."
                + "		?location dul:hasDataValue ?dataval."
                + "	}";
        Query query = org.apache.jena.query.QueryFactory.create(queryString);
        return query;
    }

}
