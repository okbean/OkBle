package okble.central.client.executor;

import android.bluetooth.BluetoothGatt;

import okble.central.client.ClientOperation;
import okble.central.client.ConnectionParameter;
import okble.central.client.ConnectionPriority;
import okble.central.client.GattOperationResult;
import okble.central.client.Mtu;
import okble.central.client.OkBleEvent;
import okble.central.client.OkBleTask0;
import okble.central.client.exception.GattOperationFailedException;
import okble.central.client.exception.GattOperationTimeoutException;
import okble.central.client.exception.OkBleException;
import okble.central.client.request.Request;
import okble.central.client.request.SetConnectionPriorityRequest;
import okble.central.client.util.EventFilterOfTime;
import okble.central.client.util.OkBleEventFilter;

public final class SetConnectionPriorityExecutor extends RequestExecutor {

    @Override
    public void executeRequest(ClientOperation client, OkBleTask0 task, Request<?> request0) {
        final SetConnectionPriorityRequest request = (SetConnectionPriorityRequest)request0;
        final int connectionPriority = request.connectionPriority().value();
        try {
            final long now = System.currentTimeMillis();
            final boolean success = client.doSetConnectionPriority(connectionPriority);
            if(!success){
                throw new GattOperationFailedException("request connection priority return false!");
            }
            final OkBleEventFilter filter = getFilter(now);
            final OkBleEvent event = pipeReadNotThrow(filter, GATT_OP_TIMEOUT);
            if(event == null){
                throw new GattOperationTimeoutException( "request connection priority timeout!");
            }
            final GattOperationResult result = event.data();
            if(result.status() == BluetoothGatt.GATT_SUCCESS){
                final ConnectionParameter data = result.data();
                task.callOnSuccess(null, false );
            }else{
                throw new GattOperationFailedException( "request mtu operation failed!");
            }
        } catch (OkBleException ex) {
            task.callOnFailed(ex, false);
        }
    }

    private static OkBleEventFilter getFilter(final long time){
        final OkBleEventFilter filter = new EventFilterOfTime(time, OkBleEvent.SET_CONNECTION_PRIORITY);
        return filter;
    }


}
