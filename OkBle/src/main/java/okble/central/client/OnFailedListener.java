package okble.central.client;

import okble.central.client.exception.OkBleException;

public interface OnFailedListener<TResult> {

    void onFailed(OkBleTask<TResult> task, OkBleException ex);
}
