package okble.central.client;

public interface ConnectionOptions {

//    boolean reconnectOnDisconnection();
//    long connectionDelay();
//    long connectionInterval();
//    long connectionTimeout();
//    boolean refreshServicesOnGattConnected();
//    boolean disconnectOnServicesNotValidate();

    int connectionRetryCount();

    long connectGattDelay();
    long connectGattInterval();
    long connectGattTimeout();

    long discoverServicesDelay();
    long discoverServicesTimeout();

    interface Factory{
        ConnectionOptions newConnectionOptions();

        Factory DEFAULT = new Factory(){
            @Override
            public ConnectionOptions newConnectionOptions() {
                return new DefaultConnectionOptions();
            }

        };
    }

}
