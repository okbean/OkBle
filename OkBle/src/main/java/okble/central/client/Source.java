package okble.central.client;

import java.util.concurrent.TimeUnit;


public interface Source<T> {

    T read(long timeout, TimeUnit unit) throws InterruptedException;

}
