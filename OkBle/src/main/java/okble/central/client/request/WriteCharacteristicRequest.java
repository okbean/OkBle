package okble.central.client.request;

import okble.central.client.PacketSender;
import okble.central.client.WriteType;

import java.util.UUID;

public final class WriteCharacteristicRequest extends Request<Void>{

    private UUID service;
    private UUID characteristic;
    private WriteType writeType;
    private long txDelay;
    private long txInterval;

    private PacketSender.Factory packetSenderFactory;

    public UUID service() {
        return service;
    }

    public UUID characteristic() {
        return characteristic;
    }


    public WriteType writeType() {
        return writeType;
    }

    private long txDelay() {
        return txDelay;
    }

    private long txInterval() {
        return txInterval;
    }
    public PacketSender.Factory packetSenderFactory() {
        return packetSenderFactory;
    }

    private WriteCharacteristicRequest(final Builder builder){
        super(Type.WriteCharacteristicRequest);
        this.service = builder.service;
        this.characteristic = builder.characteristic;
        this.packetSenderFactory = builder.packetSenderFactory;
        this.writeType = builder.writeType;
        this.txDelay = builder.txDelay;
        this.txInterval = builder.txInterval;
    }

    public final static class Builder {
        private UUID service;
        private UUID characteristic;
        private WriteType writeType;
        private long txDelay = 0;
        private long txInterval = 0;
        private PacketSender.Factory packetSenderFactory;

        public Builder service(UUID service) {
            this.service = service;
            return this;
        }

        public Builder characteristic(UUID characteristic) {
            this.characteristic = characteristic;
            return this;
        }

        public Builder writeType(WriteType writeType) {
            this.writeType = writeType;
            return this;
        }

        public Builder txDelay(int txDelay) {
            this.txDelay = txDelay;
            return this;
        }

        public Builder txInterval(int txInterval) {
            this.txInterval = txInterval;
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


        public WriteCharacteristicRequest build() {
            check(service != null, "service can not be null!");
            check(characteristic != null, "characteristic can not be null!");
            check(writeType != null, "writeType can not be null!");
            check(packetSenderFactory != null, "packetSenderFactory can not be null!");

            check((txDelay <= 4_000L), "txDelay should be less than 4000 mills!");
            check((txInterval <= 4_000L), "txInterval should be less than 4000 mills!");

            return new WriteCharacteristicRequest(this);
        }
    }




    @Override
    public WriteCharacteristicRequest getThis() {
        return this;
    }
}