package com.example.cryptowatcher;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends WearableActivity implements View.OnClickListener {

    TextView txt;
    Timer timer;
    AsyncTask<String, String, JSONObject> runningTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = findViewById(R.id.textViewCel);

        // Because we implement OnClickListener, we only
        // have to pass "this" (much easier)
        txt.setOnClickListener(this);
        startTimer();
    }

    @Override
    public void onClick(View view) {
        // Detect the view that was "clicked"
        switch (view.getId()) {
            case R.id.textViewCel:
                if (runningTask != null)
                    runningTask.cancel(true);
                runningTask = new LongOperation();
                runningTask.execute("https://api.coingecko.com/api/v3/coins/celsius-degree-token");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel running task(s) to avoid memory leaks
        if (runningTask != null)
            runningTask.cancel(true);
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new LongOperation().execute("https://api.coingecko.com/api/v3/coins/celsius-degree-token");
                new LongOperation().execute("https://api.coingecko.com/api/v3/coins/bitcoin");
                new LongOperation().execute("https://api.coingecko.com/api/v3/coins/ethereum");
            }
        }, 0, 10);
    }

    private final class LongOperation extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            URL url = null;
            try {
                url = new URL(urls[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONObject json_var = null;
            try {
                json_var = new JSONObject(IOUtils.toString(url, StandardCharsets.UTF_8));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json_var;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            String ticker = null;
            try {
                ticker = result.getString("symbol");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            String logo = "logo_" + ticker;
            TextView price = (TextView) findViewById(getResources().getIdentifier("textViewPrice_" + ticker , "id", getPackageName()));
            TextView high = (TextView) findViewById(getResources().getIdentifier("textViewHighPrice_" + ticker , "id", getPackageName()));
            TextView low = (TextView) findViewById(getResources().getIdentifier("textViewLowPrice_" + ticker , "id", getPackageName()));
            try {
                price.setText("$" + formatter.format(result.getJSONObject("market_data").getJSONObject("current_price").getDouble("usd")));
                if (result.getJSONObject("market_data").getInt("price_change_24h")>0)
                    price.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.increase,0);
                else
                    price.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.decrease,0);
                high.setText("$" + formatter.format(result.getJSONObject("market_data").getJSONObject("high_24h").getDouble("usd")));
                low.setText("$" + formatter.format(result.getJSONObject("market_data").getJSONObject("low_24h").getDouble("usd")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}