package okble.demo.ui.device2;

public class LogInfo {

    public final long time;
    public final String message;
    public final int level;

    public LogInfo(long time, String message, int level) {
        this.time = time;
        this.message = message;
        this.level = level;
    }

    public LogInfo(long time, String message) {
        this.time = time;
        this.message = message;
        this.level = 0;
    }

}
