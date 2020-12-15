package okble.demo.util;

import okble.central.client.PacketReceiver;

public class OnePacketReceiverFactory implements PacketReceiver.Factory<byte[]>{

    @Override
    public PacketReceiver<byte[]> newPacketReceiver() {
        return new PacketReceiver<byte[]>(){
            boolean isFinished = false;
            byte[] result;
            @Override
            public boolean isFinished() {
                return isFinished;
            }

            @Override
            public boolean isSuccess() {
                return isFinished;
            }

            @Override
            public boolean isFailed() {
                return false;
            }

            @Override
            public byte[] getResult() {
                return result;
            }

            @Override
            public Exception getError() {
                return null;
            }

            @Override
            public void onReceived(byte[] packet) {
                result = packet;
                isFinished = true;
            }
        };
    }
}
