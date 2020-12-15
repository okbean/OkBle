package okble.central.client.executor;

import okble.central.client.ClientOperation;
import okble.central.client.OkBleEvent;
import okble.central.client.util.OkBleEventFilter;
import okble.central.client.OkBleTask0;
import okble.central.client.Pipe;
import okble.central.client.exception.OkBleException;
import okble.central.client.request.Request;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class RequestExecutor {

    private Pipe<OkBleEvent> pipe = Pipe.create();

    public abstract void executeRequest(ClientOperation client, OkBleTask0 task, Request<?> request0) throws OkBleException;

    public Pipe<OkBleEvent> pipe(){
        return this.pipe;
    }

    OkBleEvent pipeReadNotThrow(OkBleEventFilter filter, long timeout, TimeUnit unit) {
        final long duration = unit.toMillis(timeout);
        long now = System.currentTimeMillis();
        final long deadline = System.currentTimeMillis() + duration;
        while(deadline > now){
            try{
                OkBleEvent event = pipe().source().read(deadline - now, TimeUnit.MILLISECONDS);
                if(event == null){
                    return null;
                }
                if(filter.accept(event)){
                    return event;
                }
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }
            now = System.currentTimeMillis();
        }
        return null;
    }

    OkBleEvent pipeReadNotThrow(OkBleEventFilter filter, long timeoutInMills){
        return pipeReadNotThrow(filter, timeoutInMills, TimeUnit.MILLISECONDS);
    }

    final static long GATT_OP_TIMEOUT = 32_000L;

    final static UUID CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

}