package ru.likada.monitoringdriver.modeTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bumur on 09.05.2017.
 */

public class InvalidOrderState {
    @SerializedName("invalidOrderStateId")
    @Expose
    private Integer invalidOrderStateId;
    @SerializedName("sleepStateName")
    @Expose
    private String invalidOrderStateName;

    public Integer getInvalidOrderStateId() {
        return invalidOrderStateId;
    }

    public void setInvalidOrderStateId(Integer invalidOrderStateId) {
        this.invalidOrderStateId = invalidOrderStateId;
    }

    public String getInvalidOrderStateName() {
        return invalidOrderStateName;
    }

    public void setInvalidOrderStateName(String invalidOrderStateName) {
        this.invalidOrderStateName = invalidOrderStateName;
    }
}
