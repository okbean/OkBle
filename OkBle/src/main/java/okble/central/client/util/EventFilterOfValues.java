package okble.central.client.util;

import okble.central.client.OkBleEvent;

public final class EventFilterOfValues implements OkBleEventFilter{

    private int[] events;


    public EventFilterOfValues(final int... events){
        this.events = events;
    }


    public static EventFilterOfValues from(int... events){
        return new EventFilterOfValues(events);
    }

    @Override
    public boolean accept(OkBleEvent event) {
        if(events == null || event == null){
            return false;
        }
        for(int val : events){
            if(val == event.value()){
                return true;
            }
        }
        return false;
    }
}
