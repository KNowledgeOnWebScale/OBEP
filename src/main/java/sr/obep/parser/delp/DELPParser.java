package sr.obep.parser.delp;

import org.apache.jena.graph.Node;
import org.parboiled.Rule;
import sr.obep.parser.sparql.SPARQLParser;
import sr.obep.querying.OBEPQueryImpl;

/**
 * Created by Riccardo on 09/08/16.
 */
public class DELPParser extends SPARQLParser {

    @Override
    public Rule Query() {
        return Sequence(push(new OBEPQueryImpl()), WS(), Prologue(),
                OneOrMore(CreateEventClause()), EOI);
    }

    public Rule CreateEventClause() {
        return Sequence(NAMED(), EVENT(), IriRef(),
                FirstOf(
                        Sequence(OPEN_CURLY_BRACE(), push(new EventCalculusDecl((Node) pop())), EventCalculusDeclaration(), CLOSE_CURLY_BRACE()),
                        Sequence(push(new DLEventDecl((Node) pop())), DLEventDeclaration(), setDLRule()))
                , pushQuery(((OBEPQueryImpl) popQuery(-1)).addEventDecl((EventDecl) pop())), Optional(DOT()));
    }

    public Rule DLEventDeclaration() {
        return ZeroOrMore(Sequence(TestNot(FirstOf(EVENT(), NAMED())), ANY), WS());
    }

    public Rule EventCalculusDeclaration() {
        return Sequence(MatchClause(), Optional(IfClause()));
    }

    public Rule EventConstructClause() {
        //TODO
        return FirstOf(Sequence(TriplesTemplate(), addTemplateAndPatternToQuery()),
                Sequence(OPEN_CURLY_BRACE(), ConstructTemplate(), addTemplateToQuery(), CLOSE_CURLY_BRACE()));
    }

    public Rule MatchClause() {
        return Sequence(MATCH(), PatternExpression(), addPatternExpression());
    }

    public Rule IfClause() {
        return FirstOf(
                Sequence(IF(), EventFilterDecl()),
                Sequence(IF(), OPEN_CURLY_BRACE(), ZeroOrMore(EventFilterDecl()), CLOSE_CURLY_BRACE()));
    }

    public Rule EventFilterDecl() {
        return Sequence(EVENT(), VarOrIRIref(), OPEN_CURLY_BRACE(), EventClause(), addEventFilter((IFDecl) pop(), (Node) pop()), CLOSE_CURLY_BRACE(), WS());
    }

    public Rule EventClause() {
        return Sequence(TriplesBlock(), push(new IFDecl(popElement())));
    }


    public Rule PatternExpression() {
        return Sequence(FollowedByExpression(), Optional(Sequence(WITHIN(), LPAR(), TimeConstrain(),
                push(new PatternCollector(match(), (PatternCollector) pop())), RPAR())));
    }

    public Rule FollowedByExpression() {
        return Sequence(OrExpression(), ZeroOrMore(FirstOf(FOLLOWED_BY(), Sequence(NOT(), FOLLOWED_BY())),
                enclose(trimMatch()), OrExpression(), addExpression()));
    }

    public Rule OrExpression() {
        return Sequence(AndExpression(), ZeroOrMore(OR_(), enclose(trimMatch()), AndExpression(), addExpression()));
    }

    public Rule AndExpression() {
        return Sequence(QualifyExpression(),
                ZeroOrMore(AND_(), enclose(trimMatch()), QualifyExpression(), addExpression()));
    }

    public Rule QualifyExpression() {
        return FirstOf(Sequence(FirstOf(EVERY(), NOT()), push(new PatternCollector(trimMatch())), GuardPostFix(),
                addExpression()), GuardPostFix());
    }

    public Rule GuardPostFix() {
        return FirstOf(
                Sequence(LPAR(), PatternExpression(), RPAR(), push(new PatternCollector((PatternCollector) pop()))),
                Sequence(VarOrIRIref(), push(((OBEPQueryImpl) getQuery(-1)).getEventDecl((Node) peek())),
                        push(new PatternCollector((EventDecl) pop(), (Node) pop()))));

    }


    //Utility methods

    @Override
    public boolean startSubQuery(int i) {
        return push(new OBEPQueryImpl(getQuery(i).getQ().getPrologue()));
    }

    // MQL
    public boolean addEventFilter(IFDecl pop, Node node) {
        EventCalculusDecl peek = (EventCalculusDecl) peek();
        pop.setVar(node);
        peek.addEventFilter(pop);

        return true;
    }

    public boolean addPatternExpression() {
        ((EventCalculusDecl) peek(1)).setExpr((PatternCollector) pop());
        return true;
    }

    public boolean setDLRule() {
        ((DLEventDecl) peek()).setBody(match());
        return true;
    }

    public boolean addExpression() {
        PatternCollector inner = (PatternCollector) pop();
        PatternCollector outer = (PatternCollector) pop();
        outer.addPattern(inner);
        return push(outer);
    }

    public boolean enclose(String operator) {
        PatternCollector inner = (PatternCollector) pop();

        if (inner.isBracketed() || inner.getOperator() == null || !operator.equals(inner.getOperator())) {
            PatternCollector outer = new PatternCollector(operator);
            outer.setOperator(operator);
            outer.addPattern(inner);
            return push(outer);
        }
        return push(inner);

    }

    //MQL Syntax Extensions

    // MQL
    public Rule EVENT() {
        return StringIgnoreCaseWS("EVENT");
    }

    public Rule CREATE() {
        return StringIgnoreCaseWS("CREATE");
    }

    public Rule AND_() {
        return StringIgnoreCaseWS("AND");
    }

    public Rule OR_() {
        return StringIgnoreCaseWS("OR");
    }

    public Rule FOLLOWED_BY() {
        return FirstOf(StringWS("->"), StringIgnoreCaseWS("FOLLOWED_BY"),
                Sequence(StringIgnoreCaseWS("FOLLOWED"), BY()));
    }

    public Rule MATCH() {
        return StringIgnoreCaseWS("MATCH");
    }

    public Rule EVERY() {
        return StringIgnoreCaseWS("EVERY");
    }

    public Rule WITHIN() {
        return StringIgnoreCaseWS("WITHIN");
    }

    public Rule TIME_UNIT() {
        return Sequence(FirstOf("ms", 's', 'm', 'h', 'd'), WS());
    }

    public Rule TimeConstrain() {
        return Sequence(INTEGER(), TIME_UNIT());
    }
}