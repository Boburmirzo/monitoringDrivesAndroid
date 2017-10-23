package ru.likada.monitoringdriver.modeTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bumur on 10.05.2017.
 */

public class Message {
    @SerializedName("message")
    @Expose
    private String messageValue;
}
