import java.util.Scanner;

import com.smartpark.AppState;
import parking.ParkingLot;
import parking.ParkingSlot;
import vehicles.Vehicle;
import vehicles.Car;
import vehicles.Bike;
import vehicles.Truck;
import vehicles.EV;
import billing.Bill;
import billing.EVBill;
import users.Admin;
import filehandler.FileHandler;
import exceptions.ParkingException;

// Console backend entry point for the parking management system.
public class Main {

    private static Scanner scanner = new Scanner(System.in);

    // Read a non-empty line from the user.
    private static String readLineNonEmpty(String prompt) {
        String s = "";
        while (s.isEmpty()) {
            System.out.print(prompt);
            s = scanner.nextLine().trim();
            if (s.isEmpty()) {
                System.out.println("Input cannot be empty. Try again.");
            }
        }
        return s;
    }

    // Read an integer and keep asking until the value is valid.
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    // Read a decimal number and keep asking until the value is valid.
    private static float readFloat(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Float.parseFloat(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    // Pause the console so the user can read the result.
    private static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static void main(String[] args) {
        AppState.init();
        ParkingLot lot = new ParkingLot(50);
        Admin admin = new Admin();

        FileHandler.loadRates(lot);
        FileHandler.loadSlots(lot);

        boolean running = true;

        while (running) {
            System.out.println("\n=== PARKING MANAGEMENT SYSTEM ===");
            System.out.println("1. Admin");
            System.out.println("2. User");
            System.out.println("3. Exit");
            int role = readInt("Select role: ");

            if (role == 1) {
                boolean backToRole = false;
                boolean adminLoggedIn = false;

                while (!adminLoggedIn && !backToRole) {
                    System.out.println("\n--- ADMIN LOGIN ---");
                    System.out.println("1. Login");
                    System.out.println("9. Back");
                    System.out.println("0. Exit");
                    int li = readInt("Choose: ");

                    if (li == 0) {
                        FileHandler.saveSlots(lot);
                        FileHandler.saveRates(lot);
                        System.out.println("Saving data and exiting...");
                        return;
                    } else if (li == 9) {
                        backToRole = true;
                    } else if (li == 1) {
                        String u = readLineNonEmpty("Username: ");
                        String p = readLineNonEmpty("Password: ");
                        if (admin.login(u, p)) {
                            adminLoggedIn = true;
                            System.out.println("Login successful!");
                        } else {
                            System.out.println("Invalid credentials. Try again.");
                        }
                        pause();
                    } else {
                        System.out.println("Invalid option.");
                    }
                }

                while (adminLoggedIn && !backToRole) {
                    System.out.println("\n--- ADMIN FEATURES ---");
                    System.out.println("1. View Parked Vehicles");
                    System.out.println("2. View History");
                    System.out.println("3. Clear History");
                    System.out.println("4. Generate Report (day/week/month)");
                    System.out.println("5. Update Parking Rate");
                    System.out.println("6. Manage EV Charging Rates");
                    System.out.println("7. Export Report");
                    System.out.println("8. Add Slot");
                    System.out.println("9. Remove Slot");
                    System.out.println("10. Monitor EV Charging");
                    System.out.println("11. Back");
                    System.out.println("12. Logout");
                    System.out.println("0. Exit");
                    int aopt = readInt("Choose: ");

                    if (aopt == 0) {
                        FileHandler.saveSlots(lot);
                        FileHandler.saveRates(lot);
                        System.out.println("Saving data and exiting...");
                        return;
                    }

                    switch (aopt) {
                        case 1:
                            admin.viewParkedVehicles(lot);
                            pause();
                            break;
                        case 2:
                            System.out.print("Enter filter (empty for all): ");
                            String filter = scanner.nextLine().trim();
                            admin.viewHistory(filter);
                            pause();
                            break;
                        case 3: {
                            System.out.print("Are you sure you want to clear ALL history? (yes/no): ");
                            String confirm = scanner.nextLine().trim();
                            if (confirm.equalsIgnoreCase("yes")) {
                                admin.clearHistory();
                            } else {
                                System.out.println("Clear history cancelled.");
                            }
                            pause();
                            break;
                        }
                        case 4:
                            String period = readLineNonEmpty("Enter period (day/week/month): ");
                            admin.generateReport(period);
                            pause();
                            break;
                        case 5:
                            String type = readLineNonEmpty("Enter vehicle type (Car/Bike/Truck/EV): ");
                            float val = readFloat("Enter new rate (Rs per minute): ");
                            admin.updateRate(lot, type, val);
                            System.out.println("Rate updated.");
                            pause();
                            break;
                        case 6: {
                            boolean rateMenuRunning = true;
                            while (rateMenuRunning) {
                                System.out.println("\n--- EV CHARGING RATE MANAGEMENT ---");
                                System.out.println("1. View Current EV Charging Rates");
                                System.out.println("2. Update Rate (Per Minute)");
                                System.out.println("3. Update Rate (Per Percent)");
                                System.out.println("4. Switch Active Charging Mode");
                                System.out.println("5. Back");
                                int evOpt = readInt("Choose: ");

                                if (evOpt == 1) {
                                    admin.viewEVChargingRates(lot);
                                    pause();
                                } else if (evOpt == 2) {
                                    float newRate = readFloat("Enter new per-minute rate (Rs/min): ");
                                    if (newRate <= 0) {
                                        System.out.println("Error: Rate must be positive. Update rejected.");
                                    } else {
                                        admin.updateEVChargingRate(lot, "perMin", newRate);
                                        FileHandler.saveRates(lot);
                                    }
                                    pause();
                                } else if (evOpt == 3) {
                                    float newRate = readFloat("Enter new per-percent rate (Rs/%): ");
                                    if (newRate <= 0) {
                                        System.out.println("Error: Rate must be positive. Update rejected.");
                                    } else {
                                        admin.updateEVChargingRate(lot, "perPercent", newRate);
                                        FileHandler.saveRates(lot);
                                    }
                                    pause();
                                } else if (evOpt == 4) {
                                    System.out.println("Current mode: " + lot.getChargingMode());
                                    String newMode = readLineNonEmpty("Enter new mode (perMin / perPercent): ");
                                    admin.switchChargingMode(lot, newMode);
                                    FileHandler.saveRates(lot);
                                    pause();
                                } else if (evOpt == 5) {
                                    rateMenuRunning = false;
                                } else {
                                    System.out.println("Invalid option.");
                                }
                            }
                            break;
                        }
                        case 7:
                            admin.exportReport();
                            pause();
                            break;
                        case 8:
                            String slotType = readLineNonEmpty("Enter slot type (Compact/Motorcycle/Large/EV): ");
                            if (lot.addSlot(slotType)) {
                                FileHandler.saveSlots(lot);
                                System.out.println("Slot added. New total slots: " + lot.getTotalSlots());
                            }
                            pause();
                            break;
                        case 9:
                            int slotId = readInt("Enter slot id to remove (only last slot): ");
                            try {
                                lot.removeSlot(slotId);
                                FileHandler.saveSlots(lot);
                                System.out.println("Slot removed. Total slots: " + lot.getTotalSlots());
                            } catch (ParkingException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                            pause();
                            break;
                        case 10: {
                            admin.monitorEVCharging(lot);
                            pause();
                            break;
                        }
                        case 11:
                            backToRole = true;
                            break;
                        case 12:
                            admin.logout();
                            adminLoggedIn = false;
                            System.out.println("Logged out.");
                            pause();
                            break;
                        default:
                            System.out.println("Invalid option.");
                    }
                }

            } else if (role == 2) {
                boolean backToRole = false;

                while (!backToRole) {
                    System.out.println("\n--- USER ---");
                    System.out.println("1. Park Vehicle");
                    System.out.println("2. Exit Vehicle (Bill)");
                    System.out.println("3. View Slot Grid");
                    System.out.println("4. Find My Vehicle (by plate)");
                    System.out.println("5. Check EV Charging Status");
                    System.out.println("6. Back");
                    System.out.println("0. Exit");
                    int uopt = readInt("Choose: ");

                    if (uopt == 0) {
                        FileHandler.saveSlots(lot);
                        FileHandler.saveRates(lot);
                        System.out.println("Saving data and exiting...");
                        return;
                    }

                    switch (uopt) {
                        case 1: {
                            String vType = readLineNonEmpty("Vehicle type (Car/Bike/Truck/EV): ");
                            String plate = readLineNonEmpty("Vehicle number / plate: ");

                            if (lot.findVehicleByNumber(plate) != null) {
                                System.out.println("Error: A vehicle with number plate '" + plate
                                        + "' is already parked. Cannot park duplicate.");
                                pause();
                                break;
                            }

                            System.out.print("Owner name: ");
                            String owner = scanner.nextLine().trim();

                            Vehicle v = null;
                            if (vType.equals("Car")) {
                                v = new Car(plate, owner);
                            } else if (vType.equals("Bike")) {
                                v = new Bike(plate, owner);
                            } else if (vType.equals("Truck")) {
                                v = new Truck(plate, owner);
                            } else if (vType.equals("EV")) {
                                EV ev = new EV(plate, owner);
                                String chargingAnswer = readLineNonEmpty("Do you want EV charging? (yes/no): ");
                                if (chargingAnswer.equals("yes")) {
                                    int battery = -1;
                                    while (battery < 0 || battery > 99) {
                                        battery = readInt("Enter current battery percentage (0-99): ");
                                        if (battery < 0 || battery > 99) {
                                            System.out.println("Battery must be between 0 and 99. Try again.");
                                        }
                                    }
                                    ev.setChargingRequested(true);
                                    ev.setBatteryOnArrival(battery);
                                    System.out.println("Charging will begin once parked.");
                                } else {
                                    ev.setChargingRequested(false);
                                }
                                v = ev;
                            } else {
                                System.out.println("Unsupported vehicle type.");
                                pause();
                                break;
                            }

                            v.generateEntryId();
                            try {
                                ParkingSlot s = lot.assignSlot(v);
                                FileHandler.saveSlots(lot);
                                System.out.println("Parked! Entry ID: " + v.getEntryId()
                                        + ", Slot: " + s.getSlotId());
                            } catch (ParkingException e) {
                                System.out.println("Parking failed: " + e.getMessage());
                            }
                            pause();
                            break;
                        }
                        case 2: {
                            String entryId = readLineNonEmpty("Enter Entry ID: ");
                            ParkingSlot slot = lot.findSlotByEntryId(entryId);
                            if (slot == null) {
                                System.out.println("Entry ID not found.");
                                pause();
                                break;
                            }
                            Vehicle v = slot.getParkedVehicle();
                            if (v == null) {
                                System.out.println("Slot empty unexpectedly.");
                                pause();
                                break;
                            }

                            long exitTime = System.currentTimeMillis();
                            float rate = lot.getRate(v.getType());
                            Bill bill;

                            if (v instanceof EV && ((EV) v).isChargingRequested()) {
                                EV ev = (EV) v;
                                EVBill evBill = new EVBill(v, exitTime, rate, ev.getBatteryOnArrival(),
                                        lot.getChargingRatePerMin(), lot.getChargingRatePerPercent(),
                                        lot.getChargingMode());
                                evBill.calculate();
                                evBill.printReceipt();
                                evBill.exportToFile();
                                bill = evBill;
                            } else {
                                bill = new Bill(v, exitTime, rate);
                                bill.calculate();
                                bill.printReceipt();
                                bill.exportToFile();
                            }

                            slot.vacate();
                            FileHandler.saveSlots(lot);
                            FileHandler.saveRates(lot);
                            System.out.println("Vehicle exited and billed.");
                            pause();
                            break;
                        }
                        case 3:
                            lot.displaySlotGrid();
                            pause();
                            break;
                        case 4: {
                            String plate = readLineNonEmpty("Enter vehicle number / plate: ");
                            ParkingSlot found = lot.findVehicleByNumber(plate);
                            if (found != null) {
                                Vehicle v = found.getParkedVehicle();
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
                                System.out.println("Found at slot " + found.getSlotId()
                                        + ", Entry ID: " + v.getEntryId()
                                        + ", Entry Time: " + sdf.format(new java.util.Date(v.getEntryTime())));

                                long now = System.currentTimeMillis();
                                int parkedMins = (int) ((now - v.getEntryTime()) / 60000);

                                if (v instanceof EV) {
                                    EV ev = (EV) v;
                                    if (ev.isChargingRequested()) {
                                        int chargeTime = (int) Math.ceil((100 - ev.getBatteryOnArrival()) / 1.8);
                                        if (parkedMins >= chargeTime) {
                                            System.out.println("EV " + v.getVehicleNo() + " battery is fully charged!");
                                        }
                                    }
                                }

                            } else {
                                System.out.println("Vehicle not found.");
                            }
                            pause();
                            break;
                        }
                        case 5: {
                            String evPlate = readLineNonEmpty("Enter your vehicle plate number: ");
                            ParkingSlot evFound = lot.findVehicleByNumber(evPlate);
                            if (evFound == null) {
                                System.out.println("Vehicle not found.");
                            } else {
                                Vehicle evV = evFound.getParkedVehicle();
                                if (!(evV instanceof EV)) {
                                    System.out.println("This vehicle is not an EV.");
                                } else {
                                    EV evCar = (EV) evV;
                                    if (!evCar.isChargingRequested()) {
                                        System.out.println("Your EV is parked but not connected to charging.");
                                    } else {
                                        long nowTime = System.currentTimeMillis();
                                        int elapsedM = (int) ((nowTime - evCar.getEntryTime()) / 60000);
                                        int estChargeTime = (int) Math.ceil((100 - evCar.getBatteryOnArrival()) / 1.8);
                                        double curBattery = evCar.getBatteryOnArrival() + (elapsedM * 1.8);
                                        if (curBattery > 100)
                                            curBattery = 100;
                                        int chargedAmt = (int) curBattery - evCar.getBatteryOnArrival();
                                        double minsToFull = ((100 - evCar.getBatteryOnArrival()) / 1.8) - elapsedM;
                                        if (minsToFull < 0)
                                            minsToFull = 0;
                                        int actChargeMins = (elapsedM < estChargeTime) ? elapsedM : estChargeTime;
                                        float feeSoFar = lot.calculateLiveChargingFee(elapsedM,
                                                evCar.getBatteryOnArrival());
                                        String evStatus = (curBattery >= 100) ? "FULLY CHARGED \u2714"
                                                : "CHARGING \u26a1";
                                        System.out.println("\n===============================");
                                        System.out.println("\u26a1 EV CHARGING STATUS");
                                        System.out.println("===============================");
                                        System.out.println("Vehicle No.  : " + evCar.getVehicleNo());
                                        System.out.println("Battery In   : " + evCar.getBatteryOnArrival() + "%");
                                        System.out.println("Battery Now  : " + (int) curBattery + "%");
                                        System.out.println("Charged      : " + chargedAmt + "%");
                                        System.out.println("Status       : " + evStatus);
                                        System.out.println("Time Elapsed : " + elapsedM + " mins");
                                        System.out.println("Charge Time  : " + actChargeMins + " mins");
                                        System.out.println("Time to Full : " + (int) minsToFull + " mins");
                                        System.out.println("Fee So Far   : $" + feeSoFar);
                                        System.out.println("===============================");
                                        if (curBattery >= 100) {
                                            System.out.println(evCar.getVehicleNo() + " is fully charged!");
                                        }
                                    }
                                }
                            }
                            pause();
                            break;
                        }
                        case 6:
                            backToRole = true;
                            break;
                        default:
                            System.out.println("Invalid option.");
                    }
                }

            } else if (role == 3) {
                System.out.println("Saving data and exiting...");
                FileHandler.saveSlots(lot);
                FileHandler.saveRates(lot);
                running = false;
            } else {
                System.out.println("Invalid role.");
            }
        }
    }
}
