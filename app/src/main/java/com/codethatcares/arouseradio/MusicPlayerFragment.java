package com.codethatcares.arouseradio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import org.json.JSONObject;

public class MusicPlayerFragment extends Fragment implements NetworkCallbacks {

    //VIEWS
    private ImageView albumImageView;
    private TextView songTextView;
    private TextView albumTextView;
    private TextView artistTextView;
    private ImageView backgroundImage;
    private ImageView pausePlayOverlay;

    //DATA
    private boolean buttonPressed;
    private DynamicThemeFromAlbum background;
    private Track currentlyPlaying;
    private RotatingAlbumCover rotatingAlbumCover;

    private MusicPlayerService player;
    boolean serviceBound = false;

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };



    //do stuff with data
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buttonPressed = true;
        playAudio(Constants.MUSIC_ENDPOINT);

    }

    //do stuff with views
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.music_player_fragment, container, false);

        //view binding
        albumImageView = v.findViewById(R.id.cover_album);
        songTextView = v.findViewById(R.id.name_song);
        albumTextView = v.findViewById(R.id.album_name);
        artistTextView = v.findViewById(R.id.name_artist);
        backgroundImage = v.findViewById(R.id.backgroundImage);
        pausePlayOverlay = v.findViewById(R.id.pause_play_vector_overlay);

        DownloadJsonTask getLastfmData = new DownloadJsonTask(this);
        getLastfmData.execute(Constants.SONG_JSON_ENDPOINT);
        return v;
    }

    @Override
    public void postJsonDownload(JSONObject json) {
        Log.e("test", json.toString());
        currentlyPlaying = new Track(json);
        setTextFromSong(currentlyPlaying);
        currentlyPlaying.downladAlbumArt(this);
    }

    @Override
    public void postImageDownload(Bitmap image) {
        rotatingAlbumCover = new RotatingAlbumCover(albumImageView, pausePlayOverlay, image, getContext());
        background = new DynamicThemeFromAlbum(image, getContext());
        rotatingAlbumCover.startAnimation(background.getComplementaryColor());
        setViewColors(background);
        backgroundImage.setImageBitmap(background.getBlurredBitmap());
        //click listener to 'pause' and 'play' the rotation
        albumImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!buttonPressed){
                    if(rotatingAlbumCover.isStarted()){
                        rotatingAlbumCover.resumeAnimation(background.getComplementaryColor());
                    }else{
                        rotatingAlbumCover.startAnimation(background.getComplementaryColor());
                    }
                    buttonPressed = true;
                    player.resumeMedia();
                } else {
                    rotatingAlbumCover.pauseAnimation(background.getComplementaryColor());
                    buttonPressed = false;
                    player.pauseMedia();
                }
            }
        });
    }

    /**
     * Set the color of the background and the text views
     * based on the album art
     * @param background ->
     *            the backround object to extract the colors from
     */
    private void setViewColors(DynamicThemeFromAlbum background) {
        //set the text color based on the background image
        background.setTextviewStyles(songTextView, artistTextView, albumTextView);
        ((MainActivity) getActivity()).setStatusBarColor(background.getStatusBarColorFromBackground());
    }

    private void setTextFromSong(Track track) {
        songTextView.setText(track.getTrackName());
        albumTextView.setText(track.getAlbumName());
        artistTextView.setText(track.getArtist());
    }

    private void playAudio(String media) {
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(getContext(), MusicPlayerService.class);
            playerIntent.putExtra("media", media);
            getActivity().startService(playerIntent);
            getActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
            Log.e("error playing audio", "error starting the media stream");

        }
    }
}
