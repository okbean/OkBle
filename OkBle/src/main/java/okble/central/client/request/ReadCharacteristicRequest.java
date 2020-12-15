package okble.central.client.request;

import java.util.UUID;


public final class ReadCharacteristicRequest extends Request<byte[]>{

    private UUID service;
    private UUID characteristic;

    private ReadCharacteristicRequest(Builder builder){
        super(Type.ReadCharacteristicRequest);
        this.service = builder.service;
        this.characteristic = builder.characteristic;
    }


    public UUID service() {
        return service;
    }

    public UUID characteristic() {
        return characteristic;
    }



    public final static class Builder {
        private UUID service;
        private UUID characteristic;

        public Builder service(UUID service) {
            this.service = service;
            return this;
        }

        public Builder characteristic(UUID characteristic) {
            this.characteristic = characteristic;
            return this;
        }

        public ReadCharacteristicRequest build(){
            check(service != null, "service can not be null!");
            check(characteristic != null, "characteristic can not be null!");

            return new ReadCharacteristicRequest(this);
        }
    }

    @Override
    public ReadCharacteristicRequest getThis() {
        return this;
    }
}
