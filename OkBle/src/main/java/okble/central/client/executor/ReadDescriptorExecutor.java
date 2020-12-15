package okble.central.client.executor;

import android.bluetooth.BluetoothGatt;

import okble.central.client.ClientOperation;
import okble.central.client.DescriptorData;
import okble.central.client.util.EventFilters;
import okble.central.client.GattOperationResult;
import okble.central.client.OkBleEvent;
import okble.central.client.util.OkBleEventFilter;
import okble.central.client.OkBleTask0;
import okble.central.client.exception.GattOperationFailedException;
import okble.central.client.exception.GattOperationTimeoutException;
import okble.central.client.exception.OkBleException;
import okble.central.client.request.ReadDescriptorRequest;
import okble.central.client.request.Request;

import java.util.UUID;

public final class ReadDescriptorExecutor extends RequestExecutor {

    @Override
    public void executeRequest(ClientOperation client, OkBleTask0 task, Request<?> request0) {
        final ReadDescriptorRequest request = (ReadDescriptorRequest)request0;
        final UUID service = request.service();
        final UUID characteristic = request.characteristic();
        final UUID descriptor = request.descriptor();

        try{
            final long now = System.currentTimeMillis();
            final boolean success = client.doReadDescriptor(service, characteristic, descriptor);
            if(!success){
                throw new GattOperationFailedException( "read descriptor return false!");
            }
            final OkBleEventFilter filter = getFilter(now, service, characteristic, descriptor);
            final OkBleEvent event = pipeReadNotThrow(filter, GATT_OP_TIMEOUT);
            if(event == null){
                throw new GattOperationTimeoutException( "read descriptor operation timeout!");
            }
            final GattOperationResult result = event.data();
            if(result.status() == BluetoothGatt.GATT_SUCCESS){
                final DescriptorData descriptorData = result.data();
                final byte[] packet = descriptorData.data();
                task.callOnSuccess(packet, false);
            }else{
                throw new GattOperationFailedException( "read descriptor operation failed!");
            }
        }catch (OkBleException ex){
            task.callOnFailed(ex, false);
        }catch (Exception ex2){
            task.callOnFailed(new OkBleException(ex2), false);
        }
    }



    private static OkBleEventFilter getFilter(final long time, final UUID service, final UUID characteristic, final UUID descriptor ){
        final OkBleEventFilter filter1 = EventFilters.getFilter(time, OkBleEvent.READ_DESCRIPTOR);
        final OkBleEventFilter filter2 = EventFilters.getFilter(OkBleEvent.READ_DESCRIPTOR,service,characteristic,descriptor) ;
        final OkBleEventFilter filter = EventFilters.andFilter(filter1, filter2);
        return filter;
    }
}
