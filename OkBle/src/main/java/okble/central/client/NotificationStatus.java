package okble.central.client;

public enum NotificationStatus {

    NotificationEnabled,
    IndicationEnabled,
    Disabled,
    Unknown;

    public static NotificationStatus getStatus(final byte[] value){
        if(value == null || value.length != 2 || value[1] != 0x00){
            return Unknown;
        }
        if(value[0] == 0x00){
            return Disabled;
        }else if(value[0] == 0x01){
            return NotificationEnabled;
        }else if(value[0] == 0x02){
            return IndicationEnabled;
        }
        return Unknown;
    }
}
