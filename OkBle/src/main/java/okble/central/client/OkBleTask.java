package okble.central.client;

import okble.central.client.exception.OkBleException;
import okble.central.client.request.Request;

import java.util.concurrent.TimeUnit;

public abstract class OkBleTask<TResult> {

    public abstract boolean isComplete();
    public abstract boolean isSuccess();
    public abstract boolean isFailed();
    public abstract boolean isCanceled();
    public abstract OkBleTask<TResult>  cancel();

    public abstract OkBleClient client();

    public abstract Request<TResult> request();

    public abstract TResult getResult();
    public abstract OkBleException getException();

    public abstract OkBleTask<TResult> enqueue();
    public abstract OkBleTask<TResult> enqueue(Priority priority);
    public abstract TResult execute() throws OkBleException;
    public abstract TResult execute(Priority priority) throws OkBleException;

    public abstract OkBleTask<TResult> await();
    public abstract OkBleTask<TResult> await(long timeout, TimeUnit unit);

    public abstract OkBleTask<TResult> addOnSuccessListener(OnSuccessListener<TResult> listener);
    public abstract OkBleTask<TResult> removeOnSuccessListener(OnSuccessListener<TResult> listener);

    public abstract OkBleTask<TResult> addOnFailedListener(OnFailedListener<TResult> listener);
    public abstract OkBleTask<TResult> removeOnFailedListener(OnFailedListener<TResult> listener);

    public abstract OkBleTask<TResult> addOnCompleteListener(OnCompleteListener<TResult> listener);
    public abstract OkBleTask<TResult> removeOnCompleteListener(OnCompleteListener<TResult> listener);

}