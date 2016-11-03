package sr.obep.extraction;

import junit.framework.TestCase;
import sr.obep.abstraction.AbstracterTest;
import sr.obep.abstraction.OBEPQueryAbstracterTest;
import sr.obep.abstraction.OBEPTestEngine;

public class ExtractionTest extends TestCase{

	private Extractor extractor;
	private OBEPTestEngine engine;
	public void setUp(){
		extractor = new ExtractorImpl();
		engine = new OBEPTestEngine();
		extractor.init(engine);
		extractor.registerQuery(new OBEPQueryAbstracterTest());
	}
	
	public void testQuery(){
		extractor.sendEvent(AbstracterTest.createTempEvent());
		System.out.println(engine.getReceivedEvent().getProperties());
		assertNotNull(engine.getReceivedEvent().getProperties());
	}
}
