package ru.likada.monitoringdriver.modeTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bumur on 09.05.2017.
 */

public class ToiletState {
    @SerializedName("toiletStateId")
    @Expose
    private Integer toiletStateId;
    @SerializedName("sleepStateName")
    @Expose
    private String toiletStateName;

    public Integer getToiletStateId() {
        return toiletStateId;
    }

    public void setToiletStateId(Integer toiletStateId) {
        this.toiletStateId = toiletStateId;
    }

    public String getToiletStateName() {
        return toiletStateName;
    }

    public void setToiletStateName(String toiletStateName) {
        this.toiletStateName = toiletStateName;
    }
}
