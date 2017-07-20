package com.liquidsky.joke;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.locks.Lock;

public class MainActivity extends AppCompatActivity implements RequestCompleted, View.OnClickListener {

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
    protected void onDestroy() {
        if (request != null) {
            request.cancel(true);
        }
        super.onDestroy();
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
        params.setMargins(10, 10, 10, 10);
        button.setLayoutParams(params);
        button.setOnClickListener(this);
        return button;
    }

    private final float scaleButton = 1.075f;
    private final long swapTime = 500L;
    private boolean swapMode = false;
    private int selectIndex = 0;
    private int maxIndex = 0;

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
        maxIndex = jokeWords.length - 1;
        selectIndex = selectIndex > maxIndex ? maxIndex : selectIndex;
        for (int i = 0; i < jokeWords.length; ++i) {
            Button button = createRequestButton(jokeWords[i], (int)(layout.getWidth() * 0.3), (int)(layout.getHeight() * 0.1), i);
            if (i == selectIndex) {
                button.setScaleX(scaleButton);
                button.setScaleY(scaleButton);
            }
            layout.addView(button, i);
        }
    }

    private void changeSelectWord(int x, int y) {
        GridLayout layout = (GridLayout) findViewById(R.id.gridLayout);

        int oldIndex = selectIndex;
        selectIndex = selectIndex + y * 3 + x;
        selectIndex = Math.min(Math.max(selectIndex, 0), maxIndex);

        if (selectIndex == oldIndex) {
            return;
        }

        Button prevButton = (Button)layout.getChildAt(oldIndex);
        Button nextButton = (Button)layout.getChildAt(selectIndex);

        if (swapMode) {
            layout.removeView(prevButton);
            layout.addView(prevButton, selectIndex);
            layout.removeView(nextButton);
            layout.addView(nextButton, oldIndex);
        } else {
            prevButton.setScaleX(1.0f);
            prevButton.setScaleY(1.0f);
            nextButton.setScaleX(scaleButton);
            nextButton.setScaleY(scaleButton);
        }
    }

    @Override
    public void onClick(View view) {
        requestNewJoke();
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        int xAxis = (int)ev.getAxisValue(MotionEvent.AXIS_HAT_X);
        int yAxis = (int)ev.getAxisValue(MotionEvent.AXIS_HAT_Y);
        Log.d("axis: ", " " + xAxis + " " + yAxis);
        if (xAxis != 0 || yAxis != 0)
            changeSelectWord(xAxis, yAxis);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent ev) {
        if (KeyEvent.isGamepadButton(ev.getKeyCode())) {
            switch (ev.getKeyCode()) {
                case KeyEvent.KEYCODE_BUTTON_A:
                    if (ev.getAction() == KeyEvent.ACTION_UP) {
                        requestNewJoke();
                    }
                    break;
                case KeyEvent.KEYCODE_BUTTON_Y:
                    if (ev.getAction() == KeyEvent.ACTION_UP) {
                        long downTime = (ev.getEventTime() - ev.getDownTime());
                        swapMode = downTime > swapTime;
                    }
                    break;
            }
        }
        return false;
    }
}
