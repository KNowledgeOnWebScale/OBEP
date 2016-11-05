package sr.obep.abstration;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.stream.Collectors;


public class Test {

    private static final String ONT_IRI = "http://pbonte.github.io/ontology/ssniot2.owl#";
    private static final String ONT_SSNIOT_IRI = "http://IBCNServices.github.io/Accio-Ontology/SSNiot.owl#";
    private static final String ONT_DUL_IRI = "http://IBCNServices.github.io/Accio-Ontology/ontologies/DUL.owl#";
    private static final String ONT_SSN_IRI = "http://IBCNServices.github.io/Accio-Ontology/ontologies/ssn#";

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        OWLEquivalentClassesAxiom ax = createSmokeFilter();
        System.out.println(ax);
        System.out.println(ax.namedClasses().collect(Collectors.toSet()));
    }

    private static OWLEquivalentClassesAxiom createSmokeFilter() {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClass owlFilter = factory.getOWLClass(ONT_IRI + "SmokeFilter");
        //hasContext some (observedProperty some Somke)
        OWLObjectProperty hasContextProp = factory.getOWLObjectProperty(ONT_SSNIOT_IRI + "hasContext");
        OWLObjectProperty observedProperty = factory.getOWLObjectProperty(ONT_SSN_IRI + "observedProperty");
        OWLClass propCls = factory.getOWLClass(ONT_IRI + "Smoke");


        return factory.getOWLEquivalentClassesAxiom(
                owlFilter,
                factory.getOWLObjectSomeValuesFrom(
                        hasContextProp,
                        factory.getOWLObjectSomeValuesFrom(observedProperty, propCls)
                )
        );
    }
}
