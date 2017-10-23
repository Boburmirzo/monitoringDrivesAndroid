package ru.likada.monitoringdriver.modeTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bumur on 09.05.2017.
 */

public class BreakingState {
    @SerializedName("breakingStateId")
    @Expose
    private Integer breakingStateId;
    @SerializedName("breakingStateName")
    @Expose
    private String breakingStateName;

    public Integer getBreakingStateId() {
        return breakingStateId;
    }

    public void setBreakingStateId(Integer breakingStateId) {
        this.breakingStateId = breakingStateId;
    }

    public String getBreakingStateName() {
        return breakingStateName;
    }

    public void setBreakingStateName(String breakingStateName) {
        this.breakingStateName = breakingStateName;
    }
}
