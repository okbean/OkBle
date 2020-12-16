package okble.central.client.request;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Request<T> {

    public enum Type{
        ConnectionRequest,
        DisconnectionRequest,
        DiscoverServicesRequest,
        ConnectGattRequest,

        WriteCharacteristicRequest,
        ReadCharacteristicRequest,
        WriteCharacteristicForResponseRequest,

        ReadDescriptorRequest,
        WriteDescriptorRequest,

        EnableNotificationRequest,
        DisableNotificationRequest,

        ReadPhyRequest,
        SetPreferredPhyRequest,

        RefreshRequest,
        ReadRemoteRssiRequest,
        SetMtuRequest,

        SetConnectionPriorityRequest,
        UpdateConnectionParameterRequest,

        RemoveBondRequest,
        CreateBondRequest,
    }

    private final static AtomicInteger sId = new AtomicInteger(0);

    private final Type type;
    private final int id;
    public Request(final Type type){
        this.type = type;
        this.id = sId.getAndAdd(1);
    }

    public int id(){
        return this.id;
    }

    private Object tag;
    public Request<T> tag(final Object tag){
        this.tag = tag;
        return getThis();
    }

    public <R> R tag(){
        return (R)tag;
    }

    public final Type type(){
        return type;
    }


    public abstract Request<T> getThis();

    static void check(final boolean validate, final String errorMsg){
        if(!validate){
            throw new IllegalArgumentException(errorMsg);
        }
    }
}
