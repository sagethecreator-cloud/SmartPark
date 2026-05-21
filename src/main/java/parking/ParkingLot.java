package parking;
import vehicles.Vehicle;
import exceptions.ParkingException;
import java.io.Serializable;
import java.util.ArrayList;
// Holds parking slots, rates, and EV charging settings
public class ParkingLot implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<ParkingSlot> slots;
    private float[] rates; // Rate order: Car, Bike, Truck, EV
    private float chargingRatePerMin;     // Per-minute EV charging rate
    private float chargingRatePerPercent; // Per-percent EV charging rate
    private String chargingMode;          // Active EV charging mode
    public ParkingLot(int initialCapacity) {
        slots = new ArrayList<ParkingSlot>();
        rates = new float[]{2.0f, 1.0f, 3.0f, 5.0f}; // Default parking rates
        chargingRatePerMin = 5.0f;      // Default per-minute charging rate
        chargingRatePerPercent = 10.0f; // Default per-percent charging rate
        chargingMode = "perMin";        // Default charging mode
        // Build the initial slot mix
        int compactCount = initialCapacity / 2;
        int motorcycleCount = initialCapacity / 4;
        int evCount = 5;
        int largeCount = initialCapacity - compactCount - motorcycleCount - evCount;
        int id = 1;
        for (int i = 0; i < compactCount; i++) {
            slots.add(new ParkingSlot(id, "Compact"));
            id++;
        }
        for (int i = 0; i < motorcycleCount; i++) {
            slots.add(new ParkingSlot(id, "Motorcycle"));
            id++;
        }
        for (int i = 0; i < largeCount; i++) {
            slots.add(new ParkingSlot(id, "Large"));
            id++;
        }
        // Add EV slots after standard slot types
        for (int i = 0; i < evCount; i++) {
            slots.add(new ParkingSlot(id, "EV"));
            id++;
        }
    }
    // Constructor used when loading saved data
    public ParkingLot() {
        slots = new ArrayList<ParkingSlot>();
        rates = new float[]{2.0f, 1.0f, 3.0f, 5.0f};
        chargingRatePerMin = 5.0f;
        chargingRatePerPercent = 10.0f;
        chargingMode = "perMin";
    }
    // Assign the first available matching slot
    public ParkingSlot assignSlot(Vehicle v) throws ParkingException {
        if (findVehicleByNumber(v.getVehicleNo()) != null) {
            throw new ParkingException("Vehicle with plate " + v.getVehicleNo() + " is already parked!");
        }
        String neededType = getSlotTypeForVehicle(v.getType());
        // Gather free slots that match the vehicle type
        ArrayList<ParkingSlot> availableSlots = new ArrayList<ParkingSlot>();
        for (int i = 0; i < slots.size(); i++) {
            ParkingSlot slot = slots.get(i);
            if (!slot.isOccupied() && slot.getSlotType().equals(neededType)) {
                availableSlots.add(slot);
            }
        }
        if (availableSlots.isEmpty()) {
            throw new ParkingException("No suitable slot available for " + v.getType());
        }
        ParkingSlot chosen = availableSlots.get(0);
        chosen.parkVehicle(v);
        return chosen;
    }
    // Map vehicle type to compatible slot category
    private String getSlotTypeForVehicle(String vehicleType) {
        if (vehicleType.equals("Car")) {
            return "Compact";
        }
        if (vehicleType.equals("Bike")) {
            return "Motorcycle";
        }
        if (vehicleType.equals("Truck")) {
            return "Large";
        }
        // EVs use dedicated EV slots
        if (vehicleType.equals("EV")) {
            return "EV";
        }
        return "Compact";
    }
    // Find a vehicle by its plate number
    public ParkingSlot findVehicleByNumber(String plateNo) {
        for (int i = 0; i < slots.size(); i++) {
            ParkingSlot slot = slots.get(i);
            if (slot.isOccupied()) {
                if (slot.getParkedVehicle().getVehicleNo().equals(plateNo)) {
                    return slot;
                }
            }
        }
        return null;
    }
    // Find a slot by the vehicle's entry ID
    public ParkingSlot findSlotByEntryId(String entryId) {
        for (int i = 0; i < slots.size(); i++) {
            ParkingSlot slot = slots.get(i);
            if (slot.isOccupied()) {
                Vehicle v = slot.getParkedVehicle();
                if (v != null && v.getEntryId().equals(entryId)) {
                    return slot;
                }
            }
        }
        return null;
    }
    // Display all slots in a grid format
    public void displaySlotGrid() {
        System.out.println("\n=== SLOT GRID ===");
        for (int i = 0; i < slots.size(); i++) {
            ParkingSlot s = slots.get(i);
            String status;
            if (s.isOccupied()) {
                status = "[X]";
            } else {
                status = "[ ]";
            }
            System.out.print("Slot " + s.getSlotId() + " (" + s.getSlotType() + ") " + status);
            if (s.isOccupied()) {
                Vehicle v = s.getParkedVehicle();
                System.out.print("  -> " + v.getType() + " | " + v.getVehicleNo() + " | " + v.getOwnerName());
            }
            System.out.println();
        }
        System.out.println("=================");
    }
    // Get the configured rate for a vehicle type
    public float getRate(String vehicleType) {
        if (vehicleType.equals("Car")) return rates[0];
        if (vehicleType.equals("Bike")) return rates[1];
        if (vehicleType.equals("Truck")) return rates[2];
        if (vehicleType.equals("EV")) return rates[3]; // EV parking rate
        return 0;
    }
    // Update rate for a vehicle type
    public void updateRate(String vehicleType, float newRate) {
        if (vehicleType.equals("Car")) rates[0] = newRate;
        else if (vehicleType.equals("Bike")) rates[1] = newRate;
        else if (vehicleType.equals("Truck")) rates[2] = newRate;
        else if (vehicleType.equals("EV")) rates[3] = newRate; // EV parking rate
    }
    // Add a new slot
    public boolean addSlot(String slotType) {
        int newId = slots.size() + 1;
        slots.add(new ParkingSlot(newId, slotType));
        return true;
    }
    // Remove the last slot (only if empty)
    public boolean removeSlot(int slotId) throws ParkingException {
        if (slots.isEmpty()) {
            throw new ParkingException("No slots to remove.");
        }
        ParkingSlot last = slots.get(slots.size() - 1);
        if (last.getSlotId() != slotId) {
            throw new ParkingException("Only the last slot (ID: " + last.getSlotId() + ") can be removed.");
        }
        if (last.isOccupied()) {
            throw new ParkingException("Cannot remove an occupied slot.");
        }
        slots.remove(slots.size() - 1);
        return true;
    }
    // Parking lot accessors and setters
    public int getTotalSlots() {
        return slots.size();
    }
    public ParkingSlot getSlot(int index) {
        return slots.get(index);
    }
    public ArrayList<ParkingSlot> getSlots() {
        return slots;
    }
    public float[] getRates() {
        return rates;
    }
    public void setRates(float[] r) {
        this.rates = r;
    }
    // Per-minute charging rate accessor
    public float getChargingRatePerMin() {
        return chargingRatePerMin;
    }
    // Update the per-minute charging rate
    public void setChargingRatePerMin(float rate) {
        if (rate >= 0) { // Keep rates non-negative
            this.chargingRatePerMin = rate;
        }
    }
    // Per-percent charging rate accessor
    public float getChargingRatePerPercent() {
        return chargingRatePerPercent;
    }
    // Update the per-percent charging rate
    public void setChargingRatePerPercent(float rate) {
        if (rate >= 0) { // Keep rates non-negative
            this.chargingRatePerPercent = rate;
        }
    }
    // Active charging mode accessor
    public String getChargingMode() {
        return chargingMode;
    }
    // Update the active charging mode
    public void setChargingMode(String mode) {
        // Ignore unsupported charging modes
        if (mode.equals("perMin") || mode.equals("perPercent")) {
            this.chargingMode = mode;
        }
    }
    // Print current EV charging settings
    public void viewChargingRates() {
        System.out.println("\n===============================================");
        System.out.println("         \u26a1 EV CHARGING RATES");
        System.out.println("===============================================");
        System.out.println("  Rate Per Minute   : Rs " + chargingRatePerMin + " / min");
        System.out.println("  Rate Per Percent  : Rs " + chargingRatePerPercent + " / %");
        // Format the active charging mode
        String activeLabel;
        if (chargingMode.equals("perMin")) {
            activeLabel = "Per Minute (Rs " + chargingRatePerMin + "/min)";
        } else {
            activeLabel = "Per Percent (Rs " + chargingRatePerPercent + "/%)";
        }
        System.out.println("  Active Mode       : " + activeLabel);
        System.out.println("===============================================");
    }
    // Calculate the current EV charging fee
    public float calculateLiveChargingFee(int elapsedMins, int batteryOnArrival) {
        int estimatedChargeTime = (int) Math.ceil((100 - batteryOnArrival) / 1.8);
        if (chargingMode.equals("perPercent")) {
            // Charge by battery percentage gained
            double totalCharged = elapsedMins * 1.8;
            if (batteryOnArrival + totalCharged > 100) {
                totalCharged = 100 - batteryOnArrival; // Do not charge beyond full battery
            }
            return (float) (totalCharged * chargingRatePerPercent);
        } else {
            // Charge by active charging minutes
            int actualChargeMins;
            if (elapsedMins < estimatedChargeTime) {
                actualChargeMins = elapsedMins;
            } else {
                actualChargeMins = estimatedChargeTime;
            }
            return actualChargeMins * chargingRatePerMin;
        }
    }
}
