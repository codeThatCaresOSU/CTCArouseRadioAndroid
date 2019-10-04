package com.codethatcares.arouseradio;

import android.graphics.Bitmap;
import org.json.JSONObject;

public interface NetworkCallbacks {
    /**
     * called when the json is done loading
     * @param json
     */
    public void postJsonDownload(JSONObject json);
    public void postImageDownload(Bitmap image);
}
