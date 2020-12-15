package okble.central.client.executor;

import android.bluetooth.BluetoothGatt;

import okble.central.client.ClientOperation;
import okble.central.client.util.EventFilterOfTime;
import okble.central.client.GattOperationResult;
import okble.central.client.OkBleEvent;
import okble.central.client.util.OkBleEventFilter;
import okble.central.client.OkBleTask0;
import okble.central.client.Rssi;
import okble.central.client.exception.GattOperationFailedException;
import okble.central.client.exception.GattOperationTimeoutException;
import okble.central.client.exception.OkBleException;
import okble.central.client.request.ReadRemoteRssiRequest;
import okble.central.client.request.Request;

final class ReadRemoteRssiExecutor extends RequestExecutor {

    @Override
    public void executeRequest(ClientOperation client, OkBleTask0 task, Request<?> request0) {
        final ReadRemoteRssiRequest request = (ReadRemoteRssiRequest)request0;
        try{
            final long now = System.currentTimeMillis();
            final boolean success = client.doReadRemoteRssi();
            if(!success){
                throw new GattOperationFailedException( "read remote rssi return false!");
            }
            final OkBleEventFilter filter = getFilter(now);
            final OkBleEvent event = pipeReadNotThrow(filter, GATT_OP_TIMEOUT);
            if(event == null){
                throw new GattOperationTimeoutException( "read remote rssi operation timeout!");
            }
            final GattOperationResult result = event.data();
            if(result.status() == BluetoothGatt.GATT_SUCCESS){
                final Rssi data = result.data();
                task.callOnSuccess(data, false);
            }else{
                throw new GattOperationFailedException( "read remote rssi operation failed!");
            }
        }catch (OkBleException ex){
            task.callOnFailed(ex, false);
        }catch (Exception ex2){
            task.callOnFailed(new OkBleException(ex2), false);
        }
    }



    private static OkBleEventFilter getFilter(final long time){
        final OkBleEventFilter filter = new EventFilterOfTime(time, OkBleEvent.READ_REMOTE_RSSI);
        return filter;
    }
}
