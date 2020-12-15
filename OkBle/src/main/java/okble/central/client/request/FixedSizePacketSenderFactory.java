package okble.central.client.request;

import okble.central.client.PacketSender;

final class FixedSizePacketSenderFactory implements PacketSender.Factory{

    private final byte[] data;
    private final int offset;
    private final int length;
    private final int maxPacketSize;

    private FixedSizePacketSenderFactory(final byte[] data, final int offset, final int length, final int maxPacketSize){
        if(data == null || data.length <= 0){
            throw new IllegalArgumentException("packet can not be null or zero length!");
        }
        if(offset < 0 || offset >= data.length){
            throw new IllegalArgumentException(String.format("offset:%s out of bound!", offset));
        }
        if((offset + length) >  data.length){
            throw new IllegalArgumentException(String.format("(offset:%s + length:%s) more than data.length:%s!", offset, length,data.length));
        }
        if(maxPacketSize < 1){
            throw new IllegalArgumentException("maxPacketSize can not be less than 1!");
        }
        this.data = data;
        this.offset = offset;
        this.length = length;
        this.maxPacketSize = maxPacketSize;
    }

    public static FixedSizePacketSenderFactory from(final byte[] data, final int offset, final int length, final int maxPacketSize){
        return new FixedSizePacketSenderFactory(data,  offset,  length,  maxPacketSize);
    }

    public static FixedSizePacketSenderFactory from(final byte[] data, final int offset, final int length){
        return new FixedSizePacketSenderFactory(data,  offset,  length,  20);
    }

    public static FixedSizePacketSenderFactory from(final byte[] data, final int maxPacketSize){
        return new FixedSizePacketSenderFactory(data,  0,  data.length,  maxPacketSize);
    }

    public static FixedSizePacketSenderFactory from(final byte[] data){
        return new FixedSizePacketSenderFactory(data,  0,  data.length,  20);
    }

    @Override
    public PacketSender newPacketSender() {
        return new PacketSender(){
            private int index = 0;
            @Override
            public boolean hasNext() {
                final boolean val = index * maxPacketSize < length;
                return  val;
            }

            @Override
            public byte[] nextPacket() {
                if(hasNext()){
                    final int remain = length - (maxPacketSize * index);
                    final int size = remain < maxPacketSize ? remain : maxPacketSize;
                    final byte[] val = new byte[size];
                    System.arraycopy(data, (offset + (maxPacketSize * index)), val, 0,size);
                    index ++;
                    return val;
                }
                return null;
            }

            @Override
            public boolean isDeterminate() {
                return true;
            }

            @Override
            public int size() {
                return length;
            }

            @Override
            public void reset() {
                this.index = 0;
            }
        };
    }
}
