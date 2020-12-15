package okble.central.client.util;

import okble.central.client.request.Request;

public final class RequestUtils {

    public static boolean typeIn(Request.Type type, Request.Type... types ){
        if(types == null){
            return false;
        }
        for(Request.Type t : types){
            if(t == type){
                return true;
            }
        }
        return false;
    }

    private RequestUtils(){}

}
