package okble.central.client.request;

final class CreateBondRequest extends Request<Void>{

    private CreateBondRequest(final Builder builder){
        super(Type.CreateBondRequest);
    }

    public final static class Builder {
        public CreateBondRequest build() {
            return new CreateBondRequest(this);
        }
    }

    @Override
    public CreateBondRequest getThis() {
        return this;
    }
}