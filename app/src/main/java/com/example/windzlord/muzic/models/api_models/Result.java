package com.example.windzlord.muzic.models.api_models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by QuangTM on 02/11/2017.
 */

public class Result {

    @SerializedName("status")
    private boolean success;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

}
