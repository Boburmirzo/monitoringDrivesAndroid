
package ru.likada.monitoringdriver.modeTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentState {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("typeId")
    @Expose
    private Integer typeId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("modifiedDate")
    @Expose
    private String modifiedDate;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("checkContinue")
    @Expose
    private boolean checkContinue;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCheckContinue() {
        return checkContinue;
    }

    public void setCheckContinue(boolean checkContinue) {
        this.checkContinue = checkContinue;
    }
}
