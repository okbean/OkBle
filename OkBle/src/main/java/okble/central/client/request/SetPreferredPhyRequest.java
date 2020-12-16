package okble.central.client.request;


import okble.central.client.Phy;
import okble.central.client.PhyOptions;
import okble.central.client.PhyMask;

public final class SetPreferredPhyRequest extends Request<Phy>{

    private PhyMask[] txPhy;
    private PhyMask[] rxPhy;
    private PhyOptions phyOptions;


    private SetPreferredPhyRequest(final Builder builder){
        super(Type.SetPreferredPhyRequest);
        this.txPhy = builder.txPhy;
        this.rxPhy = builder.rxPhy;
        this.phyOptions = builder.phyOptions;
    }

    public PhyMask[] txPhy() {
        return txPhy;
    }

    public PhyMask[] rxPhy() {
        return rxPhy;
    }

    public PhyOptions phyOptions() {
        return phyOptions;
    }

    public final static class Builder {
        private PhyMask[] txPhy;
        private PhyMask[] rxPhy;
        private PhyOptions phyOptions;

        public Builder txPhy(PhyMask... txPhy) {
            this.txPhy = txPhy;
            return this;
        }

        public Builder rxPhy(PhyMask... rxPhy) {
            this.rxPhy = rxPhy;
            return this;
        }

        public Builder phyOptions(PhyOptions phyOptions) {
            this.phyOptions = phyOptions;
            return this;
        }

        public SetPreferredPhyRequest build() {

            check(txPhy != null && txPhy.length > 0, "txPhy can not be null or zero length!");
            check(rxPhy != null && rxPhy.length > 0, "rxPhy can not be null or zero length!");
            check(phyOptions != null, "phyOptions can not be null!");

            return new SetPreferredPhyRequest(this);
        }
    }

    @Override
    public SetPreferredPhyRequest getThis() {
        return this;
    }

}
