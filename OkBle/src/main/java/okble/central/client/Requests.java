package okble.central.client;

import okble.central.client.executor.RequestExecutor;
import okble.central.client.executor.RequestExecutors;
import okble.central.client.request.Request;


final class Requests {


    static <R> OkBleTask<R> newTask(OkBleClient client, Request<R> request){
        final RequestExecutor handler = RequestExecutors.fromRequestType(request.type());
        final OkBleTask0<R> task =  new OkBleTask0(client, handler, request);
        return task;
    }


}
