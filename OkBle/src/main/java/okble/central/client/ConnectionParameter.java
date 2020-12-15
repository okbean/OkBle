package okble.central.client;


public final class ConnectionParameter {

    private final int interval;
    private final int latency;
    private final int timeout;

    private ConnectionParameter(final int interval, final int latency, final int timeout){
        this.interval = interval;
        this.latency = latency;
        this.timeout = timeout;
    }


    public static ConnectionParameter of(final int interval, final int latency, final int timeout){
        return new ConnectionParameter(interval, latency, timeout);
    }

    public int interval() {
        return interval;
    }

    public int latency() {
        return latency;
    }

    public int timeout() {
        return timeout;
    }

    @Override
    public String toString() {
        return "ConnectionParameter{" +
                "interval=" + interval +
                ", latency=" + latency +
                ", timeout=" + timeout +
                '}';
    }
}
