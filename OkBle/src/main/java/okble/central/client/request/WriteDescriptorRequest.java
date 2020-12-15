package okble.central.client.request;

import okble.central.client.PacketSender;

import java.util.UUID;

public final class WriteDescriptorRequest extends Request<Void>{

    private UUID service;
    private UUID characteristic;
    private UUID descriptor;
    private PacketSender.Factory packetSenderFactory;
    private long txDelay;
    private long txInterval;

    public UUID service() {
        return service;
    }

    public UUID characteristic() {
        return characteristic;
    }

    public UUID descriptor() {
        return descriptor;
    }

    public PacketSender.Factory packetSenderFactory() {
        return packetSenderFactory;
    }

    private long txDelay() {
        return txDelay;
    }

    private long txInterval() {
        return txInterval;
    }

    private WriteDescriptorRequest(final  Builder builder){
        super(Type.WriteDescriptorRequest);
        this.service = builder.service;
        this.characteristic = builder.characteristic;
        this.descriptor = builder.descriptor;
        this.packetSenderFactory = builder.packetSenderFactory;
        this.txDelay = builder.txDelay;
        this.txInterval = builder.txInterval;
    }

    public final static class Builder {
        private UUID service;
        private UUID characteristic;
        private UUID descriptor;
        private PacketSender.Factory packetSenderFactory;
        private long txDelay = 0;
        private long txInterval = 0;

        public Builder service(UUID service) {
            this.service = service;
            return this;
        }

        public Builder characteristic(UUID characteristic) {
            this.characteristic = characteristic;
            return this;
        }

        public Builder descriptor(UUID descriptor) {
            this.descriptor = descriptor;
            return this;
        }

        public Builder packetSenderFactory(PacketSender.Factory factory) {
            this.packetSenderFactory = factory;
            return this;
        }


        public Builder packetSenderFactory(final byte[] data){
            this.packetSenderFactory =  FixedSizePacketSenderFactory.from(data);
            return this;
        }

        public Builder packetSenderFactory(final byte[] data, final int maxPacketSize){
            this.packetSenderFactory =  FixedSizePacketSenderFactory.from(data, maxPacketSize);
            return this;
        }

        public Builder packetSenderFactory(final byte[] data, final int offset, final int length){
            this.packetSenderFactory =  FixedSizePacketSenderFactory.from(data,  offset,  length);
            return this;
        }

        public Builder packetSenderFactory(final byte[] data, final int offset, final int length, final int maxPacketSize){
            this.packetSenderFactory =  FixedSizePacketSenderFactory.from(data,  offset,  length,  maxPacketSize);
            return this;
        }


        private Builder txDelay(int txDelay) {
            this.txDelay = txDelay;
            return this;
        }

        private Builder txInterval(int txInterval) {
            this.txInterval = txInterval;
            return this;
        }

        public WriteDescriptorRequest build() {
            check(service != null, "service can not be null!");
            check(characteristic != null, "characteristic can not be null!");
            check(descriptor != null, "descriptor can not be null!");

            check(packetSenderFactory != null, "packetSenderFactory can not be null!");

            check((txDelay <= 4_000L), "txDelay should be less than 4000 mills!");
            check((txInterval <= 4_000L), "txInterval should be less than 4000 mills!");

            return new WriteDescriptorRequest(this);
        }

    }


    @Override
    public WriteDescriptorRequest getThis() {
        return this;
    }
}
