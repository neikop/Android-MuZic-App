package project.qhk.fpt.edu.vn.muzic.models.api_models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import project.qhk.fpt.edu.vn.muzic.Logistic;

/**
 * Created by QuangTM on 02/11/2017.
 */

public class SearchResult {

    @SerializedName("collection")
    private ArrayList<SearchSong> results;

    @SerializedName("total_results")
    private String totalResult;

    @SerializedName("next_href")
    private String nextHref;

    public ArrayList<SearchSong> getResults() {
        return results;
    }

    public class SearchSong {

        @SerializedName("title")
        private String title;

        @SerializedName("artwork_url")
        private String imageLink;

        @SerializedName("uri")
        private String url;

        public String getTitle() {
            return title;
        }

        public String getImageLink() {
            return imageLink;
        }

        public String getStream() {
            return url + "/stream?client_id=" + Logistic.CLIENT_ID;
        }
    }
}
