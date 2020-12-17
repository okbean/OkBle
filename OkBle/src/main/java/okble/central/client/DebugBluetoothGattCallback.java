package okble.central.client;

public interface DebugBluetoothGattCallback {

    void onConnectionStateChange(OkBleClient client, int status, int newState);

    void onServicesDiscovered(OkBleClient client, int status);

    void onCharacteristicChanged(OkBleClient client, final CharacteristicData data);

    void onCharacteristicWrite(OkBleClient client, final CharacteristicData data, int status);

    void onCharacteristicRead(OkBleClient client, final CharacteristicData data, int status);

    void onDescriptorRead(OkBleClient client, final DescriptorData data, int status);

    void onDescriptorWrite(OkBleClient client, final DescriptorData data, int status);

    void onPhyUpdate(OkBleClient client, int txPhy, int rxPhy, int status);

    void onPhyRead(OkBleClient client, int txPhy, int rxPhy, int status);

    void onMtuChanged(OkBleClient client, int mtu, int status);

    void onReadRemoteRssi(OkBleClient client, int rssi, int status);

    void onConnectionUpdated(OkBleClient client, int interval, int latency, int timeout, int status);
}
