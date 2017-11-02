package project.qhk.fpt.edu.vn.muzic.services;

import okhttp3.RequestBody;
import project.qhk.fpt.edu.vn.muzic.models.api_models.LoginResult;
import project.qhk.fpt.edu.vn.muzic.models.api_models.MediaFeed;
import project.qhk.fpt.edu.vn.muzic.models.api_models.PlaylistResult;
import project.qhk.fpt.edu.vn.muzic.models.api_models.SearchResult;
import project.qhk.fpt.edu.vn.muzic.models.api_models.SongMp3;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by WindzLord on 11/29/2016.
 */

public interface MusicService {

    @GET("/us/rss/topSongs/limit=50/genre={id}/explicit=true/json")
    Call<MediaFeed> getMediaFeed(@Path("id") String id);

    @GET("/services/api/audio")
    Call<SongMp3> getSongMp3(@Query("search_terms") String search);

    @POST("/api/user/login")
    Call<LoginResult> getLoginResult(@Body RequestBody account);

    @POST("/api/playlist/getPlaylistByUser")
    Call<PlaylistResult> getPlaylistByUser(@Body RequestBody token);

    @GET("/search/tracks?client_id=MbFtrpTYuwoPYLnPGQIFPahc1TNeVFnu&limit=10&offset=0")
    Call<SearchResult> getSearchResult(@Query("q") String query);


}
