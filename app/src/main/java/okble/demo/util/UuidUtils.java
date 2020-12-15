package okble.demo.util;

import java.util.UUID;

public final class UuidUtils {


    private final static String UUID_PAR = "0000%s-0000-1000-8000-00805f9b34fb";


    public static UUID fromShortHex(final String hexStr){
        checkShortHex(hexStr);
        final String str = String.format(UUID_PAR, hexStr);
        final UUID val = UUID.fromString(str);
        return val;
    }

    private static void checkShortHex(final String hexStr){
        if(hexStr == null || hexStr.length() == 0 ){
            throw new IllegalArgumentException("hexStr is null or zero length!");
        }
        if(hexStr.length() != 4){
            throw new IllegalArgumentException("hexStr should be 4 length string!");
        }
        for(int i= 0 ; i<4; i++){
            final char c = hexStr.charAt(i);
            if(!isHexDigit(c)){
                throw new IllegalArgumentException("hexStr should only contain hex digit!");
            }
        }
    }
    private static boolean isHexDigit(char c){
        if((c>=0 && c<='9') ||
                (c>='a' && c<='z') ||
                (c>='A' && c<='Z')){
            return true;
        }
        return false;
    }


    private UuidUtils(){}
}
