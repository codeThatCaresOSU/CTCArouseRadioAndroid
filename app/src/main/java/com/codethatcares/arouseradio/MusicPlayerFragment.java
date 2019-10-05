package com.codethatcares.arouseradio;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import org.json.JSONObject;

public class MusicPlayerFragment extends Fragment implements NetworkCallbacks {

    //VIEWS
    private ImageView albumImageView;
    private TextView songTextView;
    private TextView albumTextView;
    private TextView artistTextView;
    private ImageView backgroundImage;

    //DATA
    private boolean buttonPressed;
    private DynamicThemeFromAlbum background;
    private Track currentlyPlaying;
    private RotatingAlbumCover rotatingAlbumCover;

    //do stuff with data
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buttonPressed = false;
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

        DownloadJsonTask getLastfmData = new DownloadJsonTask(this);
        getLastfmData.execute(Constants.SONG_ENDPOINT);

        return v;
    }

    @Override
    public void postJsonDownload(JSONObject json) {
        currentlyPlaying = new Track(json);
        setTextFromSong(currentlyPlaying);
        currentlyPlaying.downladAlbumArt(this);
    }

    @Override
    public void postImageDownload(Bitmap image) {
        rotatingAlbumCover = new RotatingAlbumCover(albumImageView, image, getContext());
        background = new DynamicThemeFromAlbum(image, getContext());
        setViewColors(background);
        backgroundImage.setImageBitmap(background.getBlurredBitmap());
        //click listener to 'pause' and 'play' the rotation
        albumImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!buttonPressed){
                    if(rotatingAlbumCover.isStarted()){
                        rotatingAlbumCover.resumeAnimation();
                    }else{
                        rotatingAlbumCover.startAnimation();
                    }
                    buttonPressed = true;
                } else {
                    rotatingAlbumCover.pauseAnimation();
                    buttonPressed = false;
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
}
