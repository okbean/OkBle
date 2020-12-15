package okble.central.client;

public interface PacketReceiver<T> {

    boolean isFinished();
    boolean isSuccess();
    boolean isFailed();
    T getResult();
    Exception getError();
    void onReceived(byte[] packet);

    interface Factory<T>{
        PacketReceiver<T> newPacketReceiver();
    }
}