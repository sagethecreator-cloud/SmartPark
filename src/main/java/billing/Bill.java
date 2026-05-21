package billing;
import com.smartpark.AppState;
import vehicles.Vehicle;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
// Stores one completed parking bill
public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;
    private Vehicle vehicle;
    private long exitTime;
    private float ratePerMinute;
    private int durationMins;
    private float totalAmount;
    // Create a bill before totals are calculated
    public Bill(Vehicle vehicle, long exitTime, float ratePerMinute) {
        this.vehicle = vehicle;
        this.exitTime = exitTime;
        this.ratePerMinute = ratePerMinute;
        this.durationMins = 0;
        this.totalAmount = 0.0f;
    }
    // Calculate parking duration and amount due
    public void calculate() {
        long diffMs = exitTime - getEntryTime();
        durationMins = (int) (diffMs / 60000);
        if (durationMins < 1) {
            durationMins = 1;
        }
        totalAmount = durationMins * ratePerMinute;
    }
    // Print the bill receipt
    public void printReceipt() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println("\n=== PARKING RECEIPT ===");
        System.out.println("Entry ID    : " + getEntryId());
        System.out.println("Vehicle No. : " + getVehicleNo());
        System.out.println("Owner       : " + getOwnerName());
        System.out.println("Type        : " + getVehicleType());
        System.out.println("Entry Time  : " + sdf.format(new Date(getEntryTime())));
        System.out.println("Exit Time   : " + sdf.format(new Date(exitTime)));
        System.out.println("Duration    : " + durationMins + " minutes");
        System.out.println("Rate        : Rs " + ratePerMinute + " / minute");
        System.out.println("TOTAL DUE   : Rs " + totalAmount);
        System.out.println("=======================");
        System.out.println("Thank you! Drive safely.");
    }
    // Add the bill to the current session history
    public void exportToFile() {
        AppState.addBillToHistory(this);
    }
    // Bill accessors
    public int getDuration() {
        return durationMins;
    }
    public float getAmount() {
        return totalAmount;
    }
    public String getEntryId() {
        return vehicle.getEntryId();
    }
    public String getVehicleNo() {
        return vehicle.getVehicleNo();
    }
    public String getOwnerName() {
        return vehicle.getOwnerName();
    }
    public String getVehicleType() {
        return vehicle.getType();
    }
    public long getEntryTime() {
        return vehicle.getEntryTime();
    }
    public long getExitTime() {
        return exitTime;
    }
    public String toString() {
        return getEntryId() + " | " + getVehicleNo() + " | " + getOwnerName()
                + " | " + getVehicleType() + " | Rs " + totalAmount;
    }
    // Sort bills by amount
    public static class AmountComparator implements Comparator<Bill> {
        public int compare(Bill b1, Bill b2) {
            if (b1.getAmount() < b2.getAmount()) return -1;
            if (b1.getAmount() > b2.getAmount()) return 1;
            return 0;
        }
    }
}
