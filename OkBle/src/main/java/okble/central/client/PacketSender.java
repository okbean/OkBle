package okble.central.client;


public interface PacketSender {

    boolean hasNext();
    byte[] nextPacket();
    boolean isDeterminate();
    int size();

    void reset();

    interface Factory{
        PacketSender newPacketSender();
    }

}
