package okble.central.client;

public interface OnDataReceivedListener {

    void onDataReceived(OkBleClient client, CharacteristicData data);

}
