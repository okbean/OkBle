package okble.central.client.request;


final class RemoveBondRequest extends Request<Void>{

    private RemoveBondRequest(final  Builder builder){
        super(Type.RemoveBondRequest);
    }

    public final static class Builder {
        public RemoveBondRequest build() {
            return new RemoveBondRequest(this);
        }
    }

    @Override
    public RemoveBondRequest getThis() {
        return this;
    }
}