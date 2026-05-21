package vehicles;
import java.io.Serializable;
// Car vehicle type
public class Car extends Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    public Car(String vehicleNo, String ownerName) {
        super(vehicleNo, ownerName, "Car");
    }
    public float getRate() {
        return 2.0f;
    }
    public String getType() {
        return "Car";
    }
}