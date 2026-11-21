# SmartWatt – Intelligent Energy Consumption & Optimization Platform

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Chart.js](https://img.shields.io/badge/Chart.js-4.x-pink.svg)](https://www.chartjs.org/)
[![Status](https://img.shields.io/badge/Stage-2%20Functional%20Prototype-cyan.svg)](#)

> **SmartWatt** is an intelligent electricity monitoring, billing estimation, and optimization platform that monitors electricity consumption across three independently monitored rooms, tracks active appliance loads in real-time, calculates energy consumption using standard electrical formulations, identifies heavy power consumers, triggers safety alerts, and generates rule-based energy-saving recommendations.

---

## 📌 Features

### 1. Main Telemetry Dashboard
- **Total Power Consumption:** Live real-time wattage ($W$).
- **Total Cumulative Energy:** Accurate energy calculation in kilowatt-hours ($kWh$).
- **Estimated Electricity Bill:** Dynamic billing estimation in Indian Rupees ($₹$).
- **Active Appliance Counter:** Live tracking of appliances switched ON across sections.
- **System Health Status:** Automatic categorization (`NORMAL`, `MODERATE`, `HIGH_CONSUMPTION`, `CRITICAL`).

### 2. Three-Room Independent Monitoring
Three independently monitored physical sections:
- **Room 1 – Living Room:** Living Room LED Bulb (15W), Ceiling Fan (75W), Smart TV (120W)
- **Room 2 – Bedroom:** Bedside Warm Bulb (15W), Ceiling Fan (75W), Inverter Air Conditioner (1500W)
- **Room 3 – Kitchen & Utility:** Kitchen LED Tube Light (25W), Exhaust Fan (50W), Double-Door Refrigerator (200W)
- **Individual Controls:** Instant ON/OFF toggle switches that update power immediately and persist state to MySQL.

### 3. Realistic Simulated Sensor Telemetry (Stage 2 Prototype)
- Generates realistic simulated readings for **Voltage** ($\approx 230\text{V} \pm 2\text{V}$), **Current** ($I = P / V$), **Power** (Watts), **Appliance Status**, and **Timestamps**.
- All readings are clearly labeled as **simulated** in the UI.
- Designed with clean REST ingestion endpoints ready for direct **ESP32 + ACS712** current sensor hardware integration in Stage 3.

### 4. Energy Analytics & Formulations
- **Energy Formulation:**
  $$\text{Energy (kWh)} = \frac{\text{Power (Watts)} \times \text{Time (Hours)}}{1000}$$
- **Electricity Bill Estimation:**
  $$\text{Bill (₹)} = \text{Total Energy (kWh)} \times \text{Configurable Tariff (₹/kWh)}$$
- **Visual Charts (Chart.js):**
  - **Real-Time Power Trend (Line Chart):** Rolling live wattage trend.
  - **Room-Wise Energy Share (Doughnut Chart):** Distribution of energy across rooms.
  - **Appliance Wattage Comparison (Horizontal Bar Chart):** Active vs. rated load comparison.
  - **7-Day Historical Energy Consumption (Bar Chart):** Past week energy and cost logs.

### 5. Intelligent Rule-Based Recommendations
- Evaluates real telemetry without false claims of ML:
  - **Long-Running Loads:** Detects fans or lights left running continuously for over 4 hours.
  - **Heavy Load Optimization:** Suggests eco-mode or thermostat adjustments (e.g., 24°C on AC) with estimated monthly ₹ savings.
  - **Room Imbalance:** Alerts when one room accounts for $>60\%$ of total household load.
  - **Efficiency Tips:** Practical advice for BLDC fans and 5-star appliances.

### 6. Configurable System Alerts & Thresholds
- **Safety Limits:** User-configurable thresholds for Maximum House Power, Room Power Limits, and Continuous Run Hours.
- **Severity Tiers:** `INFO`, `WARNING`, and `CRITICAL` alerts with single-click dismissal and bulk resolution.

---

## 🗄️ Database Architecture (`smartwatt_db`)

The relational database consists of 7 normalized MySQL tables:

```
smartwatt_db
├── rooms                  # Monitored building sections
├── appliances             # Individual appliances with base and current wattage
├── readings               # Sensor telemetry logs (Voltage, Current, Watts, Status)
├── energy_usage           # Aggregated daily & monthly kWh and cost records
├── alerts                 # Active and historical threshold violation alerts
├── recommendations        # Rule-based optimization suggestions
└── settings               # Configurable key-value system settings (Tariff, Thresholds)
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 21** (JDK 21 LTS)
- **Maven 3.8+** (or use included `mvnw.cmd`)
- **MySQL Server 8.0+**

### 1. Database Setup
Ensure MySQL is running and create the database (or let Spring Boot create it automatically):
```sql
CREATE DATABASE IF NOT EXISTS smartwatt_db;
```

### 2. Configuration
Open `src/main/resources/application.properties` and verify your MySQL credentials:
```properties
server.port=8085
spring.datasource.url=jdbc:mysql://localhost:3306/smartwatt_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 3. Build & Run
Run the application using Maven:
```bash
# Compile and build package
mvn clean package -DskipTests

# Run the Spring Boot application
java -jar target/smartwatt-1.0.0.jar
```

### 4. Open Dashboard
Open your web browser and navigate to:
```
http://localhost:8085
```

---

## 📡 REST API Reference

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/dashboard` | Returns global telemetry, status, rooms, alerts, and recommendations. |
| `GET` | `/api/dashboard/rooms` | Returns summary and appliance status for all 3 rooms. |
| `GET` | `/api/appliances` | Returns all registered appliances. |
| `POST` | `/api/appliances/{id}/toggle?state={bool}` | Toggles appliance state, updates wattage, and recalculates totals. |
| `GET` | `/api/analytics` | Returns room share, appliance comparisons, and 7-day daily trends. |
| `GET` | `/api/alerts` | Lists active unresolved safety alerts. |
| `POST` | `/api/alerts/{id}/resolve` | Marks an alert as resolved. |
| `POST` | `/api/alerts/resolve-all` | Resolves all active alerts. |
| `GET` | `/api/recommendations` | Returns active rule-based energy savings suggestions. |
| `POST` | `/api/recommendations/generate` | Triggers immediate heuristic evaluation. |
| `GET` | `/api/settings` | Returns current tariff and safety threshold values. |
| `PUT` | `/api/settings` | Updates tariff rate (₹/kWh) and consumption thresholds. |
| `GET` | `/api/simulation/status` | Returns simulation ticker status and grid voltage. |
| `POST` | `/api/simulation/toggle` | Toggles simulation engine ON / OFF. |

---

## 🔮 Future Roadmap (Stage 3 Hardware Integration)
- Connect **ESP32 microcontroller** with **ACS712 current sensors** and **ZMPT101B voltage sensors**.
- Ingest live electrical telemetry directly via `POST /api/readings` or MQTT broker integration.
- Hardware relay switching for physical automation.

---

## 📄 License
This project was developed for the **SmartWatt Hackathon Challenge**.
