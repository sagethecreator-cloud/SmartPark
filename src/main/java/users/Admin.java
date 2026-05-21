package users;
import com.smartpark.AppState;
import parking.ParkingLot;
import parking.ParkingSlot;
import vehicles.Vehicle;
import billing.Bill;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.util.Date;
// Stores admin credentials and admin operations
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private boolean loggedIn;
    public Admin() {
        this.username = "Sir Aksam";
        this.password = "siraksamisgreat";
        this.loggedIn = false;
    }
    public boolean login(String u, String p) {
        if (u.equals(username) && p.equals(password)) {
            loggedIn = true;
            return true;
        }
        return false;
    }
    public void logout() {
        loggedIn = false;
    }
    public boolean isLoggedIn() {
        return loggedIn;
    }
    // Print parked vehicles ordered by entry time
    public void viewParkedVehicles(ParkingLot lot) {
        System.out.println("\n=== PARKED VEHICLES (by entry time) ===");
        ArrayList<Vehicle> parkedList = new ArrayList<Vehicle>();
        for (int i = 0; i < lot.getTotalSlots(); i++) {
            ParkingSlot slot = lot.getSlot(i);
            if (slot.isOccupied()) {
                parkedList.add(slot.getParkedVehicle());
            }
        }
        if (parkedList.isEmpty()) {
            System.out.println("No vehicles currently parked.");
            return;
        }
        // Keep earliest entries first
        Collections.sort(parkedList);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        for (int i = 0; i < parkedList.size(); i++) {
            Vehicle v = parkedList.get(i);
            System.out.println((i + 1) + ". " + v.toString()
                    + " | Entry: " + sdf.format(new Date(v.getEntryTime())));
        }
    }
    // Print billing history sorted by amount
    public void viewHistory(String filter) {
        System.out.println("\n=== BILLING HISTORY ===");
        ArrayList<Bill> history = AppState.getBillingHistory();
        if (history.isEmpty()) {
            System.out.println("No history records found.");
            return;
        }
        Collections.sort(history, new Bill.AmountComparator());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int count = 0;
        for (int i = 0; i < history.size(); i++) {
            Bill b = history.get(i);
            // Skip rows that do not match the search text
            if (!filter.isEmpty()) {
                boolean matches = b.getVehicleNo().contains(filter)
                        || b.getOwnerName().contains(filter)
                        || b.getVehicleType().contains(filter)
                        || b.getEntryId().contains(filter);
                if (!matches) {
                    continue;
                }
            }
            count++;
            System.out.println(count + ". " + b.toString()
                    + " | " + sdf.format(new Date(b.getEntryTime())));
        }
        if (count == 0) {
            System.out.println("No records match the filter: " + filter);
        }
    }
    public void clearHistory() {
        AppState.clearBillingHistory();
        System.out.println("History cleared successfully.");
    }
    // Print a summary report for the selected period
    public void generateReport(String period) {
        System.out.println("\n=== REPORT (" + period.toUpperCase() + ") ===");
        ArrayList<Bill> history = AppState.getBillingHistory();
        if (history.isEmpty()) {
            System.out.println("No history records to report.");
            return;
        }
        long now = System.currentTimeMillis();
        long cutoff;
        if (period.equals("day")) {
            cutoff = now - (24L * 60 * 60 * 1000);
        } else if (period.equals("week")) {
            cutoff = now - (7L * 24 * 60 * 60 * 1000);
        } else if (period.equals("month")) {
            cutoff = now - (30L * 24 * 60 * 60 * 1000);
        } else {
            System.out.println("Invalid period. Use day/week/month.");
            return;
        }
        int totalVehicles = 0;
        float totalRevenue = 0;
        int totalMinutes = 0;
        for (int i = 0; i < history.size(); i++) {
            Bill b = history.get(i);
            if (b.getExitTime() >= cutoff) {
                totalVehicles++;
                totalRevenue += b.getAmount();
                totalMinutes += b.getDuration();
            }
        }
        System.out.println("Period        : " + period);
        System.out.println("Total Vehicles: " + totalVehicles);
        System.out.println("Total Revenue : Rs " + totalRevenue);
        System.out.println("Total Minutes : " + totalMinutes);
        if (totalVehicles > 0) {
            System.out.println("Avg Duration  : " + (totalMinutes / totalVehicles) + " mins");
            System.out.println("Avg Revenue   : Rs " + (totalRevenue / totalVehicles));
        }
        System.out.println("========================");
    }
    // Update a parking rate through the lot
    public void updateRate(ParkingLot lot, String type, float val) {
        lot.updateRate(type, val);
    }
    // Print a full billing report to the console
    public void exportReport() {
        System.out.println("\n=== EXPORTING REPORT ===");
        ArrayList<Bill> history = AppState.getBillingHistory();
        if (history.isEmpty()) {
            System.out.println("No history records to export.");
            return;
        }
        // Keep exported rows ordered by amount
        Collections.sort(history, new Bill.AmountComparator());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("========== PARKING MANAGEMENT REPORT ==========");
        System.out.println("Generated: " + sdf.format(new Date()));
        System.out.println("Total Records: " + history.size());
        System.out.println("================================================");
        System.out.println();
        float totalRevenue = 0;
        for (int i = 0; i < history.size(); i++) {
            Bill b = history.get(i);
            totalRevenue += b.getAmount();
            System.out.println((i + 1) + ". " + b.getEntryId()
                    + " | " + b.getVehicleNo()
                    + " | " + b.getOwnerName()
                    + " | " + b.getVehicleType()
                    + " | Duration: " + b.getDuration() + " mins"
                    + " | Rs " + b.getAmount());
        }
        System.out.println();
        System.out.println("================================================");
        System.out.println("TOTAL REVENUE: Rs " + totalRevenue);
    }
    // Print live charging details for active EV sessions
    public void monitorEVCharging(ParkingLot lot) {
        System.out.println("\n=== EV CHARGING MONITOR ===");
        boolean found = false;
        long now = System.currentTimeMillis();
        for (int i = 0; i < lot.getTotalSlots(); i++) {
            ParkingSlot slot = lot.getSlot(i);
            if (slot.isOccupied()) {
                Vehicle v = slot.getParkedVehicle();
                // Only EV vehicles expose charging details
                if (v instanceof vehicles.EV) {
                    vehicles.EV ev = (vehicles.EV) v;
                    if (ev.isChargingRequested()) {
                        found = true;
                        int elapsedMins = (int) ((now - ev.getEntryTime()) / 60000);
                        double currentBattery = ev.getBatteryOnArrival() + (elapsedMins * 1.8);
                        if (currentBattery > 100) {
                            currentBattery = 100;
                        }
                        int charged = (int) currentBattery - ev.getBatteryOnArrival();
                        double minutesToFull = ((100 - ev.getBatteryOnArrival()) / 1.8) - elapsedMins;
                        if (minutesToFull < 0) {
                            minutesToFull = 0;
                        }
                        // Read the active charging rate from the parking lot
                        float chargingFeeSoFar = lot.calculateLiveChargingFee(elapsedMins, ev.getBatteryOnArrival());
                        // Format the charging mode for display
                        String modeLabel;
                        if (lot.getChargingMode().equals("perPercent")) {
                            modeLabel = "Per Percent (Rs " + lot.getChargingRatePerPercent() + "/%)";
                        } else {
                            modeLabel = "Per Minute (Rs " + lot.getChargingRatePerMin() + "/min)";
                        }
                        String status;
                        if (currentBattery >= 100) {
                            status = "FULLY CHARGED";
                        } else {
                            status = "CHARGING";
                        }
                        System.out.println("-----------------------------------------------");
                        System.out.println("Slot         : " + slot.getSlotId());
                        System.out.println("Vehicle No.  : " + ev.getVehicleNo());
                        System.out.println("Owner        : " + ev.getOwnerName());
                        System.out.println("Battery In   : " + ev.getBatteryOnArrival() + "%");
                        System.out.println("Battery Now  : " + (int) currentBattery + "%");
                        System.out.println("Charged      : " + charged + "%");
                        System.out.println("Status       : " + status);
                        System.out.println("Charge Mode  : " + modeLabel);
                        System.out.println("Time Elapsed : " + elapsedMins + " mins");
                        System.out.println("Time to Full : " + (int) minutesToFull + " mins");
                        System.out.println("Fee So Far   : $" + chargingFeeSoFar);
                        System.out.println("-----------------------------------------------");
                    }
                }
            }
        }
        if (!found) {
            System.out.println("No EVs currently charging.");
        }
    }
    // EV charging rate management
    // Print the current EV charging rates
    public void viewEVChargingRates(ParkingLot lot) {
        lot.viewChargingRates(); // Delegate rate display to the parking lot
    }
    // Update one EV charging rate after validation
    public boolean updateEVChargingRate(ParkingLot lot, String mode, float newRate) {
        // Reject non-positive rates
        if (newRate <= 0) {
            System.out.println("Error: Rate must be a positive number. Update rejected.");
            return false;
        }
        // Apply the rate to the selected charging mode
        if (mode.equals("perMin")) {
            lot.setChargingRatePerMin(newRate); // Use the parking lot setter
            System.out.println("\u2714 EV charging rate (Per Minute) updated to: Rs " + newRate + "/min");
            return true;
        } else if (mode.equals("perPercent")) {
            lot.setChargingRatePerPercent(newRate); // Use the parking lot setter
            System.out.println("\u2714 EV charging rate (Per Percent) updated to: Rs " + newRate + "/%");
            return true;
        } else {
            System.out.println("Error: Invalid mode. Use 'perMin' or 'perPercent'.");
            return false;
        }
    }
    // Switch between supported charging modes
    public boolean switchChargingMode(ParkingLot lot, String newMode) {
        // Ignore unsupported charging modes
        if (newMode.equals("perMin") || newMode.equals("perPercent")) {
            lot.setChargingMode(newMode); // Use the parking lot setter
            String label;
            if (newMode.equals("perMin")) {
                label = "Per Minute";
            } else {
                label = "Per Percent";
            }
            System.out.println("\u2714 Active charging mode switched to: " + label);
            return true;
        } else {
            System.out.println("Error: Invalid mode. Use 'perMin' or 'perPercent'.");
            return false;
        }
    }
    // Print a compact admin status line
    public void displayInfo() {
        System.out.println("Admin: " + username + " | Logged In: " + loggedIn);
    }
}