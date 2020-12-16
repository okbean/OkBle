package okble.central.client.executor;

import okble.central.client.ClientOperation;
import okble.central.client.OkBleTask0;
import okble.central.client.exception.OkBleException;
import okble.central.client.request.Request;


public final class RequestExecutors {

    public static RequestExecutor fromRequestType(final Request.Type type){
        final RequestExecutor executor;
        switch (type){
            case ConnectionRequest:
                executor = new ConnectionExecutor();
                break;
            case DisconnectionRequest:
                executor = new DisconnectionExecutor();
                break;
            case DiscoverServicesRequest:
                executor = new DiscoverServicesExecutor();
                break;
            case ConnectGattRequest:
                executor = new ConnectGattExecutor();
                break;

            case WriteCharacteristicRequest:
                executor = new WriteCharacteristicExecutor();
                break;
            case ReadCharacteristicRequest:
                executor = new ReadCharacteristicExecutor();
                break;
            case WriteCharacteristicForResponseRequest:
                executor = new WriteCharacteristicForResponseExecutor();
                break;


            case ReadDescriptorRequest:
                executor = new ReadDescriptorExecutor();
                break;
            case WriteDescriptorRequest:
                executor = new WriteDescriptorExecutor();
                break;


            case EnableNotificationRequest:
                executor = new EnableNotificationExecutor();
                break;
            case DisableNotificationRequest:
                executor = new DisableNotificationExecutor();
                break;


            case ReadPhyRequest:
                executor = new ReadPhyExecutor();
                break;
            case SetPreferredPhyRequest:
                executor = new SetPreferredPhyExecutor();
                break;


            case RefreshRequest:
                executor = new RefreshExecutor();
                break;
            case ReadRemoteRssiRequest:
                executor = new ReadRemoteRssiExecutor();
                break;
            case SetMtuRequest:
                executor = new SetMtuExecutor();
                break;


            case SetConnectionPriorityRequest:
                executor = new SetConnectionPriorityExecutor();
                break;
            case UpdateConnectionParameterRequest:
                executor = new UpdateConnectionParameterExecutor();
                break;


            case RemoveBondRequest:
                executor = new RemoveBondExecutor();
                break;
            case CreateBondRequest:
                executor = new CreateBondExecutor();
                break;
            default:
                executor = new EmptyRequestExecutor();
                break;
        }

        return executor;
    }


    private final static class EmptyRequestExecutor extends RequestExecutor{

        @Override
        public void executeRequest(ClientOperation client, OkBleTask0 task, Request<?> request0) throws OkBleException {
            throw new OkBleException("it's an empty request!");
        }
    }


    static RequestExecutor fromRequest(final Request request){
        return fromRequestType(request.type());
    }




}
