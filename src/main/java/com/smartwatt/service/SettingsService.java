package com.smartwatt.service;

import com.smartwatt.dto.TariffSettingsDTO;
import com.smartwatt.entity.Setting;
import com.smartwatt.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SettingsService {

    public static final String KEY_TARIFF_RATE = "TARIFF_RATE_INR_PER_KWH";
    public static final String KEY_MAX_POWER_THRESHOLD = "MAX_POWER_THRESHOLD_WATTS";
    public static final String KEY_ROOM_POWER_THRESHOLD = "ROOM_POWER_THRESHOLD_WATTS";
    public static final String KEY_LONG_RUNNING_HOURS = "LONG_RUNNING_HOURS_THRESHOLD";
    public static final String KEY_SIMULATION_ENABLED = "SIMULATION_ENABLED";

    private final SettingRepository settingRepository;

    @Value("${smartwatt.tariff.default-rate-per-kwh:7.50}")
    private Double defaultTariffRate;

    @Value("${smartwatt.threshold.max-power-watts:2500.0}")
    private Double defaultMaxPower;

    @Value("${smartwatt.threshold.room-max-power-watts:1800.0}")
    private Double defaultRoomMaxPower;

    @Value("${smartwatt.threshold.appliance-long-run-hours:4.0}")
    private Double defaultLongRunningHours;

    public SettingsService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public Double getTariffRate() {
        return getDoubleSetting(KEY_TARIFF_RATE, defaultTariffRate);
    }

    public Double getMaxPowerThreshold() {
        return getDoubleSetting(KEY_MAX_POWER_THRESHOLD, defaultMaxPower);
    }

    public Double getRoomPowerThreshold() {
        return getDoubleSetting(KEY_ROOM_POWER_THRESHOLD, defaultRoomMaxPower);
    }

    public Double getLongRunningHoursThreshold() {
        return getDoubleSetting(KEY_LONG_RUNNING_HOURS, defaultLongRunningHours);
    }

    public Boolean isSimulationEnabled() {
        return settingRepository.findBySettingKey(KEY_SIMULATION_ENABLED)
                .map(s -> Boolean.parseBoolean(s.getSettingValue()))
                .orElse(true);
    }

    public TariffSettingsDTO getAllSettings() {
        return new TariffSettingsDTO(
                getTariffRate(),
                getMaxPowerThreshold(),
                getRoomPowerThreshold(),
                getLongRunningHoursThreshold()
        );
    }

    @Transactional
    public TariffSettingsDTO updateSettings(TariffSettingsDTO dto) {
        if (dto.getTariffRatePerKwh() != null && dto.getTariffRatePerKwh() > 0) {
            saveOrUpdateSetting(KEY_TARIFF_RATE, String.valueOf(dto.getTariffRatePerKwh()), "Electricity tariff rate in INR per kWh");
        }
        if (dto.getMaxPowerThresholdWatts() != null && dto.getMaxPowerThresholdWatts() > 0) {
            saveOrUpdateSetting(KEY_MAX_POWER_THRESHOLD, String.valueOf(dto.getMaxPowerThresholdWatts()), "Total house power consumption threshold in Watts");
        }
        if (dto.getRoomPowerThresholdWatts() != null && dto.getRoomPowerThresholdWatts() > 0) {
            saveOrUpdateSetting(KEY_ROOM_POWER_THRESHOLD, String.valueOf(dto.getRoomPowerThresholdWatts()), "Per room power consumption threshold in Watts");
        }
        if (dto.getLongRunningHoursThreshold() != null && dto.getLongRunningHoursThreshold() > 0) {
            saveOrUpdateSetting(KEY_LONG_RUNNING_HOURS, String.valueOf(dto.getLongRunningHoursThreshold()), "Threshold in hours to alert for long-running appliances");
        }
        return getAllSettings();
    }

    @Transactional
    public void setSimulationEnabled(boolean enabled) {
        saveOrUpdateSetting(KEY_SIMULATION_ENABLED, String.valueOf(enabled), "Flag to toggle live sensor simulation");
    }

    @Transactional
    public void saveOrUpdateSetting(String key, String value, String description) {
        Setting setting = settingRepository.findBySettingKey(key)
                .orElse(new Setting(key, value, description));
        setting.setSettingValue(value);
        setting.setDescription(description);
        setting.setUpdatedAt(LocalDateTime.now());
        settingRepository.save(setting);
    }

    private Double getDoubleSetting(String key, Double defaultValue) {
        return settingRepository.findBySettingKey(key)
                .map(s -> {
                    try {
                        return Double.parseDouble(s.getSettingValue());
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }
}
