package okble.central.client.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public final class ExecutorProxy implements InvocationHandler{

    private final Executor mExecutor;
    private final Object mTarget;

    private ExecutorProxy(final Object target, final Executor executor){
        this.mExecutor = executor;
        this.mTarget = target;
    }

    public static <T> T newProxy(final T target, final Executor executor){
        if(executor == null){
            throw new IllegalArgumentException("executor can not be null!");
        }
        final Class<?> clazz = target.getClass();
        final ClassLoader loader = clazz.getClassLoader();
        final Class<?>[] interfaces = clazz.getInterfaces();
        final InvocationHandler invocationHandler = new ExecutorProxy(target, executor);
        return (T) Proxy.newProxyInstance(loader, interfaces,invocationHandler);
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final CountDownLatch lock = new CountDownLatch(1);
        final boolean[] success = new boolean[1];
        final Object[] result = new Object[1];
        final Exception[] exception = new Exception[1];
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if(lock.getCount() <= 0){
                    return;
                }
                try{
                    final Object val = method.invoke(mTarget, args);
                    success[0] = true;
                    result[0] = val;
                }catch (Exception ex){
                    success[0] = false;
                    exception[0] = ex;
                }finally {
                    lock.countDown();
                }
            }
        });
        try{
            lock.await();
        }catch (InterruptedException ex){
            if(lock.getCount() > 0){
                lock.countDown();
            }
            throw ex;
        }
        if(!success[0] && exception[0] != null){
            throw exception[0];
        }
        return result[0];
    }
}
