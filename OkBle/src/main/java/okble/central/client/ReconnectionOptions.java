package okble.central.client;

interface ReconnectionOptions {

    boolean reconnectOnDisconnection();

    interface Factory{
        ReconnectionOptions newReconnectionOptions();
    }
}
