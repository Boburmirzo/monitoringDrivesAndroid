
package ru.likada.monitoringdriver.modeTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SourceWrapper {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("fromWhere")
    @Expose
    private String fromWhere;
    @SerializedName("object")
    @Expose
    private String object;
    @SerializedName("address")
    @Expose
    private Object address;
    @SerializedName("contact")
    @Expose
    private Object contact;
    @SerializedName("coordinates")
    @Expose
    private String coordinates;
    @SerializedName("description")
    @Expose
    private Object description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromWhere() {
        return fromWhere;
    }

    public void setFromWhere(String fromWhere) {
        this.fromWhere = fromWhere;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
    }

    public Object getContact() {
        return contact;
    }

    public void setContact(Object contact) {
        this.contact = contact;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

}
