package okble.central.client.request;

import java.util.UUID;

public final class DisableNotificationRequest extends Request<Void>{

    private UUID service;
    private UUID characteristic;

    public UUID service() {
        return service;
    }

    public UUID characteristic() {
        return characteristic;
    }

    private DisableNotificationRequest(final Builder builder){
        super(Type.DisableNotificationRequest);
        this.service = builder.service;
        this.characteristic = builder.characteristic;
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

        public DisableNotificationRequest build() {
            check(service != null, "service can not be null!");
            check(characteristic != null, "characteristic can not be null!");

            return new DisableNotificationRequest(this);
        }
    }

    @Override
    public DisableNotificationRequest getThis() {
        return this;
    }

}