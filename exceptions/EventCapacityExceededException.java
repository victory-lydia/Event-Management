package exceptions;

public class EventCapacityExceededException extends Exception {
    public EventCapacityExceededException(String message) {
        super(message);
    }
}