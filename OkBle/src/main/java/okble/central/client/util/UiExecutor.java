package okble.central.client.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

public class UiExecutor implements Executor {

    private final Handler mH = new Handler(Looper.getMainLooper());

    private final boolean mSync;

    public UiExecutor(boolean sync){
        this.mSync = sync;
    }

    public UiExecutor(){
        this(false);
    }

    @Override
    public void execute(final Runnable command) {
        if(command == null){
            throw new IllegalArgumentException("command can not be null!");
        }
        if(mSync && Looper.myLooper() == mH.getLooper()){
            command.run();
        }else {
            mH.post(command);
        }
    }
}