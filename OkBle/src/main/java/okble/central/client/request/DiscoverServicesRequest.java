package okble.central.client.request;


final class DiscoverServicesRequest extends Request<Void>{

    private DiscoverServicesRequest(final Builder builder){
        super(Type.DiscoverServicesRequest);
    }

    private final static class Builder {
        public DiscoverServicesRequest build() {
            return new DiscoverServicesRequest(this);
        }
    }

    @Override
    public DiscoverServicesRequest getThis() {
        return this;
    }
}