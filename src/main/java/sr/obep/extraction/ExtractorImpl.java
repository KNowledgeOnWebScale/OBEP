package sr.obep.extraction;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import sr.obep.OBEPEngine;
import sr.obep.SemanticEvent;
import sr.obep.querying.OBEPQuery;
import sr.obep.querying.OBEPQueryImpl;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by pbonte on 03/11/2016.
 */
@Slf4j
public class ExtractorImpl implements Extractor {

    private OBEPEngine obep;
    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private List<Query> queries;

    public ExtractorImpl() {
        queries = new ArrayList<Query>();
        manager = OWLManager.createOWLOntologyManager();
    }

    private static ResultSet sparqlQuery(Model model, Query query) {
        QueryExecution qExec = QueryExecutionFactory.create(query, model);
        return qExec.execSelect();
    }

    protected static List<Map<String, String>> exec(Model model, Query query) {
        List<Map<String, String>> results = new ArrayList<Map<String, String>>();

        ResultSet result = sparqlQuery(model, query);
        while (result != null && result.hasNext()) {
            Map<String, String> tempMap = new HashMap<String, String>();

            QuerySolution solution = result.next();
            Iterator<String> it = solution.varNames();

            // Iterate over all results
            while (it.hasNext()) {
                String varName = it.next();
                String varValue = solution.get(varName).toString();

                tempMap.put(varName, varValue);

            }
            // Only add if we have some objects in temp map
            if (tempMap.size() > 0) {
                results.add(tempMap);
            }
        }

        return results;
    }

    protected static OWLOntology getOWLOntology(final Model model) {
        OWLOntology ontology;
        try (PipedInputStream is = new PipedInputStream(); PipedOutputStream os = new PipedOutputStream(is)) {
            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    model.write(os, "TURTLE", null);
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            ontology = man.loadOntologyFromOntologyDocument(is);
            return ontology;
        } catch (Exception e) {
            throw new RuntimeException("Could not convert JENA API model to OWL API ontology.", e);
        }
    }

    @Override
    public void init(OBEPEngine obep) {
        this.obep = obep;

    }

    @Override
    public void registerQuery(OBEPQuery q) {

    }

    @Override
    public void setOntology(OWLOntology o) {

    }

    @Override
    public void sendEvent(SemanticEvent se) {
        Map<String, String> props = new HashMap<String, String>();
        for (Query q : queries) {
            List<Map<String, String>> results = query(se.getAxioms(), q);
            for (Map<String, String> resultItem : results) {
                for (Entry<String, String> entry : resultItem.entrySet()) {
                    props.put(entry.getKey(), entry.getValue());
                }
            }
        }
        se.setProperties(props);
        obep.sendEvent(se);
    }

    public List<Map<String, String>> query(Set<OWLAxiom> event, Query query) {
        OWLOntology tempOnt;
        List<Map<String, String>> results = null;
        try {
            tempOnt = manager.createOntology();
            manager.addAxioms(tempOnt, event);
            OntModel tempModel = getOntologyModel(manager, tempOnt);
            results = exec(tempModel, query);
        } catch (OWLOntologyCreationException e) {
            log.error("Unable to create ontology");

        }

        return results;
    }

    protected OntModel getOntologyModel(OWLOntologyManager manager, OWLOntology ontology) {
        OntModel noReasoningModel = null;

        noReasoningModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        noReasoningModel.getDocumentManager().setProcessImports(false);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            manager.saveOntology(ontology, out);
        } catch (OWLOntologyStorageException e) {
            log.error("Unable to write ontology to stream");
        }

        try {
            noReasoningModel.read(new ByteArrayInputStream(out.toByteArray()), "RDF/XML");
        } catch (Exception e) {
            log.error("Problems reading stream. Might be ignored");
        }

        return noReasoningModel;
    }
}
