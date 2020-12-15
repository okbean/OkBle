package okble.central.client.executor;

import android.bluetooth.BluetoothGatt;

import okble.central.client.ClientOperation;
import okble.central.client.util.EventFilterOfTime;
import okble.central.client.GattOperationResult;
import okble.central.client.OkBleEvent;
import okble.central.client.util.OkBleEventFilter;
import okble.central.client.OkBleTask0;
import okble.central.client.Phy;
import okble.central.client.exception.GattOperationFailedException;
import okble.central.client.exception.GattOperationTimeoutException;
import okble.central.client.exception.OkBleException;
import okble.central.client.request.ReadPhyRequest;
import okble.central.client.request.Request;

final class ReadPhyExecutor extends RequestExecutor {

    @Override
    public void executeRequest(ClientOperation client, OkBleTask0 task, Request<?> request0) {
        final ReadPhyRequest request = (ReadPhyRequest)request0;
        try{
            final long now = System.currentTimeMillis();
            final boolean success = client.doReadPhy();
            if(!success){
                throw new GattOperationFailedException( "read phy return false!");
            }
            final OkBleEventFilter filter = getFilter(now);
            final OkBleEvent event = pipeReadNotThrow(filter, GATT_OP_TIMEOUT);
            if(event == null){
                throw new GattOperationTimeoutException(  "read phy operation timeout!");
            }
            final GattOperationResult result = event.data();
            if(result.status() == BluetoothGatt.GATT_SUCCESS){
                final Phy data = result.data();
                task.callOnSuccess(data, false);
            }else{
                throw new GattOperationFailedException( "read phy operation failed!");
            }
        }catch (OkBleException ex){
            task.callOnFailed(ex, false);
        }catch (Exception ex2){
            task.callOnFailed(new OkBleException(ex2), false);
        }
    }



    private static OkBleEventFilter getFilter(final long time){
        final OkBleEventFilter filter = new EventFilterOfTime(time, OkBleEvent.READ_PHY);
        return filter;
    }
}
