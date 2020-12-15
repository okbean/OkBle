package okble.central.client.executor;

import android.bluetooth.BluetoothGatt;

import okble.central.client.ClientOperation;
import okble.central.client.util.EventFilters;
import okble.central.client.GattOperationResult;
import okble.central.client.OkBleEvent;
import okble.central.client.util.OkBleEventFilter;
import okble.central.client.OkBleTask0;
import okble.central.client.PacketSender;
import okble.central.client.exception.GattOperationFailedException;
import okble.central.client.exception.GattOperationTimeoutException;
import okble.central.client.exception.OkBleException;
import okble.central.client.request.Request;
import okble.central.client.request.WriteDescriptorRequest;

import java.util.UUID;

final class WriteDescriptorExecutor extends RequestExecutor {

    @Override
    public void executeRequest(ClientOperation client, OkBleTask0 task, Request<?> request0) {
        final WriteDescriptorRequest request = (WriteDescriptorRequest)request0;
        final UUID service = request.service();
        final UUID characteristic = request.characteristic();
        final UUID descriptor = request.descriptor();
        final PacketSender sender = request.packetSenderFactory().newPacketSender();
        while (sender.hasNext()){
            try{
                final byte[] packet = sender.nextPacket();
                checkPacket(packet);
                final long now = System.currentTimeMillis();
                final boolean success = client.doWriteDescriptor(service, characteristic, descriptor, packet);
                if(!success){
                    throw new GattOperationFailedException( "write descriptor return false!");
                }
                final OkBleEventFilter filter = getFilter(now, service, characteristic, descriptor);
                final OkBleEvent event = pipeReadNotThrow(filter, GATT_OP_TIMEOUT);
                if(event == null){
                    throw new GattOperationTimeoutException(  "write descriptor operation timeout!");
                }
                final GattOperationResult result = event.data();
                if(result.status() == BluetoothGatt.GATT_SUCCESS){
                    continue;
                }else{
                    throw new GattOperationFailedException( "write descriptor operation failed!");
                }
            }catch (OkBleException ex){
                task.callOnFailed(ex, false);
                break;
            }catch (Exception ex2){
                task.callOnFailed(new OkBleException(ex2), false);
                break;
            }
        }
        task.callOnSuccess(null, false );
        try{
            sender.reset();
        }catch (Exception ex){
        }
    }



    private static void checkPacket(final byte[] packet) throws OkBleException {
        if(packet == null || packet.length == 0){
            throw new OkBleException("packet is null or zero length!");
        }
    }

    private static OkBleEventFilter getFilter(final long time, final UUID service, final UUID characteristic, final UUID descriptor ){
        final OkBleEventFilter filter1 = EventFilters.getFilter(time, OkBleEvent.WRITE_DESCRIPTOR);
        final OkBleEventFilter filter2 = EventFilters.getFilter(OkBleEvent.WRITE_DESCRIPTOR,service,characteristic,descriptor) ;
        final OkBleEventFilter filter = EventFilters.andFilter(filter1, filter2);
        return filter;
    }
}
