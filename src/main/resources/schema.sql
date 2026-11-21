-- ===================================================================
-- SmartWatt – Database Initialization Script for MySQL
-- Database: smartwatt_db
-- ===================================================================

CREATE DATABASE IF NOT EXISTS smartwatt_db;
USE smartwatt_db;

-- 1. Rooms Table
CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    section_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. Appliances Table
CREATE TABLE IF NOT EXISTS appliances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    icon VARCHAR(50),
    base_wattage DOUBLE NOT NULL,
    is_on BOOLEAN NOT NULL DEFAULT FALSE,
    current_wattage DOUBLE DEFAULT 0.0,
    current_voltage DOUBLE DEFAULT 0.0,
    current_amp DOUBLE DEFAULT 0.0,
    last_status_change DATETIME DEFAULT CURRENT_TIMESTAMP,
    active_duration_hours DOUBLE DEFAULT 0.0,
    total_energy_kwh DOUBLE DEFAULT 0.0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appliance_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

-- 3. Sensor Readings Table (Simulated in Stage 2, IoT ESP32 Ready)
CREATE TABLE IF NOT EXISTS readings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appliance_id BIGINT NOT NULL,
    voltage DOUBLE NOT NULL,
    current_amp DOUBLE NOT NULL,
    power_watt DOUBLE NOT NULL,
    appliance_status BOOLEAN NOT NULL,
    is_simulated BOOLEAN NOT NULL DEFAULT TRUE,
    recorded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reading_appliance FOREIGN KEY (appliance_id) REFERENCES appliances(id) ON DELETE CASCADE
);

-- 4. Energy Usage Table (Daily / Monthly Tracking)
CREATE TABLE IF NOT EXISTS energy_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT,
    appliance_id BIGINT,
    usage_date DATE NOT NULL,
    active_hours DOUBLE NOT NULL DEFAULT 0.0,
    total_kwh DOUBLE NOT NULL DEFAULT 0.0,
    calculated_cost DOUBLE NOT NULL DEFAULT 0.0,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usage_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL,
    CONSTRAINT fk_usage_appliance FOREIGN KEY (appliance_id) REFERENCES appliances(id) ON DELETE SET NULL
);

-- 5. System & Energy Alerts Table
CREATE TABLE IF NOT EXISTS alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    message VARCHAR(500) NOT NULL,
    alert_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    room_name VARCHAR(100),
    appliance_name VARCHAR(100),
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_resolved BOOLEAN NOT NULL DEFAULT FALSE
);

-- 6. Intelligent Recommendations Table
CREATE TABLE IF NOT EXISTS recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(600) NOT NULL,
    category VARCHAR(50) NOT NULL,
    potential_monthly_savings_inr DOUBLE DEFAULT 0.0,
    action_suggested VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 7. System Settings Table
CREATE TABLE IF NOT EXISTS settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
