package com.smartpark;
import parking.ParkingLot;
import billing.Bill;
import java.io.*;
import java.util.ArrayList;
public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String PARKING_LOT_FILE = DATA_DIR + File.separator + "parkinglot.dat";
    private static final String BILLING_HISTORY_FILE = DATA_DIR + File.separator + "billinghistory.dat";
    public static void save(ParkingLot lot, ArrayList billingHistory) {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        // Persist parking lot state
        ObjectOutputStream lotOut = null;
        try {
            lotOut = new ObjectOutputStream(new FileOutputStream(PARKING_LOT_FILE));
            lotOut.writeObject(lot);
            lotOut.flush();
        } catch (IOException e) {
            System.err.println("Error saving parking lot: " + e.getMessage());
        } finally {
            if (lotOut != null) {
                try {
                    lotOut.close();
                } catch (IOException e) {
                    System.err.println("Error closing parking lot file: " + e.getMessage());
                }
            }
        }
        // Persist billing history
        ObjectOutputStream historyOut = null;
        try {
            historyOut = new ObjectOutputStream(new FileOutputStream(BILLING_HISTORY_FILE));
            historyOut.writeObject(billingHistory);
            historyOut.flush();
        } catch (IOException e) {
            System.err.println("Error saving billing history: " + e.getMessage());
        } finally {
            if (historyOut != null) {
                try {
                    historyOut.close();
                } catch (IOException e) {
                    System.err.println("Error closing billing history file: " + e.getMessage());
                }
            }
        }
    }
    public static ParkingLot loadParkingLot() {
        File file = new File(PARKING_LOT_FILE);
        if (!file.exists()) {
            System.out.println("No saved parking lot found. Creating default lot with 50 slots.");
            return new ParkingLot(50);
        }
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(file));
            ParkingLot lot = (ParkingLot) in.readObject();
            System.out.println("Parking lot loaded from " + PARKING_LOT_FILE);
            return lot;
        } catch (FileNotFoundException e) {
            System.out.println("Parking lot file not found. Creating default lot with 50 slots.");
        } catch (IOException e) {
            System.out.println("Error reading parking lot file. Creating default lot with 50 slots: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Incompatible parking lot data. Creating default lot with 50 slots: " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.err.println("Error closing parking lot file: " + e.getMessage());
                }
            }
        }
        return new ParkingLot(50);
    }
    public static ArrayList loadBillingHistory() {
        File file = new File(BILLING_HISTORY_FILE);
        if (!file.exists()) {
            System.out.println("No saved billing history found. Starting with empty history.");
            return new ArrayList();
        }
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(file));
            ArrayList history = (ArrayList) in.readObject();
            System.out.println("Billing history loaded from " + BILLING_HISTORY_FILE);
            return history;
        } catch (FileNotFoundException e) {
            System.out.println("Billing history file not found. Starting with empty history.");
        } catch (IOException e) {
            System.out.println("Error reading billing history file. Starting with empty history: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Incompatible billing history data. Starting with empty history: " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.err.println("Error closing billing history file: " + e.getMessage());
                }
            }
        }
        return new ArrayList();
    }
}