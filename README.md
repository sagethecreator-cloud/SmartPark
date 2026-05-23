<div align="center">

# 🅿️ SmartPark — Parking Management System

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-17-0080FF?style=flat-square&logo=java&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=flat-square&logo=intellij-idea&logoColor=white)
![Scene Builder](https://img.shields.io/badge/Scene_Builder-FF6600?style=flat-square)
![OOP](https://img.shields.io/badge/OOP-Design_Patterns-4CAF50?style=flat-square)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=flat-square)

A full-featured, dual-role Smart Parking Management System built in **Java** with a polished **JavaFX GUI**.  
Multi-vehicle support · EV charging · Real-time slot grid · Complete admin portal · File persistence

[Features](#-features) · [Architecture](#️-architecture) · [OOP Concepts](#-oop-concepts-used) · [Getting Started](#-getting-started) · [Team](#-team)

</div>

---

## 📌 Overview

This is not a basic console application.

SmartPark is a fully functional, dual-role parking management system built from the ground up in **Java** with a hand-crafted **JavaFX + Scene Builder** graphical interface. It supports four vehicle types including EVs with charging, real-time slot management, automated billing, and a complete admin portal — all through a dark-themed GUI.

The backend is architected using core OOP principles: abstract classes, polymorphism, interfaces, custom exceptions, and Java serialization for persistence. The GUI layer sits cleanly on top via the MVC pattern — zero business logic in the controller files.

Built as a collaborative team project for the **Object Oriented Programming** course at **COMSATS University Islamabad, Lahore Campus**, Spring 2026.

---

## ✨ Features

### 👤 User Portal
| Feature | Description |
|---|---|
| 🚗 **Park Vehicle** | Select type (Car / Bike / Truck / EV), enter plate and owner, get assigned slot and Entry ID |
| 🚪 **Exit & Bill** | Enter plate number, receive formatted receipt with duration and total charge |
| 🗺️ **Real-time Slot Grid** | Color-coded visual map — 🟢 Available · 🔴 Occupied · by slot type |
| 🔍 **Find My Vehicle** | Search any parked vehicle instantly by plate number |
| ⚡ **EV Charging** | Request charging on arrival, set battery level, choose per-minute or per-percent billing |

### 🔐 Admin Portal
| Feature | Description |
|---|---|
| 🔑 **Secure Login** | Username + password authentication |
| 📊 **Dashboard** | Live stats — total slots, available, occupied, revenue overview |
| 🅿️ **Manage Slots** | Add or remove slots, change slot types dynamically at runtime |
| 💰 **Parking Rates** | Update rates per vehicle type (Car / Bike / Truck / EV) in real time |
| ⚡ **EV Charging Rates** | Set per-minute and per-percent EV charging prices independently |
| 🔋 **Monitor EV** | Track all active EV charging sessions across every EV slot |
| 🧾 **Billing History** | Complete searchable transaction log of every completed parking session |
| 📈 **Generate Reports** | Occupancy and revenue summaries with export |
| 🚘 **Parked Vehicles** | Full live table of all currently parked vehicles with entry times |

---

## 🏗️ Architecture

```
SmartPark
├── Presentation Layer   →  JavaFX + Scene Builder (FXML + CSS dark theme)
├── Controller Layer     →  15 FXML Controllers (MVC pattern)
├── Business Logic Layer →  Vehicles, ParkingLot, Billing, Admin
└── Persistence Layer    →  Java Serialization via FileManager
```

**Class Hierarchy**
```
Vehicle  (abstract)
├── Car    → getRate() = 2.0f  (Compact slot)
├── Bike   → getRate() = 1.0f  (Motorcycle slot)
├── Truck  → getRate() = 3.0f  (Large slot)
└── EV     → getRate() = 5.0f  (EV slot + implements Chargeable)

ParkingLot   →  owns      →  ParkingSlot[]
Bill         →  uses      →  Vehicle
EVBill       →  extends   →  Bill, implements Chargeable
Admin        →  manages   →  ParkingLot
AppState     →  holds     →  ParkingLot + Admin + BillingHistory
FileManager  →  persists  →  everything via Serialization
```

---

## 📂 Package Structure

```
src/main/java/
│
├── com/smartpark/                  ← Core application
│   ├── MainApp.java                  Entry point (JavaFX Application)
│   ├── AppState.java                 Global state (singleton-style)
│   ├── SceneManager.java             Screen navigation helper
│   ├── FileManager.java              Load / save via Java serialization
│   └── controllers/                  One controller per FXML screen (15 total)
│       ├── WelcomeController.java
│       ├── LoginController.java
│       ├── AdminDashboardController.java
│       ├── UserDashboardController.java
│       ├── ParkVehicleController.java
│       ├── ExitVehicleController.java
│       ├── FindVehicleController.java
│       ├── SlotGridController.java
│       ├── ManageSlotsController.java
│       ├── ParkingRatesController.java
│       ├── BillingHistoryController.java
│       ├── GenerateReportController.java
│       ├── EVChargingController.java
│       ├── EVChargingRatesController.java
│       └── MonitorEVController.java
│
├── vehicles/                       ← Vehicle hierarchy
│   ├── Vehicle.java                  Abstract base class
│   ├── Car.java
│   ├── Bike.java
│   ├── Truck.java
│   └── EV.java
│
├── parking/                        ← Parking domain
│   ├── ParkingLot.java               Manages all slots + rates
│   └── ParkingSlot.java              Individual slot state
│
├── billing/                        ← Billing domain
│   ├── Bill.java                     Parking receipt model
│   └── EVBill.java                   EV-specific charging bill
│
├── users/
│   └── Admin.java                    Admin credentials + operations
│
├── interfaces/
│   └── Chargeable.java               EV charging contract
│
├── exceptions/
│   └── ParkingException.java         Custom runtime exception
│
└── filehandler/
    └── FileHandler.java              Low-level file I/O utilities

src/main/resources/fxml/
├── Main/            welcome · login · adminDashboard · userDashboard
├── Admin Features/  manageSlots · parkingRates · billingHistory · generateReport · monitor_ev · ev_charging_rates
└── User Features/   parkVehicle · exitVehicle · findVehicle · evCharging · slotGridView
```

---

## 🖥️ GUI Screens

| Screen | Description |
|---|---|
| Welcome | Landing screen with navigation to Admin or User portal |
| Login | Admin credential entry with error feedback |
| Admin Dashboard | Live stat cards + quick-access to all admin features |
| User Dashboard | User portal home with 5 quick-access cards |
| Park Vehicle | Type selector, plate + owner input, slot assignment receipt |
| Exit Vehicle | Plate lookup, full billing receipt with duration and amount |
| Slot Grid | Full color-coded visual map of all parking slots |
| Parked Vehicles | Live table of all currently occupied slots |
| Manage Slots | Add / remove slots and change slot types |
| Parking Rates | Per-vehicle-type rate editor |
| EV Charging Rates | Per-minute and per-percent rate configuration |
| Monitor EV | Live view of all active EV charging sessions |
| Billing History | Searchable complete transaction log |
| Generate Report | Occupancy and revenue report generation |
| Find Vehicle | Plate number search with full vehicle details |

---

## 🧠 OOP Concepts Used

| Concept | Where Applied |
|---|---|
| **Abstract Class** | `Vehicle` — abstract `getRate()` and `getType()` methods |
| **Inheritance** | `Car`, `Bike`, `Truck`, `EV` all extend `Vehicle` |
| **Polymorphism** | `ArrayList<Vehicle>` holds mixed types; each calls its own `getRate()` |
| **Interface** | `Chargeable` — implemented by `EVBill` for charging fee logic |
| **Encapsulation** | All fields `private`; accessed via getters/setters throughout |
| **Comparable** | `Vehicle implements Comparable<Vehicle>` — sorts by entry time |
| **Comparator** | `Bill` uses `Comparator` for sorting history by amount or date |
| **Serialization** | `ParkingLot` and `Bill` implement `Serializable` for file persistence |
| **Custom Exception** | `ParkingException` thrown for duplicate or invalid parking entries |
| **Composition** | `ParkingLot` is composed of `ParkingSlot` objects |
| **MVC Pattern** | Full separation — FXML (View), Controllers, Model classes |
| **Collections** | Extensive use of `ArrayList` and `Collections.sort()` |
| **Static Methods** | `FileManager` — load/save methods are static |

---

## 🚀 Getting Started

### Prerequisites
- **Java JDK 17** or higher — [Download](https://adoptium.net/)
- **JavaFX SDK 17** — [Download](https://gluonhq.com/products/javafx/)
- **IntelliJ IDEA** — [Download](https://www.jetbrains.com/idea/download/)
- **Scene Builder 17+** *(optional, for editing FXML)* — [Download](https://gluonhq.com/products/scene-builder/)

### JavaFX Setup in IntelliJ IDEA
1. Go to **File → Project Structure → Libraries** → click **+** → **Java**
2. Navigate to your JavaFX SDK folder → select the `lib/` subfolder → **OK**
3. Go to **Run → Edit Configurations** → add VM options:
```
--module-path "C:\path\to\javafx-sdk-17\lib" --add-modules javafx.controls,javafx.fxml
```
4. Set **Main class** to `com.smartpark.MainApp`

### Clone & Run
```bash
git clone https://github.com/YOUR_USERNAME/SmartPark.git
```
Open `SmartPark` in IntelliJ → configure JavaFX (above) → press **▶ Run**

### Default Admin Credentials
```
Username : Sir Aksam
Password : siraksamisgreat
```

---

## 👥 Team

**Team SmartPark · COMSATS University Islamabad, Lahore Campus · OOP · Spring 2026**

| Name | Role |
|---|---|
| **Saad** | Backend Developer |
| **Taha** | Frontend Developer & Debugger |

---

## 📄 License

This project was built for academic purposes as part of the Object Oriented Programming course at COMSATS University Islamabad, Lahore Campus, Spring 2026.

---

<div align="center">
SmartPark · COMSATS Lahore · OOP · Spring 2026
</div>
