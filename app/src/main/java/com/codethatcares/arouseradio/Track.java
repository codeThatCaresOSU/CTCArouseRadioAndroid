package com.codethatcares.arouseradio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Track {
    private String artist;
    private String trackName;
    private String albumName;
    private Bitmap albumArt;
    private String artUrl;

    public Track(JSONObject jsonObject) {
        processJsonData(jsonObject);
    }

    public String getArtist() {
        return artist;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    public void downladAlbumArt(NetworkCallbacks callbacks) {
        DownloadImageTask task = new DownloadImageTask(callbacks);
        task.execute(artUrl);
    }

    private void processJsonData(JSONObject jsonObject) {
        try {
            JSONObject recentTracks = jsonObject.getJSONObject("recenttracks");
            JSONArray track = recentTracks.getJSONArray("track");
            JSONObject trackFront = track.getJSONObject(0);
            artist = trackFront.getJSONObject("artist").getString("#text");
            albumName = trackFront.getJSONObject("album").getString("#text");
            trackName = trackFront.getString("name");
            artUrl = trackFront.getJSONArray("image").getJSONObject(3).getString("#text");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
