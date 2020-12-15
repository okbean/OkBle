package okble.central.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class Pipe<T> {

    public abstract Sink<T> sink();
    public abstract Source<T> source();


    public static <T> Pipe<T> create(){
        return new PipeImpl<T>();
    }

    private static class PipeImpl<T> extends Pipe<T>{

        Sink<T> sink;
        Source<T> source;

        public PipeImpl(){
            final BlockingQueue<T> queue = new LinkedBlockingQueue<T>();

            sink = new Sink<T>() {
                @Override
                public boolean write(T value) {
                    return queue.offer(value);
                }
            };

            source = new Source<T>() {
                @Override
                public T read(long timeout, TimeUnit unit) throws InterruptedException {
                    return queue.poll(timeout, unit);
                }
            };
        }

        @Override
        public Sink<T> sink() {
            return sink;
        }

        @Override
        public Source<T> source() {
            return source;
        }
    }



}
