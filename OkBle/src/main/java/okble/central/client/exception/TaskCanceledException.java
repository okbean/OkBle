package okble.central.client.exception;

public class TaskCanceledException extends OkBleException{

    public TaskCanceledException() {
        super(TASK_CANCELED);
    }

    public TaskCanceledException(String message) {
        super(TASK_CANCELED, message);
    }
}
