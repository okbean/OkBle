package okble.central.client.request;


import okble.central.client.PacketReceiver;
import okble.central.client.PacketSender;
import okble.central.client.WriteType;

import java.util.UUID;

public final class WriteCharacteristicForResponseRequest<T> extends Request<T>{

    private UUID service;
    private UUID characteristic;
    private WriteType writeType;

    private UUID responseService;
    private UUID responseCharacteristic;

    private long txDelay;
    private long txInterval;
    private PacketSender.Factory packetSenderFactory;
    private PacketReceiver.Factory<T> packetReceiverFactory;
    private long rxMaxDelay;
    private long rxMaxInterval;

    public UUID service() {
        return service;
    }

    public UUID characteristic() {
        return characteristic;
    }

    public WriteType writeType() {
        return writeType;
    }

    public UUID responseService() {
        return responseService;
    }

    public UUID responseCharacteristic() {
        return responseCharacteristic;
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

    public PacketReceiver.Factory<T> packetReceiverFactory() {
        return packetReceiverFactory;
    }

    public long rxMaxDelay() {
        return rxMaxDelay;
    }

    public long rxMaxInterval() {
        return rxMaxInterval;
    }

    private WriteCharacteristicForResponseRequest(final Builder builder){
        super(Type.WriteCharacteristicForResponseRequest);
        this.service = builder.service;
        this.characteristic = builder.characteristic;
        this.packetSenderFactory = builder.packetSenderFactory;
        this.writeType = builder.writeType;
        this.txDelay = builder.txDelay;
        this.txInterval = builder.txInterval;

        this.responseService = builder.responseService;
        this.responseCharacteristic = builder.responseCharacteristic;
        this.rxMaxDelay = builder.rxMaxDelay;
        this.rxMaxInterval = builder.rxMaxInterval;

        this.packetReceiverFactory = builder.packetReceiverFactory;
    }

    public final static class Builder<T> {
        private UUID service;
        private UUID characteristic;
        private WriteType writeType;
        private UUID responseService;
        private UUID responseCharacteristic;

        private long txDelay = 0;
        private long txInterval = 0;

        private PacketSender.Factory packetSenderFactory;
        private PacketReceiver.Factory<T> packetReceiverFactory;

        private long rxMaxDelay = 5_000L;
        private long rxMaxInterval = 5_000L;

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


        public Builder responseService(UUID service) {
            this.responseService = service;
            return this;
        }

        public Builder responseCharacteristic(UUID characteristic) {
            this.responseCharacteristic = characteristic;
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


        public Builder packetReceiverFactory(PacketReceiver.Factory<T> factory) {
            this.packetReceiverFactory = factory;
            return this;
        }


        private Builder rxMaxDelay(int rxMaxDelay) {
            this.rxMaxDelay = rxMaxDelay;
            return this;
        }

        private Builder rxMaxInterval(int rxMaxInterval) {
            this.rxMaxInterval = rxMaxInterval;
            return this;
        }

        public WriteCharacteristicForResponseRequest<T> build() {
            check(service != null, "service can not be null!");
            check(characteristic != null, "characteristic can not be null!");
            check(writeType != null, "writeType can not be null!");
            check(packetSenderFactory != null, "factory of PacketSender.Factory can not be null!");
            check(packetReceiverFactory != null, "factory of PacketReceiver.Factory can not be null!");

            check((txDelay <= 4_000L), "txDelay should be less than 4000 mills!");
            check((txInterval <= 4_000L), "txInterval should be less than 4000 mills!");

            check(responseService != null, "responseService can not be null!");
            check(responseCharacteristic != null, "responseCharacteristic can not be null!");

            check((rxMaxDelay >= 5_000L), "txDelay should be more than 5000 mills!");
            check((rxMaxInterval >= 5_000L), "txInterval should be more than 5000 mills!");

            return new WriteCharacteristicForResponseRequest(this);
        }
    }


    @Override
    public WriteCharacteristicForResponseRequest getThis() {
        return this;
    }
}
