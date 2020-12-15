package okble.central.client.executor;

import okble.central.client.ClientOperation;
import okble.central.client.OkBleTask0;
import okble.central.client.exception.OkBleException;
import okble.central.client.request.Request;

final class ConnectGattExecutor extends RequestExecutor {
    @Override
    public void executeRequest(ClientOperation client, OkBleTask0 task, Request<?> request0) throws OkBleException {
        throw new OkBleException("connect gatt not supported!");
    }
}
