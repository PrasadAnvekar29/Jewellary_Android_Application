package com.example.vadi.jewellay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HomeActivity extends AppCompatActivity {

    private EditText silver,gold,igst,cgst,category,sgst;
    private Button save,add;
    private TextView movingtext;
    private ViewFlipper viewFlipper;
    private Button button2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        movingtext=(TextView)findViewById(R.id.movingtext);
        button2=(Button)findViewById(R.id.button2);


        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        viewFlipper.startFlipping();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        if(!haveNetworkConnection()) {

            Toast.makeText(getApplicationContext(), "You don't have internet connection." , Toast.LENGTH_LONG).show();
            return;
        }else {
            new Rate().execute();
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


    public void purchase(View view) {
        Intent intent=new Intent(getApplicationContext(),PurchaseActivity.class);
        startActivity(intent);
    }

    public void bill(View view) {
        Intent intent=new Intent(getApplicationContext(),BillActivity.class);
        startActivity(intent);
    }

    public void sale(View view) {
        Intent intent=new Intent(getApplicationContext(),SaleActivity.class);
        startActivity(intent);
    }

    public void stock(View view) {
        Intent intent=new Intent(getApplicationContext(),StockActivity.class);
        startActivity(intent);
    }

    public void makingitems(View view) {
        Intent intent=new Intent(getApplicationContext(),MakingItemActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ratemaster:
                ratemaster();
                break;
            case R.id.categorymaster:
                categorymaster();
                break;
            case R.id.refresh:
                Intent intent = getIntent();
                finish();
                startActivity(intent);

                break;
            case R.id.companymastermaster:
                Intent i =new  Intent(getApplicationContext(),CompanyMasterActivity.class);
                startActivity(i);

                break;
            default:
                break;
        }
        return true;
    }
    public void ratemaster() {
                LayoutInflater li = LayoutInflater.from(HomeActivity.this);
                View rateDialog = li.inflate(R.layout.rate_master_dialog, null);

        cgst = (EditText) rateDialog.findViewById(R.id.cgst);
        sgst = (EditText) rateDialog.findViewById(R.id.sgst);
        igst = (EditText) rateDialog.findViewById(R.id.igst);
        gold = (EditText) rateDialog.findViewById(R.id.gold);
        silver = (EditText) rateDialog.findViewById(R.id.silver);
        save = (Button) rateDialog.findViewById(R.id.save);

        AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                alert.setView(rateDialog);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCancelable(true);
                alertDialog.show();

        save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        HttpResponse response = null;
                        int status=0;

                        try {

                            HttpParams httpParams = new BasicHttpParams();
                            HttpClient client = new DefaultHttpClient(httpParams);
                       //     HttpPost request = new HttpPost("http://192.168.0.113/jewellery-app/public/api/rates");
                            HttpPost request = new HttpPost(Config.RateMaster);
                            JSONObject json = new JSONObject();


                            json.put("cgst",cgst.getText().toString());
                            json.put("sgst",sgst.getText().toString());
                            json.put("igst",igst.getText().toString());
                            json.put("tgold",gold.getText().toString());
                            json.put("tsilver",silver.getText().toString());

                            request.setEntity(new StringEntity(json.toString()));
                            request.setHeader( "Content-type", "application/json");

                            response = client.execute(request);
                            HttpEntity entity = response.getEntity();
                            status=response.getStatusLine().getStatusCode();



                        } catch(Exception e)
                        {
                            e.printStackTrace();
                            Log.e("log_tag", "Error converting result "+e.toString());
                        }


                        if (status==200){
                            Toast.makeText(getApplicationContext(), "Successfully submited ",Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();
                            Config.CGST=Double.parseDouble(cgst.getText().toString());
                            Config.SGST=Double.parseDouble(sgst.getText().toString());
                            Config.IGST=Double.parseDouble(igst.getText().toString());
                            Config.GOLD=Double.parseDouble(gold.getText().toString());
                            Config.SILVER=Double.parseDouble(silver.getText().toString());
                        }else {
                            Toast.makeText(getApplicationContext(), " plz check connection ",Toast.LENGTH_LONG).show();
                        }
                    }
                });

        cgst.setText(Config.CGST+"");
        sgst.setText(Config.SGST+"");
        igst.setText(Config.IGST+"");
        gold.setText(Config.GOLD+"");
        silver.setText(Config.SILVER+"");

     //   new Rate().execute();





    }

    public void categorymaster() {
        LayoutInflater li = LayoutInflater.from(HomeActivity.this);
        View rateDialog = li.inflate(R.layout.category_master_dialog, null);


        category = (EditText) rateDialog.findViewById(R.id.category);
        add = (Button) rateDialog.findViewById(R.id.add);

        AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
        alert.setView(rateDialog);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpResponse response = null;
                int status=0;
                String data="";

                try {

                    HttpParams httpParams = new BasicHttpParams();
                    HttpClient client = new DefaultHttpClient(httpParams);
              //      HttpPost request = new HttpPost("http://192.168.0.113/jewellery-app/public/api/categories");
                    HttpPost request = new HttpPost(Config.CategoryMaster);
                    JSONObject json = new JSONObject();

                    json.put("category",category.getText().toString());

                    request.setEntity(new StringEntity(json.toString()));
                    request.setHeader( "Content-type", "application/json");
                    response = client.execute(request);
                    HttpEntity entity = response.getEntity();
                    status=response.getStatusLine().getStatusCode();
               //     data=response.gt;

                } catch(Exception e)
                {
                    e.printStackTrace();
                    Log.e("log_tag", "Error converting result "+e.toString());
                    //      Toast.makeText(getApplicationContext(), "  "+e.toString(),Toast.LENGTH_LONG).show();
                }


                if (status==200){
                    Toast.makeText(getApplicationContext(), "Successfully submited ",Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                }else {
                    Toast.makeText(getApplicationContext(), " plz check connection ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



   /* @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }*/



    private class Rate extends AsyncTask<String, Void, String> {
        int status=0;
        Double GOLD=0.0;
        Double SILVER=0.0;
        Double CGST=0.0;
        Double SGST=0.0;
        Double IGST=0.0;


        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            InputStream is = null;
            String line ="";
            String result = "";

            try {
                HttpClient httpClient = new DefaultHttpClient();
         //       HttpGet HttpGet = new HttpGet("http://192.168.0.113/jewellery-app/public/api/rates");
                HttpGet HttpGet = new HttpGet(Config.RateMaster);
                HttpResponse response = httpClient.execute(HttpGet);
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
           //     JSONArray JA = new JSONArray(result);


                JSONObject jsonObject1 = new  JSONObject(result);
                for (int i = 0; i < jsonObject1.length(); i++) {
              //      JSONObject jsonObject = jsonObject1.getJSONObject(i);

                    CGST = jsonObject1.getDouble("cgst");
                    SGST = jsonObject1.getDouble("sgst");
                    IGST = jsonObject1.getDouble("igst");
                    GOLD = jsonObject1.getDouble("tgold");
                    SILVER = jsonObject1.getDouble("tsilver");

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {




            movingtext.setText("Today's price Gold: "+GOLD+"   Silver: "+SILVER);
           /* movingtext.setText("Today's price  Gold:"+GOLD+ "\n"+
                    "                        Silver:"+SILVER);*/
            Animation animationToRight = new TranslateAnimation(-950,1100, 0, 0);
            animationToRight.setDuration(11000);
            animationToRight.setRepeatMode(Animation.RESTART);
            animationToRight.setRepeatCount(Animation.INFINITE);


            movingtext.setAnimation(animationToRight);

        /*    cgst.setText(CGST+"");
            igst.setText(IGST+"");
            gold.setText(GOLD+"");
            silver.setText(SILVER+"");*/

            Config.CGST = CGST;
            Config.SGST = SGST;
            Config.IGST = IGST;
            Config.GOLD = GOLD;
            Config.SILVER = SILVER;

          /*
           */

//movingtext.setText();

            /*movingtext.setText("Today's price  Gold:"+Config.GOLD+ "   Silver:"+Config.SILVER);*/

/*
            Animation animationToRight = new TranslateAnimation(-400,400, 0, 0);
            animationToRight.setDuration(500);
            animationToRight.setRepeatMode(Animation.RESTART);
            animationToRight.setRepeatCount(Animation.INFINITE);


            movingtext.setAnimation(animationToRight);*/
         //   movingtext.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.move));


          /*  if(GOLD.isEmpty() || SILVER.isEmpty()){
                Toast.makeText(getApplicationContext(), "PLZ  Purchase" , Toast.LENGTH_LONG).show();

            }


            if (status == 200) {

                gold.setText(GOLD);
                silver.setText(SILVER);

            }else{
                Toast.makeText(getApplicationContext(), "Please check the connection" , Toast.LENGTH_LONG).show();
            }*/
        }
    }

}
