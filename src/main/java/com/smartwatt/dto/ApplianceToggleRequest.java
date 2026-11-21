package com.smartwatt.dto;

public class ApplianceToggleRequest {
    private Long applianceId;
    private Boolean isOn;

    public ApplianceToggleRequest() {}

    public ApplianceToggleRequest(Long applianceId, Boolean isOn) {
        this.applianceId = applianceId;
        this.isOn = isOn;
    }

    public Long getApplianceId() {
        return applianceId;
    }

    public void setApplianceId(Long applianceId) {
        this.applianceId = applianceId;
    }

    public Boolean getIsOn() {
        return isOn;
    }

    public void setIsOn(Boolean on) {
        isOn = on;
    }
}
