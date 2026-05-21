package vehicles;
import java.io.Serializable;
// Bike vehicle type
public class Bike extends Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    public Bike(String vehicleNo, String ownerName) {
        super(vehicleNo, ownerName, "Bike");
    }
    public float getRate() {
        return 1.0f;
    }
    public String getType() {
        return "Bike";
    }
}