package okble.central.client;

import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

import okble.central.client.util.Hex;

public final class DescriptorData {

    private final UUID service;
    private final UUID characteristic;
    private UUID descriptor;
    private final byte[] data;


    private DescriptorData(UUID service, UUID characteristic,UUID descriptor, byte[] data){
        this.service = service;
        this.characteristic = characteristic;
        this.descriptor = descriptor;
        this.data = data;
    }


    public UUID service() {
        return service;
    }

    public UUID characteristic() {
        return characteristic;
    }

    public UUID descriptor() {
        return descriptor;
    }

    public byte[] data() {
        return data;
    }


    private DescriptorData of(UUID service, UUID characteristic,UUID descriptor, byte[] data){
        return new DescriptorData(service,
                characteristic,
                descriptor,
                data);
    }


    public static DescriptorData of(String service, String characteristic,String descriptor, byte[] data){
        return new DescriptorData(UUID.fromString(service),
                UUID.fromString(characteristic),
                UUID.fromString(descriptor),
                data);
    }

    public static DescriptorData from(BluetoothGattDescriptor descriptor){
        return new DescriptorData(descriptor.getCharacteristic().getService().getUuid() ,
                descriptor.getCharacteristic().getUuid(),
                descriptor.getUuid(),
                descriptor.getValue());
    }


    @Override
    public String toString() {
        return "DescriptorData{" +
                "service=" + service +
                ", characteristic=" + characteristic +
                ", descriptor=" + descriptor +
                ", data=" + "(0x)" + Hex.toString(data, '-') +
                '}';
    }
}
