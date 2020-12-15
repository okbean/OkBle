package okble.central.client.request;

import java.util.UUID;

public final class ReadDescriptorRequest extends Request<byte[]>{

    private UUID service;
    private UUID characteristic;
    private UUID descriptor;

    private ReadDescriptorRequest(Builder builder){
        super(Type.ReadDescriptorRequest);
        this.service = builder.service;
        this.characteristic = builder.characteristic;
        this.descriptor = builder.descriptor;
    }


    public UUID service() {
        return service;
    }

    public UUID characteristic() {
        return characteristic;
    }

    public UUID descriptor() {
        return descriptor;
    }


    public final static class Builder {
        private UUID service;
        private UUID characteristic;
        private UUID descriptor;

        public  Builder service(UUID service) {
            this.service = service;
            return this;
        }

        public  Builder characteristic(UUID characteristic) {
            this.characteristic = characteristic;
            return this;
        }

        public Builder descriptor(UUID descriptor) {
            this.descriptor = descriptor;
            return this;
        }

        public ReadDescriptorRequest build(){
            check(service != null, "service can not be null!");
            check(characteristic != null, "characteristic can not be null!");
            check(descriptor != null, "descriptor can not be null!");

            return new ReadDescriptorRequest(this);
        }
    }

    @Override
    public ReadDescriptorRequest getThis() {
        return this;
    }
}