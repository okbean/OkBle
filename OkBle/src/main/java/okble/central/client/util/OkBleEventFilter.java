package okble.central.client.util;

import okble.central.client.OkBleEvent;

public interface OkBleEventFilter {

    boolean accept(OkBleEvent event);
}
