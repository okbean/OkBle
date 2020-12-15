package okble.central.client.executor;

import okble.central.client.ClientOperation;
import okble.central.client.OkBleTask0;
import okble.central.client.exception.GattOperationFailedException;
import okble.central.client.exception.OkBleException;
import okble.central.client.request.Request;
import okble.central.client.request.SetConnectionPriorityRequest;

public final class UpdateConnectionPriorityExecutor extends RequestExecutor {

    @Override
    public void executeRequest(ClientOperation client, OkBleTask0 task, Request<?> request0) {
//        throw new RuntimeException("Update connection priority not supported now!");
        final SetConnectionPriorityRequest request = (SetConnectionPriorityRequest)request0;
        final int connectionPriority = request.connectionPriority().value();
        try {
            final boolean success = client.doUpdateConnectionPriority(connectionPriority);
            if(!success){
                throw new GattOperationFailedException("request connection priority return false!");
            }
            task.callOnSuccess(null, false);
        } catch (OkBleException ex) {
            task.callOnFailed(ex, false);
        }
    }




}
