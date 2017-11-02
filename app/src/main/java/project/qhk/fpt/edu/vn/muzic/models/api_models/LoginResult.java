package project.qhk.fpt.edu.vn.muzic.models.api_models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WindzLord on 11/1/2017.
 */

public class LoginResult {

    @SerializedName("status")
    private boolean success;

    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private User user;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return user.name;
    }

    public String getMessage() {
        return message;
    }

    public class User {

        @SerializedName("_id")
        private String id;

        @SerializedName("username")
        private String username;

        @SerializedName("name")
        private String name;

        @SerializedName("email")
        private String email;

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }

    public User getUser() {
        return user;
    }
}
