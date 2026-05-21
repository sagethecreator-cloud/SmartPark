package filehandler;

import com.smartpark.AppState;
import com.smartpark.FileManager;
import parking.ParkingLot;
import parking.ParkingSlot;

import java.util.ArrayList;

// Compatibility wrapper used by the console backend.
public class FileHandler {
    public static void loadRates(ParkingLot lot) {
        ParkingLot saved = FileManager.loadParkingLot();
        lot.setRates(saved.getRates());
        lot.setChargingRatePerMin(saved.getChargingRatePerMin());
        lot.setChargingRatePerPercent(saved.getChargingRatePerPercent());
        lot.setChargingMode(saved.getChargingMode());
    }

    public static void loadSlots(ParkingLot lot) {
        ParkingLot saved = FileManager.loadParkingLot();
        ArrayList<ParkingSlot> slots = lot.getSlots();
        slots.clear();
        slots.addAll(saved.getSlots());
        lot.setRates(saved.getRates());
        lot.setChargingRatePerMin(saved.getChargingRatePerMin());
        lot.setChargingRatePerPercent(saved.getChargingRatePerPercent());
        lot.setChargingMode(saved.getChargingMode());
    }

    public static void saveSlots(ParkingLot lot) {
        FileManager.save(lot, AppState.getBillingHistory());
    }

    public static void saveRates(ParkingLot lot) {
        FileManager.save(lot, AppState.getBillingHistory());
    }
}
