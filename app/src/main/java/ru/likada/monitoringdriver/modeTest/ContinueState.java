
package ru.likada.monitoringdriver.modeTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContinueState {

    @SerializedName("continueStateId")
    @Expose
    private Integer continueStateId;
    @SerializedName("continueStateName")
    @Expose
    private String continueStateName;

    public Integer getContinueStateId() {
        return continueStateId;
    }

    public void setContinueStateId(Integer continueStateId) {
        this.continueStateId = continueStateId;
    }

    public String getContinueStateName() {
        return continueStateName;
    }

    public void setContinueStateName(String continueStateName) {
        this.continueStateName = continueStateName;
    }

}
