package com.example.vadi.jewellay;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class SaleActivity extends Activity  implements OnClickListener  {

    private EditText fromDateEtxt;
    private EditText toDateEtxt;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;
    ProgressDialog progressdialog;
    String frmdate="0";
    private TextView gold,silver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        fromDateEtxt = (EditText) findViewById(R.id.etxt_fromdate);

      //  fromDateEtxt.setInputType(InputType.TYPE_NULL);
        fromDateEtxt.requestFocus();

        toDateEtxt = (EditText) findViewById(R.id.etxt_todate);
        toDateEtxt.setInputType(InputType.TYPE_NULL);


        gold = (TextView) findViewById(R.id.gold);
        silver = (TextView) findViewById(R.id.silver);


        setDateTimeField();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DecimalFormat mFormat= new DecimalFormat("00");
        month=month+1;
        toDateEtxt.setText(year+"-"+ mFormat.format(Double.valueOf(month))+"-"+mFormat.format(Double.valueOf(day)));


        if(!haveNetworkConnection()) {

            Toast.makeText(getApplicationContext(), "You don't have internet connection." , Toast.LENGTH_LONG).show();

            /*new AlertDialog.Builder(getApplicationContext())
                    .setTitle("No Internet Connection")
                    .setMessage("You don't have internet connection.")
                    .setCancelable(false)
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish(); //Close current activity
                            startActivity(getIntent()); //Restart it
                        }
                    }).create().show();*/
            return;
        }else {
            new Sale().execute(frmdate, toDateEtxt.getText().toString());
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


    private void setDateTimeField() {
        fromDateEtxt.setOnClickListener(this);
        toDateEtxt.setOnClickListener(this);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));

                if(fromDateEtxt.getText().toString().isEmpty()) {
                    new Sale().execute(frmdate, toDateEtxt.getText().toString());
                }else {
                    new Sale().execute(fromDateEtxt.getText().toString(), toDateEtxt.getText().toString());
                }
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));

                if(fromDateEtxt.getText().toString().isEmpty()) {
                    new Sale().execute(frmdate, toDateEtxt.getText().toString());
                }else {
                    new Sale().execute(fromDateEtxt.getText().toString(), toDateEtxt.getText().toString());
                }
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if(view == fromDateEtxt) {
            fromDatePickerDialog.show();
        } else if(view == toDateEtxt) {
            toDatePickerDialog.show();
        }
    }



    private class Sale extends AsyncTask<String, Void, String> {
        int status=0;
        String GOLD="";
        String SILVER="";

        protected void onPreExecute() {
      //      progressdialog = ProgressDialog.show(SaleActivity.this, null, "loading, please wait...");
            super.onPreExecute();
        }


        public String getData(String fromdate,String todate ) {
            InputStream is = null;
            String line ="";
            String result = "";

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpPost = new HttpGet(Config.Sales+fromdate+"/"+todate);
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
                JSONArray JA = new JSONArray(result);
                for (int i = 0; i < JA.length(); i++) {
                    JSONObject jsonObject1 = JA.getJSONObject(0);
                    GOLD = jsonObject1.getString("total_wt");
                    //      table.add(jsonObject.getString("TableNum"));
                }

                for (int i = 1; i < JA.length(); i++) {
                    JSONObject jsonObject2 = JA.getJSONObject(1);
                    SILVER = jsonObject2.getString("total_wt");
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected String doInBackground(String... urls) {
            return getData(urls[0], urls[1]);
        }

        @Override
        protected void onPostExecute(String result) {


      //      progressdialog.dismiss();

           if(GOLD.isEmpty() && SILVER.isEmpty()){
               Toast.makeText(getApplicationContext(), "PLZ  Purchase" , Toast.LENGTH_LONG).show();

           }


            if (status == 200) {

                gold.setText(GOLD);
                silver.setText(SILVER);

            }else{
                Toast.makeText(getApplicationContext(), "Please check the connection" , Toast.LENGTH_LONG).show();
            }

      //      progressdialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
