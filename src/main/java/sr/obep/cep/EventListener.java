package sr.obep.cep;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;
import sr.obep.SemanticEvent;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Created by Riccardo on 03/11/2016.
 */
@Log4j
public class EventListener implements UpdateListener {
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        // EventBean event = newEvents[0];
        log.info("EventListener update");
        List<SemanticEvent> activatedEvents = new ArrayList<SemanticEvent>();
        if (newEvents != null) {
            log.info("New events: (" + this + ") " + newEvents.length);
            for (EventBean e : newEvents) {
                if (e instanceof MapEventBean) {
                    MapEventBean meb = (MapEventBean) e;
                    for (Entry<String, Object> entry : meb.getProperties().entrySet()) {
                        if (entry.getValue() instanceof MapEventBean) {
                            MapEventBean mapEvent = (MapEventBean) entry.getValue();
                            if (mapEvent.getProperties().containsKey("content")) {
                                SemanticEvent message = (SemanticEvent) mapEvent.getProperties().get("content");
                                activatedEvents.add(message);

                            }
                        }
                    }
                    log.info("" + meb.getProperties());

                }
            }
        }
        if (oldEvents != null) {
            log.info("Old events:" + oldEvents.length);

        }
        for (SemanticEvent message : activatedEvents) {
            //service.receive(message);

        }

    }
}
