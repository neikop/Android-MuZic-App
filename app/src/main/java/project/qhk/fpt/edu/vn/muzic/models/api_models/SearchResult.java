package project.qhk.fpt.edu.vn.muzic.models.api_models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by QuangTM on 02/11/2017.
 */

public class SearchResult {
    @SerializedName("collection")
    private ArrayList results;

    @SerializedName("total_results")
    private String totalResult;

    @SerializedName("next_href")
    private String nextHref;


    public class SearchSong {

        @SerializedName("id")
        private String id;

        @SerializedName("title")
        private String title;

        @SerializedName("permalink")
        private String permalink;

        @SerializedName("uri")
        private String url;

        public String getStream() {
            return this.url + "/stream?client_id=MbFtrpTYuwoPYLnPGQIFPahc1TNeVFnu";
        }
    }
}
