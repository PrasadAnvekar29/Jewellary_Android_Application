package com.example.vadi.jewellay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CompanyMasterActivity extends Activity {

    private ImageView logo;
    private EditText name, addrs, mobile, phone, ownername, gstno;
    String encodedImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comany_master);

        name = (EditText) findViewById(R.id.companyname);
        addrs = (EditText) findViewById(R.id.companyadrs);
        mobile = (EditText) findViewById(R.id.mobile);
        phone = (EditText) findViewById(R.id.phone);
        ownername = (EditText) findViewById(R.id.ownername);
        gstno = (EditText) findViewById(R.id.gstno);
        logo = (ImageView) findViewById(R.id.logo);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        if(!haveNetworkConnection()) {

            Toast.makeText(getApplicationContext(), "You don't have internet connection." , Toast.LENGTH_LONG).show();

            return;
        }else {
            new CompanyMaster().execute();
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


    public void takeImageFromCamera(View view) {

        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            logo.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            Bitmap immagex = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            immagex.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] b = baos.toByteArray();
            String encoded = Base64.encodeToString(b, Base64.DEFAULT);

            encodedImage = encoded;


        }
    }

    public void submit(View view) {
        if (name.getText().toString().isEmpty() || addrs.getText().toString().isEmpty()
                || mobile.getText().toString().isEmpty()
                || ownername.getText().toString().isEmpty() ||
                gstno.getText().toString().isEmpty() ) {

            Toast.makeText(getApplicationContext(), "Please Enter all the feilds", Toast.LENGTH_LONG).show();

        } else {


            AlertDialog.Builder alertDialog = new AlertDialog.Builder(CompanyMasterActivity.this);
            //   alertDialog.setTitle("Confirm ...?");
            alertDialog.setMessage("Confirm ...?");

            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    InputStream is = null;
                    String line = "";
                    String result = "";
                    HttpResponse response = null;
                    int status = 0;
                    try {

                        HttpParams httpParams = new BasicHttpParams();
                        HttpClient client = new DefaultHttpClient(httpParams);
                        //       String url = Config.ORDERS;
                        HttpPost request = new HttpPost(Config.CompanyDetails);

                        JSONObject json = new JSONObject();

                        json.put("company_name", name.getText().toString());
                        json.put("company_addrs", addrs.getText().toString());
                        json.put("company_mobile", mobile.getText().toString());
                        json.put("company_phone", phone.getText().toString());
                        json.put("company_owner_name", ownername.getText().toString());
                        json.put("company_gst_no", gstno.getText().toString());
                        json.put("company_logo", encodedImage);


                        request.setEntity(new StringEntity(json.toString()));

                        //     request.setHeader("json", json.toString());
                        request.setHeader("Content-type", "application/json");
                        response = client.execute(request);
                        HttpEntity entity = response.getEntity();
                        status = response.getStatusLine().getStatusCode();
                        is = entity.getContent();


                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                    }

                    if (status == 200) {
                        Toast.makeText(getApplicationContext(), "Successfully submited ", Toast.LENGTH_LONG).show();
                            name.setText("");
                            addrs.setText("");
                            mobile.setText("");
                            phone.setText("");
                            ownername.setText("");
                            gstno.setText("");
                            encodedImage="";

                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        //    pic.setImageResource(getResources(),R.drawable.empty1);

                    } else {
                        Toast.makeText(getApplicationContext(), "Plz Check Connection", Toast.LENGTH_LONG).show();
                    }

                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }

    }




    private class CompanyMaster extends AsyncTask<String, Void, String> {
        int status=0;
        String NAME="";
        String ADDRS="";
        String MOBILE="";
        String PHONE="";
        String OWNERNAME="";
        String GSTNO="";
        String LOGO="";




        protected void onPreExecute() {
            //      progressdialog = ProgressDialog.show(getApplicationContext(), null, "loading, please wait...");
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {
            InputStream is = null;
            String line ="";
            String result = "";

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpPost = new HttpGet(Config.CompanyDetails);
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
            /*    JSONObject jsonObject1 = new  JSONObject(result);
                GOLD = jsonObject1.getString("stock_gold");
                SILVER = jsonObject1.getString("stock_silver");*/

       //     JSONObject jsonObject = null;
            try {
                JSONObject  jsonObject = new JSONObject(result);


            //      if(JA!=null) {

        //        for (int i = 0; i < JA.length(); i++) {
        //            JSONObject jsonObject = JA.getJSONObject(0);
                    NAME=jsonObject.getString("company_name");
                    ADDRS=jsonObject.getString("company_addrs");
                    MOBILE=jsonObject.getString("company_mobile");
                    PHONE=jsonObject.getString("company_phone");
                    OWNERNAME=jsonObject.getString("company_owner_name");
                    GSTNO=jsonObject.getString("company_gst_no");
                    LOGO=jsonObject.getString("company_logo");

            //    }


            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            byte[] imageAsBytes = Base64.decode(LOGO.getBytes(), Base64.DEFAULT);
            Bitmap bm= BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            logo.setImageBitmap(bm);
            name.setText(NAME);
            addrs.setText(ADDRS);
            mobile.setText(MOBILE);
            phone.setText(PHONE);
            ownername.setText(OWNERNAME);
            gstno.setText(GSTNO);


           /* logo.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            Bitmap immagex = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            immagex.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] b = baos.toByteArray();
            String encoded = Base64.encodeToString(b, Base64.DEFAULT);*/

            encodedImage = LOGO;


        }
    }
}





