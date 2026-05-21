package interfaces;
// Charging behavior required for EV billing
public interface Chargeable {
    float calculateChargingFee(int durationMins, int batteryOnArrival);
    int estimatedChargeTime(int batteryOnArrival);
    boolean isFullyCharged(int durationMins, int batteryOnArrival);
}