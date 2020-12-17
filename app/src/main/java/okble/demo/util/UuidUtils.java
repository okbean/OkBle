package okble.demo.util;

import java.util.UUID;

public final class UuidUtils {


    private final static String UUID_PAR = "0000%s-0000-1000-8000-00805f9b34fb";


    private final static String UUID_CCCD = "00002902-0000-1000-8000-00805f9b34fb";


    public static boolean isCccd(final String uuid){
        return UUID_CCCD.equalsIgnoreCase(uuid);
    }

    public static boolean isCccd(final UUID uuid){
        return isCccd(uuid == null ? null : uuid.toString());
    }


    public static String getShortUuid(final String uuid){
        if(canBeShortUuid(uuid)){
            return uuid.toLowerCase().substring(4, 8);
        }
        return "";
    }

    public static String getShortUuid(final UUID uuid){
        return getShortUuid(uuid == null ? null : uuid.toString());
    }

    public static boolean canBeShortUuid(final String uuid){
        if(uuid == null || uuid.length() != 36){
            return false;
        }
        final String lowerUuid = uuid.toLowerCase();
        if(lowerUuid.startsWith("0000") &&
                lowerUuid.startsWith("0000-1000-8000-00805f9b34fb", 9)){
            return true;
        }
        return false;
    }

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
