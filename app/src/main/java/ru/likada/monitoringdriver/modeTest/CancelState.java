
package ru.likada.monitoringdriver.modeTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancelState {

    @SerializedName("cancelStateId")
    @Expose
    private Integer cancelStateId;
    @SerializedName("cancelStateName")
    @Expose
    private String cancelStateName;

    public Integer getCancelStateId() {
        return cancelStateId;
    }

    public void setCancelStateId(Integer cancelStateId) {
        this.cancelStateId = cancelStateId;
    }

    public String getCancelStateName() {
        return cancelStateName;
    }

    public void setCancelStateName(String cancelStateName) {
        this.cancelStateName = cancelStateName;
    }

}
