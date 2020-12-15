package okble.central.client.request;

import okble.central.client.Phy;


public final class ReadPhyRequest extends Request<Phy>{

    private ReadPhyRequest(final Builder builder){
        super(Type.ReadPhyRequest);
    }

    public final static class Builder {
        public ReadPhyRequest build() {
            return new ReadPhyRequest(this);
        }
    }

    @Override
    public ReadPhyRequest getThis() {
        return this;
    }
}
