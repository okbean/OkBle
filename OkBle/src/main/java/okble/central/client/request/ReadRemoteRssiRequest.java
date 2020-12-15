package okble.central.client.request;

import okble.central.client.Rssi;

public final class ReadRemoteRssiRequest extends Request<Rssi>{

    private ReadRemoteRssiRequest(final Builder builder){
        super(Type.ReadRemoteRssiRequest);
    }

    public final static class Builder {
        public ReadRemoteRssiRequest build() {
            return new ReadRemoteRssiRequest(this);
        }
    }

    @Override
    public ReadRemoteRssiRequest getThis() {
        return this;
    }

}