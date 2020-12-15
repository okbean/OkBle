package okble.central.client.request;


import okble.central.client.ConnectionPriority;

public final class SetConnectionPriorityRequest extends Request<Void>{

    private ConnectionPriority connectionPriority;
    private SetConnectionPriorityRequest(final  Builder builder){
        super(Type.UpdateConnectionPriorityRequest);
        this.connectionPriority = builder.connectionPriority;
    }

    public ConnectionPriority connectionPriority() {
        return connectionPriority;
    }

    public final static class Builder {
        private ConnectionPriority connectionPriority;

        public  Builder connectionPriority(ConnectionPriority connectionPriority) {
            this.connectionPriority = connectionPriority;
            return this;
        }

        public SetConnectionPriorityRequest build() {
            check(connectionPriority != null, "connectionPriority can not be null!");

            return new SetConnectionPriorityRequest(this);
        }
    }

    @Override
    public SetConnectionPriorityRequest getThis() {
        return this;
    }
}