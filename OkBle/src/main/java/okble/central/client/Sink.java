package okble.central.client;


public interface Sink<T> {

    boolean write(T value);


    final class SinkImpl<T> implements Sink<T>{

        @Override
        public boolean write(T value) {
            return false;
        }
    }
}
