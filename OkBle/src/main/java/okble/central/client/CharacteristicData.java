package okble.central.client;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

import okble.central.client.util.Hex;

public final class CharacteristicData {

    private final UUID service;
    private final UUID characteristic;
    private final byte[] data;


    private CharacteristicData(UUID service, UUID characteristic, byte[] data){
        this.service = service;
        this.characteristic = characteristic;
        this.data = data;
    }


    public UUID service() {
        return service;
    }

    public UUID characteristic() {
        return characteristic;
    }

    public byte[] data() {
        return data;
    }


    private CharacteristicData of(UUID service, UUID characteristic,UUID descriptor, byte[] data){
        return new CharacteristicData(service,
                characteristic,
                data);
    }


    public static CharacteristicData of(String service, String characteristic, byte[] data){
        return new CharacteristicData(UUID.fromString(service),
                UUID.fromString(characteristic),
                data);
    }

    public static CharacteristicData from(BluetoothGattCharacteristic characteristic){
        return new CharacteristicData(characteristic.getService().getUuid() ,
                characteristic.getUuid(),
                characteristic.getValue());
    }

    public static CharacteristicData from(BluetoothGattCharacteristic characteristic, byte[] data){
        return new CharacteristicData(characteristic.getService().getUuid() ,
                characteristic.getUuid(),
                data);
    }

    @Override
    public String toString() {
        return "CharacteristicData{" +
                "service=" + service +
                ", characteristic=" + characteristic +
                ", data=" + "(0x)" + Hex.toString(data, '-') +
                '}';
    }
}
