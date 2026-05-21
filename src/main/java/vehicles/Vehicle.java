package vehicles;
import java.io.Serializable;
// Shared base data and behavior for every vehicle type
public abstract class Vehicle implements Comparable<Vehicle>, Serializable {
    private static final long serialVersionUID = 1L;
    protected String vehicleNo;
    protected String ownerName;
    protected String type;
    protected String entryId;
    protected long entryTime; // milliseconds since epoch
    public Vehicle(String vehicleNo, String ownerName, String type) {
        this.vehicleNo = vehicleNo;
        this.ownerName = ownerName;
        this.type = type;
        this.entryId = "";
        this.entryTime = 0;
    }
    // Vehicle-specific values supplied by subclasses
    public abstract float getRate();
    public abstract String getType();
    // Shared vehicle helpers
    public String getVehicleNo() {
        return vehicleNo;
    }
    public String getOwnerName() {
        return ownerName;
    }
    public String getEntryId() {
        return entryId;
    }
    public long getEntryTime() {
        return entryTime;
    }
    public void setEntryId(String id) {
        this.entryId = id;
    }
    public void setEntryTime(long t) {
        this.entryTime = t;
    }
    public void generateEntryId() {
        int len = type.length();
        if (len > 3) {
            len = 3;
        }
        entryId = type.substring(0, len) + "-" + vehicleNo;
        entryTime = System.currentTimeMillis();
    }
    // Sort vehicles by entry time
    public int compareTo(Vehicle other) {
        if (this.entryTime < other.entryTime) {
            return -1;
        }
        if (this.entryTime > other.entryTime) {
            return 1;
        }
        return 0;
    }
    @Override
    public String toString() {
        return type + " | " + vehicleNo + " | " + ownerName + " | ID: " + entryId;
    }
}