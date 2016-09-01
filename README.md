# DELP
DELP is a  Description Logic syntax for Event Processing.

## Full Grammar

<SPARQL-like prefix declaration>

NamedEventClause -> ['**NAMED**'] '**EVENT**' eventIRI (EventDecl | PatternDecl)
    
EventDecl  ->  [Manchester Syntax Description](https://www.w3.org/TR/owl2-manchester-syntax/#description)

PatternDecl -> '**WHEN**' MatchClause [IFClause]

MatchClause -> '**MATCH**' patternExpr

PatternExpr ->  followedByExpression [**WITHIN** Time_period ]

Time_period ->  INTEGER (ms | s | m | h | d | w)

FollowedByExpr ->  orExpr ((['**NOT**'] FOLLOWED_BY) andExpr)*	

OrExpr -> andExpr (**OR** andExpr)*

AndExpr -> qualifyExpr ( **AND** qualifyExpr)*

EveryOrNotExpr ->  ['**EVERY**' | '**NOT**' ] % ( eventIRI ['**AS**' eventAltIri] | '(' patternExpr ')' )*

IFClause -> '**IF**' '{' '**EVENT**' (eventIRI | Var) FilterExpr '}'

FilterExpr -> '{' ( BGP | FilterClause)* '}'
