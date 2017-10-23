package ru.likada.monitoringdriver.modeTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bumur on 22.04.2017.
 */

public class SleepState {
    @SerializedName("sleepStateId")
    @Expose
    private Integer sleepStateId;
    @SerializedName("sleepStateName")
    @Expose
    private String sleepStateName;

    public Integer getSleepStateId() {
        return sleepStateId;
    }

    public void setSleepStateId(Integer sleepStateId) {
        this.sleepStateId = sleepStateId;
    }

    public String getSleepStateName() {
        return sleepStateName;
    }

    public void setSleepStateName(String sleepStateName) {
        this.sleepStateName = sleepStateName;
    }
}
