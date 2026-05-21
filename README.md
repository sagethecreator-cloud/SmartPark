# SmartPark
🅿️ Smart Parking Management System built with Java &amp; JavaFX — Object Oriented Programming
<div align="center">
<img src="https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/JavaFX-17-0080FF?style=for-the-badge&logo=java&logoColor=white"/>
<img src="https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white"/>
<img src="https://img.shields.io/badge/Scene_Builder-FF6600?style=for-the-badge&logo=java&logoColor=white"/>
<img src="https://img.shields.io/badge/OOP-Design_Patterns-4CAF50?style=for-the-badge"/>
<br/><br/>
🅿️ SmartPark — Parking Management System
> A full-featured, JavaFX-powered Smart Parking Management System built as a university OOP project.  
> Supports multi-vehicle types, EV charging, real-time slot management, billing, and admin/user roles — all through a polished GUI.
<br/>
![GitHub stars](https://img.shields.io/github/stars/YOUR_USERNAME/SmartPark?style=social)
![GitHub forks](https://img.shields.io/github/forks/YOUR_USERNAME/SmartPark?style=social)
</div>
---
📌 Table of Contents
✨ Features
🏗️ Project Architecture
🧠 OOP Concepts Demonstrated
📂 Package Structure
🖥️ Screenshots
🚀 Getting Started
👥 Team
📄 License
---
✨ Features
👤 User Side
Feature	Description
🚗 Park Vehicle	Register Car, Bike, Truck, or EV into an available slot
🔍 Find Vehicle	Look up any parked vehicle by plate number
🚪 Exit & Bill	Automated fare calculation on exit with printed receipt
⚡ EV Charging	Request charging on arrival, choose per-minute or per-percent mode
🗺️ Slot Grid View	Visual real-time map of all parking slots and their status
🔐 Admin Side
Feature	Description
📊 Admin Dashboard	Birds-eye view of occupancy, revenue, and alerts
🅿️ Manage Slots	Add, remove, or change slot types dynamically
💰 Parking Rates	Configure rates per vehicle type on the fly
⚡ EV Charging Rates	Set per-minute and per-percent EV charging prices
🔋 Monitor EV	Track charging sessions across all EV slots in real time
🧾 Billing History	Full searchable/sortable log of every completed transaction
📈 Generate Reports	Export occupancy and revenue summaries
---
🏗️ Project Architecture
```
SmartPark
├── Presentation Layer    →  JavaFX + Scene Builder (FXML + CSS)
├── Controller Layer      →  15 FXML Controllers (MVC pattern)
├── Business Logic Layer  →  Vehicles, ParkingLot, Billing, Admin
└── Persistence Layer     →  Java Serialization (FileManager)
```
The application follows the MVC (Model-View-Controller) pattern:
Model — `Vehicle`, `ParkingLot`, `ParkingSlot`, `Bill`, `Admin`
View — FXML files styled with CSS dark theme
Controller — One controller per screen, wired via `SceneManager`
State is centralized in `AppState`, a singleton-style class that holds the live `ParkingLot`, `Admin`, and billing history, and persists everything to disk on shutdown via `FileManager`.
---
🧠 OOP Concepts Demonstrated
This project was built as a second-semester Java OOP coursework. Every major OOP pillar is represented:
Concept	Where it's used
Inheritance	`Car`, `Bike`, `Truck`, `EV` all extend `Vehicle`
Abstraction	`Vehicle` is abstract with `getRate()` and `getType()` as abstract methods
Encapsulation	All fields are `private`; accessed via getters/setters
Polymorphism	`ArrayList<Vehicle>` holds mixed vehicle types; each calls its own `getRate()`
Interface	`Chargeable` interface implemented by `EVBill` for charging fee logic
Comparable	`Vehicle implements Comparable<Vehicle>` — sorts by entry time
Comparator	`Bill` uses a `Comparator` for sorting billing history by amount or date
Serialization	`ParkingLot` and `Bill` implement `Serializable` for file persistence
Exception Handling	Custom `ParkingException` thrown for duplicate/invalid entries
MVC Pattern	Full separation of UI (FXML), logic (controllers), and data (model classes)
Composition	`ParkingLot` is composed of `ParkingSlot` objects
Collections	Extensive use of `ArrayList`, `Collections.sort()` throughout
---
📂 Package Structure
```
src/main/java/
│
├── com/smartpark/                  ← Core application
│   ├── MainApp.java                  Entry point (JavaFX Application)
│   ├── AppState.java                 Global state singleton
│   ├── SceneManager.java             Screen navigation helper
│   ├── FileManager.java              Load/save via serialization
│   └── controllers/                  One controller per FXML screen
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
├── Main/                           ← Core screens
│   ├── welcome.fxml
│   ├── login.fxml
│   ├── adminDashboard.fxml
│   └── userDashboard.fxml
├── Admin Features/                 ← Admin screens + CSS
│   ├── manageSlots.fxml
│   ├── parkingRates.fxml
│   ├── billingHistory.fxml
│   ├── generateReport.fxml
│   ├── monitor_ev.fxml
│   ├── ev_charging_rates.fxml
│   ├── darktheme.css
│   └── billingHistory.css
└── User Features/                  ← User screens
    ├── parkVehicle.fxml
    ├── exitVehicle.fxml
    ├── findVehicle.fxml
    ├── evCharging.fxml
    └── slotGridView.fxml
```
---
🖥️ Screenshots
> *(Add screenshots of your running application here)*
Welcome Screen	Admin Dashboard	Slot Grid
![welcome](docs/screenshots/welcome.png)	![admin](docs/screenshots/admin_dashboard.png)	![grid](docs/screenshots/slot_grid.png)

Park Vehicle	Billing History	EV Monitor
![park](docs/screenshots/park_vehicle.png)	![billing](docs/screenshots/billing.png)	![ev](docs/screenshots/ev_monitor.png)
---
🚀 Getting Started
Prerequisites
Java 17 or higher — Download JDK
JavaFX 17 SDK — Download JavaFX
IntelliJ IDEA (recommended) — Download
Clone the Repository
```bash
git clone https://github.com/YOUR_USERNAME/SmartPark.git
cd SmartPark
```
Setup in IntelliJ IDEA
Open IntelliJ → File → Open → select the `SmartPark` folder
Go to File → Project Structure → Libraries → add JavaFX SDK `lib/` folder
Go to Run → Edit Configurations → add these VM options:
```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```
Set the main class to `com.smartpark.MainApp`
Click Run ▶
Default Admin Login
```
Username: Sir Aksam
Password: siraksamisgreat
```
---
👥 Team
Name	Role	GitHub
Sage	Project Lead & Backend Integration	@YOUR_USERNAME
Saad	Backend Developer	@saad_username
Taha	Backend Developer	@taha_username
> University: **COMSATS University Islamabad, Lahore Campus**  
> Course: **Object-Oriented Programming (Java)**  
> Semester: **2nd Semester, 2025**
---
📄 License
This project is submitted as academic coursework. All rights reserved by the team.  
Feel free to use it for learning purposes — a ⭐ is appreciated!
---
<div align="center">
Made with ☕ Java and 💙 by the SmartPark Team
</div>
