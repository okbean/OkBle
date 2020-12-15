package okble.central.client;

final class DefaultConnectionOptions implements ConnectionOptions{


    public DefaultConnectionOptions(){
    }

    @Override
    public int connectionRetryCount() {
        return 1;
    }

    @Override
    public long connectGattDelay() {
        return 3_000L;
    }

    @Override
    public long connectGattInterval() {
        return 5_000L;
    }

    @Override
    public long connectGattTimeout() {
        return 32_000L;
    }

    @Override
    public long discoverServicesDelay() {
        return 2_000L;
    }

    @Override
    public long discoverServicesTimeout() {
        return 32_000L;
    }

}
