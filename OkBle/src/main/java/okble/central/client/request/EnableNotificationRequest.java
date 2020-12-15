package okble.central.client.request;

import okble.central.client.NotificationType;

import java.util.UUID;

public final class EnableNotificationRequest extends Request<Void>{

    private UUID service;
    private UUID characteristic;
    private NotificationType notificationType;

    public UUID service() {
        return service;
    }

    public UUID characteristic() {
        return characteristic;
    }

    public NotificationType notificationType() {
        return this.notificationType;
    }

    private EnableNotificationRequest(final Builder builder){
        super(Type.EnableNotificationRequest);
        this.service = builder.service;
        this.characteristic = builder.characteristic;
        this.notificationType = builder.notificationType;
    }

    public final static class Builder {
        private UUID service;
        private UUID characteristic;
        private NotificationType notificationType;

        public Builder service(UUID service) {
            this.service = service;
            return this;
        }

        public Builder characteristic(UUID characteristic) {
            this.characteristic = characteristic;
            return this;
        }

        public Builder notificationType(NotificationType type) {
            this.notificationType = type;
            return this;
        }

        public EnableNotificationRequest build() {
            check(service != null, "service can not be null!");
            check(characteristic != null, "characteristic can not be null!");
            check(notificationType != null, "notificationType can not be null!");

            return new EnableNotificationRequest(this);
        }

    }

    @Override
    public EnableNotificationRequest getThis() {
        return this;
    }

}
