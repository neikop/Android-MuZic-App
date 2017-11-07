package project.qhk.fpt.edu.vn.muzic.models.api_models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by WindzLord on 10/29/2017.
 */

public class MediaFeed {

    @SerializedName("feed")
    private Feed mediaFeed;

    public ArrayList<Feed.Entry> getTopSongList() {
        return mediaFeed.songList;
    }

    public class Feed {

        @SerializedName("entry")
        private ArrayList<Entry> songList;

        public class Entry {

            @SerializedName("im:name")
            private Name name;

            @SerializedName("im:artist")
            private Artist artist;

            @SerializedName("im:image")
            private ArrayList<Image> imageList;

            public String getName() {
                return name.label;
            }

            public String getArtist() {
                return artist.label;
            }

            public String getImageLink() {
                return imageList.get(imageList.size() - 1).label;
            }

            public class Name {

                @SerializedName("label")
                private String label;
            }

            public class Artist {

                @SerializedName("label")
                private String label;
            }

            public class Image {

                @SerializedName("label")
                private String label;
            }

        }
    }
}
