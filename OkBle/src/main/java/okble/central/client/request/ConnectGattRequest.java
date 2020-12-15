package okble.central.client.request;

import okble.central.client.GattConnector;

final class ConnectGattRequest extends Request<Void>{

    private GattConnector gattConnector;
    private ConnectGattRequest(final Builder builder){
        super(Type.ConnectGattRequest);
        this.gattConnector = builder.gattConnector;
    }

    public GattConnector gattConnector() {
        return gattConnector;
    }


    private final static class Builder {
        private GattConnector gattConnector;

        public Builder connectionOptions(GattConnector gattConnector) {
            this.gattConnector = gattConnector;
            return this;
        }

        public ConnectGattRequest build() {
            return new ConnectGattRequest(this);
        }
    }

    @Override
    public ConnectGattRequest getThis() {
        return this;
    }
}