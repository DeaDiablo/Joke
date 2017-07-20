package com.liquidsky.joke;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements RequestCompleted, View.OnTouchListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        requestNewJoke();
    }

    private RequestURL request = null;

    protected void requestNewJoke() {
        request = new RequestURL(this);
        request.execute("http://api.yomomma.info/");
    }

    @Override
    protected void onStop () {
        super.onStop();
        request.cancel(false);
    }

    private Random rnd = new Random();

    private int generateColor(int index) {
        int r = rnd.nextInt(128);
        int g = rnd.nextInt(128);
        int b = rnd.nextInt(128);
        index = index % 8;
        switch (index)
        {
            case 1:
                r += 128;
                break;
            case 2:
                r += 128;
                g += 128;
                break;
            case 3:
                g += 128;
                b += 128;
                break;
            case 5:
                b += 128;
                break;
            case 6:
                g += 128;
                break;
            case 7:
                r += 128;
                b += 128;
                break;
            default:
                break;
        }
        return Color.rgb(r, g, b);
    }

    private Button createRequestButton(String text, int width, int height, int index) {
        Button button = new Button(this);
        button.setText(text);

        button.setWidth(width);
        button.setHeight(height);

        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(generateColor(index));

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setMargins(3, 3, 3, 3);
        button.setLayoutParams(params);
        button.setOnTouchListener(this);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestNewJoke();
            }
        });
        return button;
    }

    @Override
    public void onRequestCompleted(String result) {
        GridLayout layout = (GridLayout) findViewById(R.id.gridLayout);
        layout.removeAllViews();

        if (result == null) {
            layout.addView(createRequestButton("Error. Repeat request.", layout.getWidth(), (int)(layout.getHeight() * 0.1), 0), 0);
            return;
        }

        Log.i("Json: ", result);
        String jokeText;

        try {
            JSONObject jsonReader = new JSONObject(result);
            jokeText = jsonReader.getString("joke");
        }
        catch (JSONException e) {
            Log.e("JSON format error", e.getMessage(), e);
            return;
        }

        if (jokeText == "null") {
            requestNewJoke();
            return;
        }

        String[] jokeWords = jokeText.split(" ");
        for (int i = 0; i < jokeWords.length; ++i) {
            Button button = createRequestButton(jokeWords[i], (int)(layout.getWidth() * 0.33), (int)(layout.getHeight() * 0.1), i);
            layout.addView(button, i);
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent me) {
        switch (me.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                Log.d("down", "down");
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d("move", "move");
                break;

            case MotionEvent.ACTION_UP:
                Log.d("up", "up");
                break;

        }
        return false;
    }
}
