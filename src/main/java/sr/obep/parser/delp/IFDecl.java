package sr.obep.parser.delp;

import lombok.Data;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Riccardo on 17/08/16.
 */
@Data
public class IFDecl {

    private Node var;
    private Set<Var> vars;
    private ElementGroup clause;
    private String name;

    public IFDecl(Element element) {
        clause = new ElementGroup();
        clause.addElement(element);
    }

    public void addElement(Element e) {
        clause.addElement(e);
    }

    public boolean isVar() {
        return var instanceof Node_Variable;
    }

    public boolean isDecl() {
        return var instanceof Node_URI;
    }

    private void addVar(Var n) {
        if (vars == null)
            vars = new HashSet<Var>();
        vars.add(n);
    }

    public void build() {
        for (Element element : clause.getElements()) {
            if (element instanceof ElementPathBlock) {
                ElementPathBlock e = (ElementPathBlock) element;
                for (TriplePath t : e.getPattern().getList()) {
                    Node n = t.getObject();
                    if (n instanceof Var) {
                        addVar((Var) n);
                    }
                    n = t.getSubject();
                    if (n instanceof Var)
                        addVar((Var) n);

                    n = t.getPredicate();
                    if (n instanceof Var)
                        addVar((Var) n);
                }
            }
        }
    }

    public Query toSPARQL(Set<Var> select) {
        Query q = new Query();
        q.setQuerySelectType();
        for (Var var : select) {
            q.addResultVar(var);
        }
        q.setQueryResultStar(false);
        q.setQueryPattern(clause);
        return q;
    }

    public Query toSPARQL() {
        Query q = new Query();
        q.setQuerySelectType();
        q.setQueryResultStar(true);
        q.setQueryPattern(clause);
        return q;
    }

    public Set<Var> shared(Set<Var> set) {
        Set<Var> intersection = new HashSet<Var>(vars);
        intersection.retainAll(set);
        return intersection;
    }

    public Set<Var> getVars() {
        if (vars == null)
            this.build();
        return vars;
    }
}
