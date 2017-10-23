package ru.likada.monitoringdriver.modeTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bumur on 10.05.2017.
 */

public class FMCMessageWrapper {

    @SerializedName("messageValue")
    @Expose
    private String messageValue;
    @SerializedName("messageText")
    @Expose
    private String messageText;

    public String getMessageValue() {
        return messageValue;
    }

    public void setMessageValue(String messageValue) {
        this.messageValue = messageValue;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
