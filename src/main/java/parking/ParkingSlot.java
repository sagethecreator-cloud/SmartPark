package parking;
import vehicles.Vehicle;
import java.io.Serializable;
// Stores the state for one parking slot
public class ParkingSlot implements Serializable {
    private static final long serialVersionUID = 1L;
    private int slotId;
    private String slotType; // Slot category used for vehicle matching
    private boolean occupied;
    private Vehicle parkedVehicle;
    public ParkingSlot(int slotId, String slotType) {
        this.slotId = slotId;
        this.slotType = slotType;
        this.occupied = false;
        this.parkedVehicle = null;
    }
    public boolean parkVehicle(Vehicle v) {
        if (occupied) {
            return false;
        }
        parkedVehicle = v;
        occupied = true;
        return true;
    }
    public void vacate() {
        parkedVehicle = null;
        occupied = false;
    }
    // Slot accessors
    public int getSlotId() {
        return slotId;
    }
    public String getSlotType() {
        return slotType;
    }
    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }
    public boolean isOccupied() {
        return occupied;
    }
    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }
}
