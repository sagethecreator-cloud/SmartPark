package exceptions;
// Exception type for parking workflow failures
public class ParkingException extends Exception {
    public ParkingException(String message) {
        super(message);
    }
}