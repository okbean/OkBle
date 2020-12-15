package okble.central.client.util;

public final class Hex {

    public static String toString(final byte[] data) {
        if(data == null){
            return null;
        }
        return new String(encodeHex(data));
    }

    public static String toString(final byte[] data, final char delimiter) {
        if(data == null){
            return null;
        }
        final char[] chars = encodeHex(data);
        final int len = chars.length;
        final StringBuilder sb = new StringBuilder(len * 2 - 1);

        for(int i=0; i<len; i= i+2){
            if(i != 0){
                sb.append(delimiter);
            }
            sb.append(chars[i]);
            sb.append(chars[i + 1]);
        }
        return sb.toString();
    }


    public static String toString(final byte[] data, final int offset, final int length) {
        return new String(encodeHex(data, offset, length));
    }


    protected static char[] encodeHex(final byte[] data) {
        return encodeHex(data, false);
    }

    protected static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static char[] encodeHex(final byte[] data, final int offset, final int length) {
        return encodeHex(data, offset, length,true);
    }

    protected static char[] encodeHex(final byte[] data, final int offset, final int length, final boolean toLowerCase) {
        return encodeHex(data,offset, length, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }


    protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    protected static char[] encodeHex(final byte[] data, final int offset, final int length, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[length << 1];
        for (int i = offset, j = 0; i < length; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private Hex(){}
}
