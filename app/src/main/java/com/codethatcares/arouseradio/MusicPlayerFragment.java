package com.codethatcares.arouseradio;

import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
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
    private boolean buttonPlayed;

    //DATA
    private boolean buttonPressed;
    private DynamicThemeFromAlbum background;
    private Track currentlyPlaying;
    private RotatingAlbumCover rotatingAlbumCover;
    private ImageView imageView;
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
        imageView = v.findViewById(R.id.play_button);

        buttonPlayed = false;

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

        if(rotatingAlbumCover.isStarted()){
            imageView.setImageResource(R.drawable.icon_play_animator);
        }else{
            imageView.setImageResource(R.drawable.icon_pause_animator);
        }
        //click listener to 'pause' and 'play' the rotation
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!buttonPlayed){
                    imageView.setImageResource(R.drawable.icon_pause_animator);
                    if(rotatingAlbumCover.isStarted()){
                        rotatingAlbumCover.resumeAnimation();
                     }else{
                        rotatingAlbumCover.startAnimation();
                    }
                    buttonPlayed = true;
                }else{
                    imageView.setImageResource(R.drawable.icon_play_animator);
                    rotatingAlbumCover.pauseAnimation();
                    buttonPlayed = false;
                }
                Animatable animation = (Animatable) imageView.getDrawable();
                animation.start();
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
