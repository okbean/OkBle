package okble.central.client;

public interface OnCompleteListener<TResult> {

    void onComplete(OkBleTask<TResult> task);
}