package billing;
import vehicles.Vehicle;
import interfaces.Chargeable;
import java.io.Serializable;
// Bill type that adds EV charging charges
public class EVBill extends Bill implements Chargeable, Serializable {
    private static final long serialVersionUID = 1L;
    private int batteryOnArrival;
    private float chargingFee;
    private float chargingRatePerMin;     // Per-minute charging rate for this bill
    private float chargingRatePerPercent; // Per-percent charging rate for this bill
    private String chargingMode;          // Charging mode captured for this bill
    // Create an EV bill with the current charging settings
    public EVBill(Vehicle vehicle, long exitTime, float ratePerMinute, int batteryOnArrival,
                  float chargingRatePerMin, float chargingRatePerPercent, String chargingMode) {
        super(vehicle, exitTime, ratePerMinute); // Initialize shared vehicle fields
        this.batteryOnArrival = batteryOnArrival;
        this.chargingFee = 0.0f;
        this.chargingRatePerMin = chargingRatePerMin;
        this.chargingRatePerPercent = chargingRatePerPercent;
        this.chargingMode = chargingMode; // Keep the selected mode with the bill
    }
    // Estimate minutes needed to reach full charge
    public int estimatedChargeTime(int batteryOnArrival) {
        // Charging model assumes a fixed percent gain per minute
        return (int) Math.ceil((100 - batteryOnArrival) / 1.8);
    }
    // Calculate charging fee using the selected mode
    public float calculateChargingFee(int durationMins, int batteryOnArrival) {
        // Branch by charging mode
        if (chargingMode.equals("perPercent")) {
            // Charge by battery percentage gained
            double totalCharged = durationMins * 1.8;
            // Do not charge beyond full battery
            if (batteryOnArrival + totalCharged > 100) {
                totalCharged = 100 - batteryOnArrival;
            }
            return (float) (totalCharged * chargingRatePerPercent);
        } else {
            // Charge by active charging minutes
            int chargeTimeFull = estimatedChargeTime(batteryOnArrival);
            // Stop charging time once the battery is full
            int actualChargeMins;
            if (durationMins < chargeTimeFull) {
                actualChargeMins = durationMins;
            } else {
                actualChargeMins = chargeTimeFull;
            }
            // Apply the configured per-minute rate
            return actualChargeMins * chargingRatePerMin;
        }
    }
    // Check whether the EV reached full charge
    public boolean isFullyCharged(int durationMins, int batteryOnArrival) {
        return durationMins >= estimatedChargeTime(batteryOnArrival);
    }
    // Add charging fee after the parking fee
    public void calculate() {
        super.calculate(); // Calculate the base parking fee first
        chargingFee = calculateChargingFee(getDuration(), batteryOnArrival);
    }
    // Print EV charging details after the base receipt
    public void printReceipt() {
        super.printReceipt(); // Print the base parking receipt first
        // Estimate battery level at exit
        double batteryOnDeparture = batteryOnArrival + (getDuration() * 1.8);
        if (batteryOnDeparture > 100) {
            batteryOnDeparture = 100;
        }
        // Calculate billable charging time
        int chargeTimeFull = estimatedChargeTime(batteryOnArrival);
        int actualChargeMins;
        if (getDuration() < chargeTimeFull) {
            actualChargeMins = getDuration();
        } else {
            actualChargeMins = chargeTimeFull;
        }
        // Calculate percent gained while parked
        double actualPercentCharged = getDuration() * 1.8;
        if (batteryOnArrival + actualPercentCharged > 100) {
            actualPercentCharged = 100 - batteryOnArrival;
        }
        float parkingFee = getAmount();
        float totalWithCharging = parkingFee + chargingFee;
        // Format charging mode details
        String modeLabel;
        if (chargingMode.equals("perPercent")) {
            modeLabel = "Per Percent (Rs " + chargingRatePerPercent + "/%)";
        } else {
            modeLabel = "Per Minute (Rs " + chargingRatePerMin + "/min)";
        }
        System.out.println("\n--- EV CHARGING BREAKDOWN ---");
        System.out.println("Battery on Arrival  : " + batteryOnArrival + "%");
        System.out.println("Battery on Departure: " + (int) batteryOnDeparture + "%");
        System.out.println("Percent Charged     : " + (int) actualPercentCharged + "%");
        System.out.println("Charging Duration   : " + actualChargeMins + " mins");
        System.out.println("Charging Mode       : " + modeLabel);
        System.out.println("Charging Fee        : $" + chargingFee);
        System.out.println("Parking Fee         : $" + parkingFee);
        System.out.println("TOTAL               : $" + totalWithCharging);
        System.out.println("-----------------------------");
    }
    // Charging fee accessor
    public float getChargingFee() {
        return chargingFee;
    }
    public int getBatteryOnArrival() {
        return batteryOnArrival;
    }
}
