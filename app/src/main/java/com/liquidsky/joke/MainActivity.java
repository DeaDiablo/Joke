package com.liquidsky.joke;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements RequestCompleted {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestNewJoke();
        setContentView(R.layout.activity_main);
    }

    private RequestURL request = null;

    protected void requestNewJoke() {
        request = new RequestURL(this);
        request.execute("http://api.yomomma.info/");
    }

    @Override
    public void onRequestCompleted(String result) {

        Log.i("Json: ", result);
        String jokeText = "";

        try {
            JSONObject jsonReader = new JSONObject(result);
            jokeText = jsonReader.getString("joke");
        }
        catch (JSONException e) {
            Log.e("JSON format error", e.getMessage(), e);
            return;
        }
    }

    @Override
    protected void onStop () {
        super.onStop();
        request.cancel(false);
    }
}
