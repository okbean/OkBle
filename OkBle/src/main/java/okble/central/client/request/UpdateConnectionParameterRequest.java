package okble.central.client.request;


final class UpdateConnectionParameterRequest extends Request<Void>{

    int minConnectionInterval;
    int maxConnectionInterval;
    int slaveLatency;
    int supervisionTimeout;
    int minConnectionEventLen;
    int maxConnectionEventLen;
    private UpdateConnectionParameterRequest(final Builder builder){
        super(Type.UpdateConnectionParameterRequest);
        this.minConnectionInterval = builder.minConnectionInterval;
        this.maxConnectionInterval = builder.maxConnectionInterval;
        this.slaveLatency = builder.slaveLatency;
        this.supervisionTimeout = builder.supervisionTimeout;
        this.minConnectionEventLen = builder.minConnectionEventLen;
        this.maxConnectionEventLen = builder.maxConnectionEventLen;
    }

    public int minConnectionInterval() {
        return minConnectionInterval;
    }

    public int maxConnectionInterval() {
        return maxConnectionInterval;
    }

    public int slaveLatency() {
        return slaveLatency;
    }

    public int supervisionTimeout() {
        return supervisionTimeout;
    }

    public int minConnectionEventLen() {
        return minConnectionEventLen;
    }

    public int maxConnectionEventLen() {
        return maxConnectionEventLen;
    }

    public final static class Builder {
        int minConnectionInterval;
        int maxConnectionInterval;
        int slaveLatency;
        int supervisionTimeout;
        int minConnectionEventLen;
        int maxConnectionEventLen;

        public Builder minConnectionInterval(int value) {
            this.minConnectionInterval = value;
            return this;
        }

        public Builder maxConnectionInterval(int value) {
            this.maxConnectionInterval = value;
            return this;
        }

        public Builder slaveLatency(int value) {
            this.slaveLatency = value;
            return this;
        }

        public Builder supervisionTimeout(int value) {
            this.supervisionTimeout = value;
            return this;
        }

        public Builder minConnectionEventLen(int value) {
            this.minConnectionEventLen = value;
            return this;
        }

        public Builder maxConnectionEventLen(int value) {
            this.maxConnectionEventLen = value;
            return this;
        }

        public UpdateConnectionParameterRequest build() {
            return new UpdateConnectionParameterRequest(this);
        }
    }

    @Override
    public UpdateConnectionParameterRequest getThis() {
        return this;
    }
}