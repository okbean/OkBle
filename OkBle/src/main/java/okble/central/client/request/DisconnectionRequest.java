package okble.central.client.request;


final class DisconnectionRequest extends Request<Void>{

    private DisconnectionRequest(final Builder builder){
        super(Type.DisconnectionRequest);
    }

    private final static class Builder {
        public DisconnectionRequest build() {
            return new DisconnectionRequest(this);
        }
    }

    @Override
    public DisconnectionRequest getThis() {
        return this;
    }
}
