package okble.central.client.request;

import okble.central.client.Mtu;

public final class SetMtuRequest extends Request<Mtu>{

    private int mtu;
    private SetMtuRequest(final Builder builder){
        super(Type.SetMtuRequest);
        this.mtu = builder.mtu;
    }

    public int mtu() {
        return mtu;
    }

    public final static class Builder {
        private int mtu;

        public Builder mtu(int mtu) {
            checkMtu(mtu);
            this.mtu = mtu;
            return this;
        }

        public SetMtuRequest build() {
            checkMtu(this.mtu);
            return new SetMtuRequest(this);
        }
    }

    @Override
    public SetMtuRequest getThis() {
        return this;
    }

    private static void checkMtu(final int mtu){
        if(mtu < 23 || mtu > 517){
            throw new IllegalArgumentException(String.format("mtu(%s) should less than 518 and more than 22!", mtu));
        }
    }
}