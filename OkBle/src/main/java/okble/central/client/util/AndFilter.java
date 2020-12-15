package okble.central.client.util;

import okble.central.client.OkBleEvent;

public final class AndFilter implements OkBleEventFilter {

    final OkBleEventFilter[] mFilters;
    public AndFilter(OkBleEventFilter... filters){
        this.mFilters = filters;
    }

    @Override
    public boolean accept(OkBleEvent event) {
        if(mFilters != null){
            for(OkBleEventFilter filter : mFilters){
                if(filter == null){
                    continue;
                }
                if(!filter.accept(event)){
                    return false;
                }
            }
        }
        return true;
    }
}
