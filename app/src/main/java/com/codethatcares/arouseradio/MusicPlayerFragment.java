package com.codethatcares.arouseradio;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.*;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class MusicPlayerFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private ImageView imgView;
    private CardView cardView;
    private Animator diskAnimator;
    private Button mButton;
    private boolean buttonState;


    //do stuff with data
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        new Player().execute("https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3");
    }

    //do stuff with views
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.music_player_fragment, container, false);

        imgView = v.findViewById(R.id.cover_album);
        cardView = v.findViewById(R.id.cover_card_view);
        mButton = v.findViewById(R.id.play_stop_button);

        buttonState = false;

        Bitmap originBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R
                .drawable.samplecover));

        Bitmap bitmap = getCircleBitmap(originBitmap);
        //let bitmap fit parent
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(this).load(bitmap).into(imgView);


        diskAnimator = ObjectAnimator.ofFloat(imgView, "rotation", 0f, 360.0f);
        diskAnimator.setDuration(10000);
        //Set rotation speed to be linear
        diskAnimator.setInterpolator(new LinearInterpolator());
        ((ObjectAnimator) diskAnimator).setRepeatCount(-1);
        ((ObjectAnimator) diskAnimator).setRepeatMode(ValueAnimator.RESTART);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!buttonState){
                    if(diskAnimator.isStarted()){
                        diskAnimator.resume();
                    }else{
                        diskAnimator.start();
                    }
                    buttonState = true;
                }else{
                    diskAnimator.pause();
                    buttonState = false;
                }
            }
        });




        return v;
    }

    /**
     * Crop the square bitmap into square
     * @param bitmap
     * @return
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        bitmap = cropBitmap(bitmap);
        try {
            Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(circleBitmap);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            float roundPx = 0.0f;
            roundPx = bitmap.getWidth();
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, rect, paint);
            return circleBitmap;
        } catch (Exception e) {
            return bitmap;
        }
    }

    /**
     * Crop the bitmap into square
     * @param bitmap
     * @return
     */
    public static Bitmap cropBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int cropWidth = w >= h ? h : w;

        return Bitmap.createBitmap(bitmap, (bitmap.getWidth() - cropWidth) / 2,
                (bitmap.getHeight() - cropWidth) / 2, cropWidth, cropWidth);
    }

        class Player extends AsyncTask<String, Void, Boolean> {
            @Override
            protected Boolean doInBackground(String... args) {
                Boolean prepared;
                try {
                    mediaPlayer.setDataSource(args[0]);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            Log.e("test", "on complete");
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                        }
                    });
                    mediaPlayer.prepare();
                    prepared = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    prepared = false;
                }
                return prepared;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                mediaPlayer.start();
            }
        }
    }
