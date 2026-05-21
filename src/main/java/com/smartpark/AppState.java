package com.smartpark;
import billing.Bill;
import parking.ParkingLot;
import users.Admin;
import java.util.ArrayList;
public class AppState {
    private static ParkingLot lot;
    private static Admin admin;
    private static ArrayList billingHistory = new ArrayList();
    public static void init() {
        lot = FileManager.loadParkingLot();
        billingHistory = FileManager.loadBillingHistory();
        admin = new Admin();
    }
    public static ParkingLot getLot() { return lot; }
    public static Admin getAdmin() { return admin; }
    public static void addBillToHistory(Bill bill) { billingHistory.add(bill); }
    public static ArrayList getBillingHistory() { return billingHistory; }
    public static void clearBillingHistory() { billingHistory.clear(); }
    public static void save() {
        FileManager.save(lot, billingHistory);
    }
    public static void shutdown() {
        save();
    }
}