package okble.central.client.request;


public final class RefreshRequest extends Request<Void>{

    private RefreshRequest(final Builder builder){
        super(Type.RefreshRequest);
    }

    public final static class Builder {
        public RefreshRequest build() {
            return new RefreshRequest(this);
        }
    }

    @Override
    public RefreshRequest getThis() {
        return this;
    }
}
