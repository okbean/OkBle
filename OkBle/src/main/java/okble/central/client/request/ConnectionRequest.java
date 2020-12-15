package okble.central.client.request;

import okble.central.client.ConnectionOptions;
import okble.central.client.GattConnector;


public final class ConnectionRequest extends Request<Void>{

    private ConnectionOptions connectionOptions;
    private GattConnector gattConnector;
    public ConnectionRequest(final Builder builder){
        super(Type.ConnectionRequest);
        this.connectionOptions = builder.connectionOptions;
        this.gattConnector = builder.gattConnector;
    }

    public ConnectionOptions connectionOptions() {
        return connectionOptions;
    }

    public GattConnector gattConnector() {
        return gattConnector;
    }


    public final static class Builder {
        private ConnectionOptions connectionOptions = ConnectionOptions.Factory.DEFAULT.newConnectionOptions();
        private GattConnector gattConnector = GattConnector.Factory.DEFAULT.newGattConnector();

        private Builder connectionOptions(ConnectionOptions connectionOptions) {
            if(connectionOptions == null){
                throw new IllegalArgumentException("connectionOptions can not be null!");
            }
            this.connectionOptions = connectionOptions;
            return this;
        }

        private Builder connectionOptions(GattConnector gattConnector) {
            if(gattConnector == null){
                throw new IllegalArgumentException("gattConnector can not be null!");
            }
            this.gattConnector = gattConnector;
            return this;
        }

        public ConnectionRequest build() {
            check(connectionOptions != null, "connectionOptions can not be null!");
            check(gattConnector != null, "gattConnector can not be null!");

            return new ConnectionRequest(this);
        }
    }

    @Override
    public ConnectionRequest getThis() {
        return this;
    }
}