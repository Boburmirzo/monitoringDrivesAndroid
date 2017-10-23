
package ru.likada.monitoringdriver.modeTest;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Drive {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("serial")
    @Expose
    private String serial;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("loadTime")
    @Expose
    private String loadTime;
    @SerializedName("unloadTime")
    @Expose
    private String unloadTime;
    @SerializedName("currentState")
    @Expose
    private CurrentState currentState;
    @SerializedName("nextState")
    @Expose
    private NextState nextState;
    @SerializedName("stopState")
    @Expose
    private StopState stopState;
    @SerializedName("continueState")
    @Expose
    private ContinueState continueState;
    @SerializedName("cancelState")
    @Expose
    private CancelState cancelState;
    @SerializedName("sleepState")
    @Expose
    private SleepState sleepState;
    @SerializedName("toiletState")
    @Expose
    private ToiletState toiletState;
    @SerializedName("invalidOrderState")
    @Expose
    private InvalidOrderState invalidOrderState;
    @SerializedName("breakingState")
    @Expose
    private BreakingState breakingState;
    @SerializedName("product")
    @Expose
    private String product;
    @SerializedName("vehicle")
    @Expose
    private String vehicle;
    @SerializedName("logisticianNameShort")
    @Expose
    private String logisticianNameShort;
    @SerializedName("logisticianNameFull")
    @Expose
    private String logisticianNameFull;
    @SerializedName("logisticianPhone")
    @Expose
    private Object logisticianPhone;
    @SerializedName("driver")
    @Expose
    private String driver;
    @SerializedName("sourceWrappers")
    @Expose
    private List<SourceWrapper> sourceWrappers = null;
    @SerializedName("destinationWrappers")
    @Expose
    private List<DestinationWrapper> destinationWrappers = null;
    @SerializedName("field")
    @Expose
    private Field field;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("description")
    @Expose
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(String loadTime) {
        this.loadTime = loadTime;
    }

    public String getUnloadTime() {
        return unloadTime;
    }

    public void setUnloadTime(String unloadTime) {
        this.unloadTime = unloadTime;
    }

    public CurrentState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(CurrentState currentState) {
        this.currentState = currentState;
    }

    public NextState getNextState() {
        return nextState;
    }

    public void setNextState(NextState nextState) {
        this.nextState = nextState;
    }

    public StopState getStopState() {
        return stopState;
    }

    public void setStopState(StopState stopState) {
        this.stopState = stopState;
    }

    public ContinueState getContinueState() {
        return continueState;
    }

    public void setContinueState(ContinueState continueState) {
        this.continueState = continueState;
    }

    public CancelState getCancelState() {
        return cancelState;
    }

    public void setCancelState(CancelState cancelState) {
        this.cancelState = cancelState;
    }

    public SleepState getSleepState() {
        return sleepState;
    }

    public ToiletState getToiletState() {
        return toiletState;
    }

    public void setToiletState(ToiletState toiletState) {
        this.toiletState = toiletState;
    }

    public InvalidOrderState getInvalidOrderState() {
        return invalidOrderState;
    }

    public void setInvalidOrderState(InvalidOrderState invalidOrderState) {
        this.invalidOrderState = invalidOrderState;
    }

    public BreakingState getBreakingState() {
        return breakingState;
    }

    public void setBreakingState(BreakingState breakingState) {
        this.breakingState = breakingState;
    }

    public void setSleepState(SleepState sleepState) {
        this.sleepState = sleepState;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getLogisticianNameShort() {
        return logisticianNameShort;
    }

    public void setLogisticianNameShort(String logisticianNameShort) {
        this.logisticianNameShort = logisticianNameShort;
    }

    public String getLogisticianNameFull() {
        return logisticianNameFull;
    }

    public void setLogisticianNameFull(String logisticianNameFull) {
        this.logisticianNameFull = logisticianNameFull;
    }

    public Object getLogisticianPhone() {
        return logisticianPhone;
    }

    public void setLogisticianPhone(Object logisticianPhone) {
        this.logisticianPhone = logisticianPhone;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public List<SourceWrapper> getSourceWrappers() {
        return sourceWrappers;
    }

    public void setSourceWrappers(List<SourceWrapper> sourceWrappers) {
        this.sourceWrappers = sourceWrappers;
    }

    public List<DestinationWrapper> getDestinationWrappers() {
        return destinationWrappers;
    }

    public void setDestinationWrappers(List<DestinationWrapper> destinationWrappers) {
        this.destinationWrappers = destinationWrappers;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
