package com.example.cryptowatcher;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class MainActivity extends FragmentActivity {

    public static LongOperation runningTask;

    private final String API_URL = "https://api.coingecko.com/api/v3/coins/";
    private String[] mtickerList = new ArrayList<String>().toArray(new String[0]);

    private ViewPager2 viewPager;

    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mtickerList = getResources().getStringArray(R.array.api_ticker);
        runningTask = new LongOperation();
        runningTask.execute();

        ViewPager2 viewPager = findViewById(R.id.pager);

        ScreenAdapter adapter = new ScreenAdapter(this);
        viewPager.setAdapter(adapter);
    }

    public class LongOperation extends AsyncTask<Void, Void, ArrayList<JSONObject>> {
        @Override
        protected ArrayList<JSONObject> doInBackground(Void... voids) {
            ArrayList<JSONObject> result = new ArrayList<JSONObject>();
            JSONObject json_var = null;
            URL url = null;
            for (String coin : mtickerList){
                try {
                    url = new URL(API_URL + coin);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    json_var = new JSONObject(IOUtils.toString(url, StandardCharsets.UTF_8));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                result.add(json_var);
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> results) {
            for (JSONObject result : results){
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
                        price.setCompoundDrawablesWithIntrinsicBounds(R.drawable.increase, 0, 0,0);
                    else
                        price.setCompoundDrawablesWithIntrinsicBounds(R.drawable.decrease, 0, 0,0);
                    high.setText("$" + formatter.format(result.getJSONObject("market_data").getJSONObject("high_24h").getDouble("usd")));
                    low.setText("$" + formatter.format(result.getJSONObject("market_data").getJSONObject("low_24h").getDouble("usd")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            PriceFragment.swipeAction.setRefreshing(false);
            selfRestart();
        }

        public void selfRestart() {
            runningTask = new LongOperation();
        }
    }
}