package com.codethatcares.arouseradio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class MusicPlayerFragment extends Fragment {

    //VIEWS
    private ImageView albumImageView;
    private ConstraintLayout root;
    private TextView songTextView;
    private TextView albumTextView;
    private TextView artistTextView;

    //DATA
    private boolean buttonPressed;

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
        root = v.findViewById(R.id.root);
        albumImageView = v.findViewById(R.id.cover_album);
        songTextView = v.findViewById(R.id.name_song);
        albumTextView = v.findViewById(R.id.album_name);
        artistTextView = v.findViewById(R.id.name_artist);

        //create the album art object based on the album art
        Bitmap originBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.samplecover));
        final RotatingAlbumCover rotatingAlbumCover = new RotatingAlbumCover(albumImageView, originBitmap, getContext());
        setViewColors(rotatingAlbumCover);

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
        return v;
    }

    /**
     * Set the color of the background and the text views
     * based on the album art
     * @param rac ->
     *            the album art object to extract the colors from
     */
    private void setViewColors(RotatingAlbumCover rac) {
        root.setBackgroundColor(rac.getAverageAlbumCover());
        songTextView.setTextColor(rac.getComplementaryColor());
        albumTextView.setTextColor(rac.getComplementaryColor());
        artistTextView.setTextColor(rac.getComplementaryColor());
    }

}
