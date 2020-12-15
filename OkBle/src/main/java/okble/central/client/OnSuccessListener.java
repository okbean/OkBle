package okble.central.client;

public interface OnSuccessListener<TResult> {

    void onSuccess(OkBleTask<TResult> task, TResult result);
}