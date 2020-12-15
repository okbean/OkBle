package okble.central.client.executor;

import okble.central.client.ClientOperation;
import okble.central.client.OkBleTask0;
import okble.central.client.request.Request;

final class UpdateConnectionParameterExecutor extends RequestExecutor {
    @Override
    public void executeRequest(ClientOperation client, OkBleTask0 task, Request<?> request0) {
        throw new RuntimeException("update connection parameter not supported now!");
    }
}
