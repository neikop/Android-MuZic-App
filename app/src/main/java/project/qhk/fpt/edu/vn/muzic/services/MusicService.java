package project.qhk.fpt.edu.vn.muzic.services;

import okhttp3.RequestBody;
import project.qhk.fpt.edu.vn.muzic.models.api_models.LoginResult;
import project.qhk.fpt.edu.vn.muzic.models.api_models.MediaFeed;
import project.qhk.fpt.edu.vn.muzic.models.api_models.PlaylistResult;
import project.qhk.fpt.edu.vn.muzic.models.api_models.Result;
import project.qhk.fpt.edu.vn.muzic.models.api_models.SearchResult;
import project.qhk.fpt.edu.vn.muzic.models.api_models.SongMp3;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET("/search/tracks")
    Call<SearchResult> getSearchResult(@Query("client_id") String clientId, @Query("limit") int limit, @Query("q") String query);

    @POST("/api/user/register")
    Call<Result> getRegisterResult(@Body RequestBody account);

    @POST("/api/song/addToPlaylist")
    Call<Result> addToPlaylist(@Body RequestBody account);
//     Request body truyen len {
    //      "token": lấy token từ preference,
//          "playlistId": "",
//	        "playlistName": "pl of kien3",
//          "song" : {
//                  "name": "Tuy Am",
//                "artist": "Xesi-Masew-NhatNguyen",
//                "url": "http://f9.stream.nixcdn.com/d5790123e81531bc39b99f4a7669b9d8/59f9874d/NhacCuaTui949/TuyAm-XesiMasewNhatNguyen-5132651.mp3",
//                "thumbnail": "https://i.ytimg.com/vi/EV-91JV4Fws/maxresdefault.jpg"
//    }}
    // nếu add vào1 new playlist thì để trống playlistId và truyền tên mới lên,
    // nếu add vào playlist có sẵn thì truyền _id của object Playlist vào trường playlistId

    @PUT("/api/song/removeFromPlaylist")
    Call<Result> removeFromPlaylist(@Body RequestBody account);
    // Request body truyen len: {
    //          "playlistId": "59f98651d0a5681b5030113e", _id của Playlist
	//          "songId": "59f98dbf117ba63578301afd"    _id của Song
    // }

    @POST("/api/playlist/syncPlaylist")
    Call<PlaylistResult> syncPlaylist(@Body RequestBody account);
    // request body truyen len: xem ảnh e gửi
//    {
//        "token":,
    //      "playlists: [
    //          {
    //
    //          }
    //      ]
//    }

    @PUT("/api/playlist/remove")
    Call<Result> removePlaylist(@Body RequestBody account);
//    {
//	"playlistId": "59fe9d239088c93c08e75443"
//}

    @POST("/api/playlist/rename")
    Call<Result> renamePlaylist(@Body RequestBody account);
//    {
//        "playlistId": "59fe9d239088c93c08e75443",
//            "playlistName": "moi rename"
//    }

    @POST("/api/playlist/getPlaylistByUser")
    Call<PlaylistResult> getPlaylistByUser(@Body RequestBody token);
    // Request body truyeenf leen : {"token": lay token tu preferences}
}
