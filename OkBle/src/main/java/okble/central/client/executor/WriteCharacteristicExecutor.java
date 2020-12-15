package okble.central.client.executor;

import android.bluetooth.BluetoothGatt;
import android.os.SystemClock;

import okble.central.client.ClientOperation;
import okble.central.client.util.EventFilters;
import okble.central.client.GattOperationResult;
import okble.central.client.OkBleEvent;
import okble.central.client.util.OkBleEventFilter;
import okble.central.client.OkBleTask0;
import okble.central.client.PacketSender;
import okble.central.client.WriteType;
import okble.central.client.exception.GattOperationFailedException;
import okble.central.client.exception.GattOperationTimeoutException;
import okble.central.client.exception.OkBleException;
import okble.central.client.request.Request;
import okble.central.client.request.WriteCharacteristicRequest;

import java.util.UUID;

final class WriteCharacteristicExecutor extends RequestExecutor {

    @Override
    public void executeRequest(ClientOperation client, OkBleTask0 task, Request<?> request0) {
        final WriteCharacteristicRequest request = (WriteCharacteristicRequest)request0;
        final UUID service = request.service();
        final UUID characteristic = request.characteristic();
        final WriteType writeType = request.writeType();
        final PacketSender sender = request.packetSenderFactory().newPacketSender();
        boolean first = true;
        while (sender.hasNext()){
            try{
//              SystemClock.sleep(first ? request.txDelay() : request.txInterval());
                first = false;
                final byte[] packet = sender.nextPacket();
                checkPacket(packet);
                final long now = System.currentTimeMillis();
                final boolean success = client.doWriteCharacteristic(service, characteristic,packet, writeType.value());
                if(!success){
                    throw new GattOperationFailedException("write characteristic return false!");
                }
                final OkBleEventFilter filter = getFilter(now, service, characteristic);
                final OkBleEvent event = pipeReadNotThrow(filter, GATT_OP_TIMEOUT);
                if(event == null){
                    throw new GattOperationTimeoutException( "write characteristic operation timeout!");
                }
                final GattOperationResult result = event.data();
                if(result.status() == BluetoothGatt.GATT_SUCCESS){
                    continue;
                }else{
                    throw new GattOperationFailedException( "write characteristic operation failed!");
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


    private static OkBleEventFilter getFilter(final long time, final UUID service, final UUID characteristic ){
        final OkBleEventFilter filter1 = EventFilters.getFilter(time, OkBleEvent.WRITE_CHARACTERISTIC);
        final OkBleEventFilter filter2 = EventFilters.getFilter(OkBleEvent.WRITE_CHARACTERISTIC,service,characteristic) ;
        final OkBleEventFilter filter = EventFilters.andFilter(filter1, filter2);
        return filter;
    }



}
