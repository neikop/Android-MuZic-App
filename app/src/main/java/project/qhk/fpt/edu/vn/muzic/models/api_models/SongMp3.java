package project.qhk.fpt.edu.vn.muzic.models.api_models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WindzLord on 10/29/2017.
 */

public class SongMp3 {

    @SerializedName("success")
    private int success;

    @SerializedName("data")
    private Source source;

    public String getStream() {
        return source.url;
    }

    public class Source {

        @SerializedName("url")
        private String url;

    }

}
