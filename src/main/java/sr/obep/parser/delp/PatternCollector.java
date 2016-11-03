package sr.obep.parser.delp;

import com.espertech.esper.client.soda.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.sparql.core.Var;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Riccardo on 17/08/16.
 */

@Getter
@Setter
@RequiredArgsConstructor
public class PatternCollector {
    final private String regex = "([0-9]+)\\s*(ms|s|m|h|d)";
    final private Pattern p = Pattern.compile(regex);

    private String operator;
    private Node var;
    private List<PatternCollector> patterns;
    private EventDecl eventDecl;
    private IFDecl eventFilter;
    private boolean bracketed = false;
    private String name;
    private String within;
    private List<IFDecl> global_filters;

    public PatternCollector(PatternCollector pop) {
        addPattern(pop);
        bracketed = true;
    }

    public PatternCollector(String s) {
        this.operator = s;
    }

    public PatternCollector(EventDecl ef, Node var) {
        this.var = var;
        this.eventDecl = ef;
    }

    public PatternCollector(String match, PatternCollector pop) {
        operator = "WITHIN";
        PatternCollector var = new PatternCollector();
        var.setWithin(match); //TODO
        if (patterns == null)
            patterns = new ArrayList<PatternCollector>();
        patterns.add(pop);
        patterns.add(var);

    }

    private boolean isVar() {
        return var != null;
    }

    public void addPattern(PatternCollector p) {
        if (patterns == null)
            patterns = new ArrayList<PatternCollector>();
        patterns.add(p);
    }

    @Override
    public String toString() {
        String s = "";

        if (within != null)
            return "(" + within + ")";

        if (isVar()) {
            return getVarName();
        }

        if (operator != null && ("every".equals(operator.toLowerCase()) || "not".equals(operator.toLowerCase()))) {
            return operator + " (" + patterns.get(0) + ")";
        }

        if (operator == null && patterns.size() == 1) {
            s += bracketed ? "(" : "";
            s += patterns.get(0).toString();
            s += bracketed ? ")" : "";
            return s;
        }

        s += bracketed ? "(" : "";

        PatternCollector pc;
        for (int i = 0; i < patterns.size() - 1; i++) {
            pc = patterns.get(i);

            s += pc.toString();

            s += " " + operator + " ";
        }

        pc = patterns.get(patterns.size() - 1);

        s += pc.toString();

        s += bracketed ? ")" : "";

        return s;
    }

    private TimePeriodExpression toTimeExpr() {
        Matcher matcher = p.matcher(within);
        if (matcher.find()) {
            MatchResult res = matcher.toMatchResult();
            if ("ms".equals(res.group(2))) {
                return Expressions.timePeriod(null, null, null, null, Integer.parseInt(res.group(1)));
            } else if ("s".equals(res.group(2))) {
                return Expressions.timePeriod(null, null, null, Integer.parseInt(res.group(1)), null);
            } else if ("m".equals(res.group(2))) {
                return Expressions.timePeriod(null, null, Integer.parseInt(res.group(1)), null, null);
            } else if ("h".equals(res.group(2))) {
                return Expressions.timePeriod(null, Integer.parseInt(res.group(1)), null, null, null);
            } else if ("d".equals(res.group(2))) {
                return Expressions.timePeriod(Integer.parseInt(res.group(1)), null, null, null, null);
            }
        }
        return null;
    }

    private PatternExpr createFilter(int currentIndex, List<IFDecl> filters) {
        Conjunction andExpr = Expressions.and();
        for (int j = 0; j < filters.size(); j++) {
            if (j == currentIndex) {
                continue;
            }


            Set<Var> vars = new HashSet<Var>(eventFilter.getVars());
            IFDecl id = filters.get(j);
            id.build();
            vars.retainAll(id.getVars());

            for (Var v : vars) {
                if (id.getName() != null) {
                    andExpr.add(Expressions.eqProperty(v.getVarName(), id.getName() + "." + v.getVarName()));
                }
            }
        }

        if (andExpr.getChildren() == null || andExpr.getChildren().isEmpty()) {
            return Patterns.filter(getVarName(), this.name = getVarName() + currentIndex);

        } else if (andExpr.getChildren().size() == 1) {
            return Patterns.filter(Filter.create(getVarName(), andExpr.getChildren().get(0)), getVarName() + currentIndex);
        }

        return Patterns.filter(Filter.create(getVarName(), andExpr), getVarName() + currentIndex);
    }

    public PatternExpr toEPL(List<IFDecl> filters_event) {

        if (var != null) {
            if (filters_event != null && !filters_event.isEmpty()) {
                for (int i = 0; i < filters_event.size(); i++) {
                    eventFilter = filters_event.get(i);
                    eventFilter.build();
                    if (eventFilter.getName() == null)
                        eventFilter.setName(getVarName() + i);
                    if (var.equals(filters_event.get(i).getVar())) {
                        return createFilter(i, filters_event);
                    }
                }
            }

            return Patterns.filter(getVarName(), this.name = getVarName() + 0);
        }

        if (bracketed || (operator == null || operator.isEmpty()) && patterns != null && patterns.size() == 1) {
            return patterns.get(0).toEPL(filters_event);
        }

        PatternExpr pattern = null;
        if (operator != null) {
            if ("within".equals(operator.toLowerCase())) {
                TimePeriodExpression timeExpr = patterns.get(1).toTimeExpr();
                return Patterns.guard("timer", "within", new Expression[]{timeExpr}, patterns.get(0).toEPL(filters_event));
            } else if ("every".equals(operator.toLowerCase())) {
                return Patterns.every(patterns.get(0).toEPL(filters_event));
            } else if ("not".equals(operator.toLowerCase())) {
                return Patterns.not(patterns.get(0).toEPL(filters_event));
            } else if ("->".equals(operator.toLowerCase())) {
                pattern = Patterns.followedBy();
                for (PatternCollector p : patterns) {
                    ((PatternFollowedByExpr) pattern).add(p.toEPL(filters_event));
                }
            } else if ("or".equals(operator.toLowerCase())) {
                pattern = Patterns.or();
                for (PatternCollector p : patterns) {
                    ((PatternOrExpr) pattern).add(p.toEPL(filters_event));
                }
            } else if ("and".equals(operator.toLowerCase())) {
                pattern = Patterns.and();
                for (PatternCollector p : patterns) {
                    ((PatternAndExpr) pattern).add(p.toEPL(filters_event));
                }

            }
        }

        return pattern;

    }


    private String getVarName() {
        if (var != null && var instanceof Node_URI) {
            return var.getURI().replace(var.getNameSpace(), "");
        }
        return var.getName();
    }

    private List<IFDecl> getIfDeclarations() {
        List<IFDecl> ifDeclarations = new ArrayList<IFDecl>();
        if (isVar() && eventDecl != null)
            ifDeclarations.add(eventFilter);

        if (patterns != null) {
            for (PatternCollector p : patterns) {
                ifDeclarations.addAll(p.getIfDeclarations());
            }
        }
        return ifDeclarations;
    }

    public boolean isURI() {
        return var != null && var instanceof Node_URI;
    }
}
