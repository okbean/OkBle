package okble.central.client.util;

import okble.central.client.OkBleEvent;

public final class EventFilterOfTime implements OkBleEventFilter{

    private int[] events;
    private long time;

    public EventFilterOfTime(long time, final int... events){
        this.events = events;
        this.time = time;
    }

    public static EventFilterOfTime from(long time, int... events){
        return new EventFilterOfTime(time, events);
    }

    @Override
    public boolean accept(OkBleEvent event) {
        if(events == null || event == null){
            return false;
        }
        for(int val : events){
            if(val == event.value() && event.time() > this.time){
                return true;
            }
        }
        return false;
    }
}
