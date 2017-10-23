package ru.likada.monitoringdriver.modeTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bumur on 08.05.2017.
 */

public class Field {
    @SerializedName("countLoad")
    @Expose
    private String countLoad;
    @SerializedName("countUnload")
    @Expose
    private String countUnload;
    @SerializedName("description")
    @Expose
    private String description;

    public String getCountLoad() {
        return countLoad;
    }

    public void setCountLoad(String countLoad) {
        this.countLoad = countLoad;
    }

    public String getCountUnload() {
        return countUnload;
    }

    public void setCountUnload(String countUnload) {
        this.countUnload = countUnload;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
