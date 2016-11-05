package sr.obep.cep;

import sr.obep.OBEPEngine;
import sr.obep.SemanticEvent;
import sr.obep.querying.OBEPQuery;

public interface EventProcessor {

    public void init(OBEPEngine obep);

    public void registerQuery(OBEPQuery q);

    public void sendEvent(SemanticEvent se);

}