package okble.central.client;

import android.os.Looper;

import okble.central.client.exception.OkBleException;
import okble.central.client.exception.TaskCanceledException;
import okble.central.client.executor.RequestExecutor;
import okble.central.client.request.Request;
import okble.central.client.util.UiExecutor;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public class OkBleTask0<TR> extends OkBleTask<TR>{

    private final OkBleClient mClient;
    private RequestExecutor mRequestExecutor;
    private Request<TR> mRequest;
    OkBleTask0(OkBleClient client, RequestExecutor executor, Request<TR> request){
        this.mClient = client;
        this.mRequestExecutor = executor;
        this.mRequest = request;
    }


    private boolean mEnqueued = false;
    private TR mResult;
    private OkBleException mException;
    private boolean isFinished = false;
    private boolean isSuccess = false;
    private boolean isCanceled = false;


    public synchronized void callOnSuccess(TR result,boolean sync){
        if(isFinished){
            return;
        }
        isFinished = true;
        isSuccess = true;
        mResult = result;
        this.notifyAll();

        callOnSuccessListener(getUiExecutor(sync),mResult);
        callOnCompleteListener(getUiExecutor(sync));
    }



    public synchronized void callOnFailed(Exception ex, boolean sync){
        if(isFinished){
            return;
        }
        isFinished = true;
        mException = (ex instanceof OkBleException) ? (OkBleException)ex : new OkBleException(ex);
        this.notifyAll();

        callOnFailedListener(getUiExecutor(sync),mException);
        callOnCompleteListener(getUiExecutor(sync));
    }

    synchronized void callOnCanceled(final OkBleException ex, final boolean sync){
        if(isFinished){
            return;
        }
        isFinished = true;
        isCanceled = true;
        mException = (ex != null) ? ex : new OkBleException(new CancellationException());
        this.notifyAll();

        callOnFailedListener(getUiExecutor(sync),mException);
        callOnCompleteListener(getUiExecutor(sync));
    }



    @Override
    public synchronized boolean isComplete() {
        return isFinished;
    }

    @Override
    public synchronized boolean isSuccess() {
        return isFinished && isSuccess;
    }

    @Override
    public synchronized boolean isFailed() {
        return isFinished && !isSuccess;
    }

    @Override
    public synchronized boolean isCanceled() {
        return isFinished && isCanceled;
    }

    @Override
    public synchronized OkBleTask<TR> cancel() {
        if(mEnqueued && !isFinished && mRequestTask != null){
            final boolean val = mRequestTask.occupy();
            if(val){
                final OkBleException ex = new TaskCanceledException("canceled from task queue!");
                callOnCanceled(ex, false);
            }
        }
        return this;
    }

    @Override
    public OkBleClient client() {
        return mClient;
    }

    @Override
    public Request<TR> request() {
        return mRequest;
    }

    @Override
    public TR getResult() {
        return mResult;
    }

    @Override
    public OkBleException getException() {
        return mException;
    }


    private RequestTask mRequestTask;
    @Override
    public OkBleTask<TR> enqueue(final Priority priority) {
        synchronized (this) {
            if (mEnqueued){
                throw new IllegalStateException("This task already enqueued!");
            }
            mEnqueued = true;
        }
        mRequestTask = new RequestTask(this, mRequestExecutor, mRequest, priority == null ? Priority.normal() : priority);
        mClient.enqueue(mRequestTask);
        return this;
    }

    @Override
    public OkBleTask<TR> enqueue() {
        return enqueue(Priority.normal());
    }


    @Override
    public TR execute(final Priority priority) throws OkBleException {
        checkMainThread("can not execute in main thread!");
        synchronized (this) {
            if(!mEnqueued){
                this.enqueue(priority == null ? Priority.normal() : priority);
            }
        }
        this.await();
        if(this.isSuccess()){
            return getResult();
        }else{
            final OkBleException ex = getException();
            throw ex != null ? ex : new OkBleException("");
        }
    }

    @Override
    public TR execute() throws OkBleException {
         return execute(Priority.normal());
    }


    @Override
    public OkBleTask<TR> await() {
        checkMainThread("can not await in main thread!");
        synchronized (this) {
            if(!mEnqueued){
                throw new IllegalStateException("call enqueue first!");
            }
            boolean interrupted = false;
            while (!isFinished){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
                if(interrupted){
                    Thread.currentThread().interrupt();
                }
            }
        }
        return this;
    }

    @Override
    public OkBleTask<TR> await(long timeout, TimeUnit unit) {
        checkMainThread("can not await in main thread!");
        synchronized (this) {
            if(!mEnqueued){
                throw new IllegalStateException("call enqueue first!");
            }
            final long duration = unit.toMillis(timeout);
            boolean interrupted = false;
            if (!isFinished ){
                try {
                    this.wait(duration);
                } catch (InterruptedException ex) {
                    interrupted = true;
                }
            }
            if(interrupted){
                Thread.currentThread().interrupt();
            }
        }
        return this;
    }


    private void callOnSuccessListener(final UiExecutor executor, final TR result){
        final ArrayList<OnSuccessListener<TR>> list = new ArrayList<>(mOnSuccessListeners);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                for(final OnSuccessListener<TR> listener : list){
                    listener.onSuccess(OkBleTask0.this, result);
                }
            }
        });
    }

    private final ArrayList<OnSuccessListener<TR>> mOnSuccessListeners = new ArrayList<>(2);
    @Override
    public OkBleTask<TR> addOnSuccessListener(OnSuccessListener<TR> listener) {
        if(listener == null){
            return this;
        }
        if(!mOnSuccessListeners.contains(listener)){
            mOnSuccessListeners.add(listener);
        }
        return this;
    }

    @Override
    public OkBleTask<TR> removeOnSuccessListener(OnSuccessListener<TR> listener) {
        if(listener == null){
            return this;
        }
        mOnSuccessListeners.remove(listener);
        return this;
    }


    private void callOnFailedListener(final UiExecutor executor, final OkBleException ex){
        final ArrayList<OnFailedListener<TR>> list = new ArrayList<>(mOnFailedListeners);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                for(final OnFailedListener<TR> listener : list){
                    listener.onFailed(OkBleTask0.this, ex);
                }
            }
        });
    }

    private final ArrayList<OnFailedListener<TR>> mOnFailedListeners = new ArrayList<>(2);
    @Override
    public OkBleTask<TR> addOnFailedListener(OnFailedListener listener) {
        if(listener == null){
            return this;
        }
        if(!mOnFailedListeners.contains(listener)){
            mOnFailedListeners.add(listener);
        }
        return this;
    }


    @Override
    public OkBleTask<TR> removeOnFailedListener(OnFailedListener<TR> listener) {
        if(listener == null){
            return this;
        }
        mOnFailedListeners.remove(listener);
        return this;
    }



    void callOnCompleteListener(final UiExecutor executor){
        final ArrayList<OnCompleteListener<TR>> list = new ArrayList<>(mOnCompleteListeners);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                for(final OnCompleteListener<TR> listener : list){
                    listener.onComplete(OkBleTask0.this);
                }
            }
        });
    }

    private final ArrayList<OnCompleteListener<TR>> mOnCompleteListeners = new ArrayList<>(2);
    @Override
    public OkBleTask<TR> addOnCompleteListener(OnCompleteListener<TR> listener) {
        if(listener == null){
            return this;
        }
        if(!mOnCompleteListeners.contains(listener)){
            mOnCompleteListeners.add(listener);
        }
        return this;
    }


    @Override
    public OkBleTask<TR> removeOnCompleteListener(OnCompleteListener<TR> listener) {
        if(listener == null){
            return this;
        }
        mOnCompleteListeners.remove(listener);
        return this;
    }



    private final static void checkMainThread(final String errorMsg){
        if(Looper.myLooper() == Looper.getMainLooper()){
            throw new IllegalStateException(errorMsg);
        }
    }

    private final static UiExecutor sSyncUiExecutor = new UiExecutor(true);
    private final static UiExecutor sAsyncUiExecutor = new UiExecutor(false);
    private final static UiExecutor getUiExecutor(boolean sync){
        return sync ? sSyncUiExecutor: sAsyncUiExecutor;
    }

}
