package com.codethatcares.arouseradio;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class RotatingAlbumCover {
    private ImageView viewHolder;
    private Bitmap albumArt;
    private Animator diskAnimator;
    int red;
    int green;
    int blue;

    /**
     * Create the 'rotation image art' object
     * @param viewHolder -> the image view to set the circular rotation image to
     * @param albumArt -> the image of the album art
     * @param context -> application context
     */
    public RotatingAlbumCover(ImageView viewHolder, Bitmap albumArt, Context context) {
        this.viewHolder = viewHolder;
        this.albumArt = getCircleBitmap(albumArt);
        this.viewHolder.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(context).load(this.albumArt).into(this.viewHolder);
        diskAnimator = ObjectAnimator.ofFloat(this.viewHolder, "rotation", 0f, 360.0f);
        diskAnimator.setDuration(3000);
        //Set rotation speed to be linear
        diskAnimator.setInterpolator(new LinearInterpolator());
        ((ObjectAnimator) diskAnimator).setRepeatCount(-1);
        ((ObjectAnimator) diskAnimator).setRepeatMode(ValueAnimator.RESTART);
        getAverageColor(albumArt);
    }

    /**
     * Crop the square bitmap into circle
     * @param bitmap
     * @return
     */
    private Bitmap getCircleBitmap(Bitmap bitmap) {
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
     * Crop the bitmap
     * @param bitmap
     * @return
     */
    private Bitmap cropBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int cropWidth = w >= h ? h : w;

        return Bitmap.createBitmap(bitmap, (bitmap.getWidth() - cropWidth) / 2,
                (bitmap.getHeight() - cropWidth) / 2, cropWidth, cropWidth);
    }

    /**
     *
     * @return whether or not rotation has started
     */
    public boolean isStarted() {
        return diskAnimator.isStarted();
    }

    /**
     * start the animation of the circle
     */
    public void startAnimation() {
        diskAnimator.start();
    }

    /**
     * resume the animation (must have be started already)
     */
    public void resumeAnimation() {
        diskAnimator.resume();
    }

    /**
     * pause the animation
     */
    public void pauseAnimation() {
        diskAnimator.pause();
    }

    /**
     * gets the average color from the album art
     * @param album -> the album art
     */
    private void getAverageColor(Bitmap album) {
        long redBucket = 0;
        long greenBucket = 0;
        long blueBucket = 0;
        long pixelCount = 0;
        for (int y = 0; y < album.getHeight(); y++) {
            for (int x = 0; x < album.getWidth(); x++) {
                int c = album.getPixel(x, y);
                pixelCount++;
                redBucket += Color.red(c);
                greenBucket += Color.green(c);
                blueBucket += Color.blue(c);
            }
        }
        red = (int) (redBucket / pixelCount);
        green = (int) (greenBucket / pixelCount);
        blue = (int) (blueBucket / pixelCount);
    }

    /**
     *
     * @return the average color of the album
     */
    public int getAverageAlbumCover() {
        return Color.rgb(red, green, blue);
    }

    /**
     *
     * @return the complementary of the average color
     */
    public int getComplementaryColor() {
        int maxPlusMin = max(red, green, blue) + min(red, green, blue);
        int rPrime = maxPlusMin - red;
        int bPrime = maxPlusMin - blue;
        int gPrime = maxPlusMin - green;
        return Color.rgb(rPrime, gPrime, bPrime);
    }

    /**
     * the max of three ints
     * @param r
     * @param b
     * @param g
     * @return
     */
    private int max(int r, int b, int g) {
        return Math.max(Math.max(r, g), b);
    }

    /**
     * the min of three ints
     * @param r
     * @param b
     * @param g
     * @return
     */
    private int min(int r, int b, int g) {
        return Math.min(Math.min(r, g), b);
    }
}
