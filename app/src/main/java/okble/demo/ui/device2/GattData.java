package okble.demo.ui.device2;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GattData  {

    public final static int SERVICE = 1;
    public final static int CHARACTERISTIC = 2;
    public final static int DESCRIPTOR = 3;

    private Object data;
    private int type;
    private GattData(final int type, final Object data){
        this.data = data;
        this.type = type;
    }

    public int type(){
        return this.type;
    }

    public UUID uuid(){
        if(isService()){
            return ((BluetoothGattService)data).getUuid();
        }else if(isCharacteristic()){
            return ((BluetoothGattCharacteristic)data).getUuid();
        }else if(isDescriptor()){
            return ((BluetoothGattDescriptor)data).getUuid();
        }
        return null;
    }

    public static GattData of(BluetoothGattService service){
        return new GattData(SERVICE, service);
    }

    public static GattData of(BluetoothGattCharacteristic characteristic){
        return new GattData(CHARACTERISTIC, characteristic);
    }

    public static GattData of(BluetoothGattDescriptor descriptor){
        return new GattData(DESCRIPTOR, descriptor);
    }

    public boolean isService(){
        return this.type == SERVICE;
    }

    public boolean isCharacteristic(){
        return this.type == CHARACTERISTIC;
    }

    public boolean isDescriptor(){
        return this.type == DESCRIPTOR;
    }

    public <T>  T get(){
        return (T)data;
    }

    public static List<GattData> from(List<BluetoothGattService> services){
        if(services == null || services.size() <= 0){
            return Collections.emptyList();
        }
        final List<GattData> value = new ArrayList<>(services.size() * 4);
        for(BluetoothGattService service : services){
            if(service == null){
                continue;
            }
            final GattData s = of(service);
            value.add(s);
            final List<BluetoothGattCharacteristic> cList = service.getCharacteristics();
            addCharacteristic(value, cList);
        }
        return value;
    }

    private static void addCharacteristic(final List<GattData> value, List<BluetoothGattCharacteristic> list){
        if(list == null || list.size() <= 0){
            return;
        }
        for(BluetoothGattCharacteristic characteristic : list){
            if(characteristic == null){
                continue;
            }
            final GattData c = of(characteristic);
            value.add(c);

            final List<BluetoothGattDescriptor> dList = characteristic.getDescriptors();
            addDescriptor(value, dList);
        }
    }

    private static void addDescriptor(final List<GattData> value, List<BluetoothGattDescriptor> list){
        if(list == null || list.size() <= 0){
            return;
        }
        for(BluetoothGattDescriptor descriptor : list){
            if(descriptor == null){
                continue;
            }
            final GattData c = of(descriptor);
            value.add(c);
        }
    }

}
