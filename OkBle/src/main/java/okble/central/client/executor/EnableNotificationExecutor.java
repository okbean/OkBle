package okble.central.client.executor;

import android.bluetooth.BluetoothGatt;

import okble.central.client.ClientOperation;
import okble.central.client.util.EventFilters;
import okble.central.client.GattOperationResult;
import okble.central.client.NotificationType;
import okble.central.client.OkBleEvent;
import okble.central.client.util.OkBleEventFilter;
import okble.central.client.OkBleTask0;
import okble.central.client.exception.GattOperationFailedException;
import okble.central.client.exception.GattOperationTimeoutException;
import okble.central.client.exception.OkBleException;
import okble.central.client.request.EnableNotificationRequest;
import okble.central.client.request.Request;

import java.util.UUID;

final class EnableNotificationExecutor extends RequestExecutor {

    @Override
    public void executeRequest(ClientOperation client, OkBleTask0 task, Request<?> request0) {
        final EnableNotificationRequest request = (EnableNotificationRequest)request0;
        final UUID service = request.service();
        final UUID characteristic = request.characteristic();
        final NotificationType type = request.notificationType();
        try{
            final long now = System.currentTimeMillis();
            final boolean success;
            if(type == NotificationType.Indication){
                success = client.doEnableIndication(service, characteristic);
            }else {
                success = client.doEnableNotification(service, characteristic);
            }
            if(!success){
                throw new GattOperationFailedException("write descriptor return false!");
            }

            final OkBleEventFilter filter = getFilter(now, service, characteristic, CCCD_UUID);
            final OkBleEvent event = pipeReadNotThrow(filter, GATT_OP_TIMEOUT);
            if(event == null){
                throw new GattOperationTimeoutException( "write descriptor operation timeout!");
            }
            final GattOperationResult result = event.data();
            if(result.status() == BluetoothGatt.GATT_SUCCESS){
                task.callOnSuccess(null, false);
            }else{
                throw new GattOperationFailedException( "write descriptor operation failed!");
            }
        }catch (OkBleException ex){
            task.callOnFailed(ex, false);
        }catch (Exception ex2){
            task.callOnFailed(new OkBleException(ex2), false);
        }
    }

    private static OkBleEventFilter getFilter(final long time, final UUID service, final UUID characteristic, final UUID descriptor ){
        final OkBleEventFilter filter1 = EventFilters.getFilter(time, OkBleEvent.WRITE_DESCRIPTOR);
        final OkBleEventFilter filter2 = EventFilters.getFilter(OkBleEvent.WRITE_DESCRIPTOR,service,characteristic,descriptor) ;
        final OkBleEventFilter filter = EventFilters.andFilter(filter1, filter2);
        return filter;
    }
}
