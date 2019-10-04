package com.codethatcares.arouseradio;

import android.os.AsyncTask;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadJsonTask extends AsyncTask<String, Void, JSONObject> {
    NetworkCallbacks callback;

    public DownloadJsonTask(NetworkCallbacks callback) {
        this.callback = callback;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        StringBuilder sb = new StringBuilder();
        try {
            URL jsonUrl = new URL(strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) jsonUrl.openConnection();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(in);
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    sb.append(inputLine);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return new JSONObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        callback.postJsonDownload(s);
    }
}
