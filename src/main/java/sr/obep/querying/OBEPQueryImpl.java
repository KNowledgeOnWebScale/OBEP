package sr.obep.querying;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QueryBuildException;
import org.apache.jena.sparql.core.Prologue;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.graph.NodeConst;
import org.apache.jena.sparql.syntax.*;
import sr.obep.parser.delp.EventCalculusDecl;
import sr.obep.parser.delp.EventDecl;
import sr.obep.parser.sparql.SPARQLQuery;

import java.util.*;

/**
 * Created by Riccardo on 05/08/16.
 */
@Data
@NoArgsConstructor
public class OBEPQueryImpl extends SPARQLQuery implements OBEPQuery {

    protected VarExprList MQLprojectVars = new VarExprList();
    private Map<Node, EventDecl> eventDeclarations;
    private Set<EventDecl> eventCalculusDecls;
    private boolean MQLQyeryStar, emitQuery;
    private boolean MQLresultVarsSet;

    public OBEPQueryImpl(Prologue prologue) {
        super(prologue);
    }


    @Override
    public OBEPQueryImpl addElement(ElementGroup sub) {
        setQueryPattern(sub);

        // TODO UNION?
        if (this.isEmitQuery()) {
            Template template = new Template(buildConstruct(new TripleCollectorBGP(), sub).getBGP());
            setQConstructTemplate(template);
        }
        return this;
    }

    private TripleCollectorBGP buildConstruct(TripleCollectorBGP collector, Element element) {
        if (element instanceof ElementGroup) {
            for (Element e : ((ElementGroup) element).getElements()) {
                buildConstruct(collector, e);
            }
            return collector;
        } else if (element instanceof ElementNamedGraph) {
            collector.addTriple(new Triple(((ElementNamedGraph) element).getGraphNameNode(), NodeConst.nodeRDFType, NodeFactory.createURI("https://www.w3.org/TR/sparql11-service-description/#sd-namedGraphs")));
            Element namedGraph = ((ElementNamedGraph) element).getElement();
            return buildConstruct(collector, namedGraph);
        } else if (element instanceof ElementPathBlock) {
            ElementPathBlock epb = (ElementPathBlock) element;
            List<TriplePath> list = epb.getPattern().getList();
            for (TriplePath triplePath : list) {
                collector.addTriple(triplePath.asTriple());
            }
        }
        return collector;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public OBEPQueryImpl addEventDecl(EventDecl ed) {
        if (eventDeclarations == null)
            eventDeclarations = new HashMap<Node, EventDecl>();
        eventDeclarations.put(ed.getHead(), ed);

        if (ed instanceof EventCalculusDecl)
            return addEventCalculusDecl((EventCalculusDecl) ed);

        return this;

    }

    private OBEPQueryImpl addEventCalculusDecl(EventCalculusDecl ed) {
        if (eventCalculusDecls == null)
            eventCalculusDecls = new HashSet<EventDecl>();
        eventCalculusDecls.add(ed);
        return this;
    }

    private OBEPQueryImpl _addMQLVar(VarExprList varlist, Var v) {
        if (varlist == null)
            varlist = new VarExprList();

        if (varlist.contains(v)) {
            Expr expr = varlist.getExpr(v);
            if (expr != null)
                throw new QueryBuildException(
                        "Duplicate variable (had an expression) in result projection '" + v + "'");
        }
        varlist.add(v);
        return this;
    }

    public OBEPQueryImpl setOBEPQueryStar() {
        this.MQLQyeryStar = true;
        return this;
    }


    public EventDecl getEventDecl(Node peek) {
        if (eventDeclarations == null) {
            return null;
        }
        return eventDeclarations.get(peek);
    }

}
