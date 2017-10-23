
package ru.likada.monitoringdriver.modeTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StopState {

    @SerializedName("stopeStateId")
    @Expose
    private Integer stopeStateId;
    @SerializedName("stopStateName")
    @Expose
    private String stopStateName;

    public Integer getStopeStateId() {
        return stopeStateId;
    }

    public void setStopeStateId(Integer stopeStateId) {
        this.stopeStateId = stopeStateId;
    }

    public String getStopStateName() {
        return stopStateName;
    }

    public void setStopStateName(String stopStateName) {
        this.stopStateName = stopStateName;
    }

}
