package com.example.vadi.jewellay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StockActivity extends Activity {

    private TextView gold,silver;

    ProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);


        gold = (TextView) findViewById(R.id.gold);
        silver = (TextView) findViewById(R.id.silver);


        if(!haveNetworkConnection()) {

            Toast.makeText(getApplicationContext(), "You don't have internet connection." , Toast.LENGTH_LONG).show();


            return;
        }else {
            new Stock().execute();
        }


    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    private class Stock extends AsyncTask<String, Void, String> {
        int status=0;
        String GOLD="";
        String SILVER="";

        protected void onPreExecute() {
            progressdialog = ProgressDialog.show(StockActivity.this, null, "loading, please wait...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            InputStream is = null;
            String line ="";
            String result = "";

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpPost = new HttpGet(Config.Stocks);
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                status=response.getStatusLine().getStatusCode();
            } catch (Exception e) {
                Log.e("Webservice Exception : ", e.toString());
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();
                result = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
            //    JSONArray JA = new JSONArray(result);
             //   for (int i = 0; i < JA.length(); i++)
             //       JSONObject jsonObject = JA.getJSONObject(0);

                JSONObject jsonObject1 = new  JSONObject(result);
                    GOLD = jsonObject1.getString("stock_gold");
                    SILVER = jsonObject1.getString("stock_silver");
           //     }





            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

progressdialog.dismiss();
            if(GOLD.isEmpty() || SILVER.isEmpty()){
                Toast.makeText(getApplicationContext(), "PLZ  Purchase" , Toast.LENGTH_LONG).show();

            }


            if (status == 200) {

                gold.setText(GOLD);
                silver.setText(SILVER);

            }else{
                Toast.makeText(getApplicationContext(), "Please check the connection" , Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
