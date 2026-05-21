package vehicles;
import java.io.Serializable;
// EV vehicle type with charging details
public class EV extends Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean chargingRequested; // Whether the driver requested charging
    private int batteryOnArrival;      // Battery level when the EV entered
    public EV(String vehicleNo, String ownerName) {
        super(vehicleNo, ownerName, "EV"); // Initialize shared vehicle fields
        this.chargingRequested = false;
        this.batteryOnArrival = 0;
    }
    // Return the parking rate for this vehicle type
    public float getRate() {
        return 5.0f;
    }
    // Return the display type for this vehicle
    public String getType() {
        return "EV";
    }
    // EV charging accessors
    public boolean isChargingRequested() {
        return chargingRequested;
    }
    public void setChargingRequested(boolean chargingRequested) {
        this.chargingRequested = chargingRequested;
    }
    public int getBatteryOnArrival() {
        return batteryOnArrival;
    }
    public void setBatteryOnArrival(int batteryOnArrival) {
        this.batteryOnArrival = batteryOnArrival;
    }
}
