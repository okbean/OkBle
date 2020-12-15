package okble.central.client;

import okble.central.client.executor.RequestExecutor;
import okble.central.client.request.Request;

final class RequestTask{

    private final OkBleTask0<?> task;
    private final RequestExecutor executor;

    private final Request<?> request;
    public OkBleTask0<?> task() {
        return task;
    }

    public RequestExecutor executor() {
        return executor;
    }

    public Priority priority() {
        return priority;
    }

    private Priority priority;

    public RequestTask(OkBleTask0<?> task, RequestExecutor executor, Request<?> request, Priority priority) {
        this.task = task;
        this.executor = executor;
        this.priority = priority;
        this.request = request;
    }


    private boolean occupied = false;
    public synchronized boolean occupy(){
        final boolean val = occupied;
        if(!occupied){
            occupied = true;
        }
        return !val;
    }

    public Request<?> request() {
        return request;
    }

}
