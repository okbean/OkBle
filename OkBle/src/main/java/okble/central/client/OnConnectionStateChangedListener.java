package okble.central.client;

public interface OnConnectionStateChangedListener {

    void onConnectionStateChanged(final OkBleClient client, final ConnectionState newState, final ConnectionState lastState);

}