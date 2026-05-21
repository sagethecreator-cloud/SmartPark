package vehicles;
import java.io.Serializable;
// Truck vehicle type
public class Truck extends Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    public Truck(String vehicleNo, String ownerName) {
        super(vehicleNo, ownerName, "Truck");
    }
    public float getRate() {
        return 3.0f;
    }
    public String getType() {
        return "Truck";
    }
}