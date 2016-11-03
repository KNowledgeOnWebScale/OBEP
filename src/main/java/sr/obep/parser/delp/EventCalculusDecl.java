package sr.obep.parser.delp;

import com.espertech.esper.client.soda.*;
import lombok.Data;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Riccardo on 17/08/16.
 */
@Data
public class EventCalculusDecl extends EventDecl {

    private PatternCollector expr;
    private List<IFDecl> filter_events;
    private List<IFDecl> filters_var;

    public EventCalculusDecl(Node head) {
        super(head);
    }

    public Set<Var> getJoinVariables() {
        Set<Var> joinVariables = null;
        if (filter_events != null)
            for (IFDecl f : filter_events) {
                if (joinVariables == null) {
                    joinVariables = new HashSet<Var>(f.getVars());
                }
                joinVariables.retainAll(f.getVars());
            }
        if (filters_var != null)
            for (IFDecl f : filters_var) {
                if (joinVariables == null) {
                    joinVariables = new HashSet<Var>(f.getVars());
                }
                joinVariables.addAll(f.getVars());
            }
        return joinVariables;
    }

    public EPStatementObjectModel toEpl() {
        EPStatementObjectModel model = new EPStatementObjectModel();
        model.setSelectClause(SelectClause.createWildcard());
        PatternExpr pattern = expr.toEPL(filter_events);
        model.setFromClause(FromClause.create(PatternStream.create(pattern)));
        return model;
    }

    public void addEventFilter(IFDecl pop) {
        if (filter_events == null) {
            filter_events = new ArrayList<IFDecl>();
        }
        if (filters_var == null) {
            filters_var = new ArrayList<IFDecl>();
        }
        if (pop.isDecl()) {
            filter_events.add(pop);
            for (IFDecl fe : filters_var) {
                for (Element element : fe.getClause().getElements()) {
                    if (!pop.getClause().getElements().contains(element)) {
                        pop.addElement(element);
                    }
                }
            }
        } else if (pop.isVar()) {
            filters_var.add(pop);
            for (IFDecl fe : filter_events) {
                for (Element element : pop.getClause().getElements()) {
                    if (!fe.getClause().getElements().contains(element)) {
                        fe.addElement(element);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "EventCalculusDecl {" + "expr=" + expr + '}';
    }

}
