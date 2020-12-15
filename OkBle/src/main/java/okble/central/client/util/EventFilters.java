package okble.central.client.util;


import android.bluetooth.BluetoothGatt;

import java.util.UUID;

import okble.central.client.CharacteristicData;
import okble.central.client.DescriptorData;
import okble.central.client.GattOperationResult;
import okble.central.client.OkBleEvent;

public final class EventFilters {

    public static OkBleEventFilter getFilter(final long time, final int... events){
        final OkBleEventFilter filter = new EventFilterOfTime(time, events);
        return filter;
    }


    public static OkBleEventFilter andFilter(OkBleEventFilter... filters){
        return new AndFilter(filters);
    }


    public static OkBleEventFilter getFilter(final int eventValue, final UUID service, final UUID characteristic){
         final OkBleEventFilter filter = new OkBleEventFilter(){
            @Override
            public boolean accept(OkBleEvent event) {
                if(event == null){
                    return false;
                }
                if(event.value() == eventValue){
                    final GattOperationResult result = event.data();
                    if(result.status() == BluetoothGatt.GATT_SUCCESS){
                        final CharacteristicData data = result.data(CharacteristicData.class);
                        if(data != null &&
                                (!service.equals(data.service())  || !characteristic.equals(data.characteristic()))){
                            return false;
                        }
                    }
                }
                return true;
            }
        };
        return filter;
    }




    public static OkBleEventFilter getFilter(final int eventValue, final UUID service, final UUID characteristic,final UUID descriptor){
        final OkBleEventFilter filter = new OkBleEventFilter(){
            @Override
            public boolean accept(OkBleEvent event) {
                if(event == null){
                    return false;
                }
                if(event.value() == eventValue){
                    final GattOperationResult result = event.data();
                    if(result.status() == BluetoothGatt.GATT_SUCCESS){
                        final DescriptorData data = result.data(DescriptorData.class);
                        if(data != null &&
                                (!service.equals(data.service())  ||
                                        !characteristic.equals(data.characteristic()) ||
                                        !descriptor.equals(data.descriptor()))){
                            return false;
                        }
                    }
                }
                return true;
            }
        };
        return filter;
    }







}
