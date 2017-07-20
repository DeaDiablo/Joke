package com.liquidsky.joke;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestURL extends AsyncTask<String, Void, String> {

    private RequestCompleted requestCallback = null;

    public RequestURL(RequestCompleted callback) {
        requestCallback = callback;
    }

    @Override
    protected String doInBackground(String... pages) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String result = null;

        try {
            URL url = new URL(pages[0]);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            if (inputStreamReader == null) {
                // Nothing to do.
                return result;
            }

            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            result = buffer.toString();
        }
        catch (MalformedURLException e) {
            Log.e("URL error", e.getMessage(), e);
            return result;
        }
        catch (IOException e) {
            Log.e("Request error", e.getMessage(), e);
            return result;
        }
        catch (NetworkOnMainThreadException e) {
            Log.e("Request error", e.getMessage(), e);
            return result;
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        requestCallback.onRequestCompleted(result);
    }
}
