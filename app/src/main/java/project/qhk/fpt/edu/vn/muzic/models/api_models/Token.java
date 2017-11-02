package project.qhk.fpt.edu.vn.muzic.models.api_models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WindzLord on 11/2/2017.
 */

public class Token {

    @SerializedName("token")
    private String token;

    public Token(String token) {
        this.token = token;
    }
}
