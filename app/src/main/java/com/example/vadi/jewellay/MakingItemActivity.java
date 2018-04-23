package com.example.vadi.jewellay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MakingItemActivity extends Activity {

    private Spinner metal,caret,category;
    private TextView date;
    private EditText name,adrs,gr_wt,stone_wt,net_wt,mobile;
 //   private LinearLayout LI;
    private ImageView pic;
    String DATE, ct,mt;
    private Uri fileUri;
    String encodedImage="";


   /* String mCurrentPhotoPath;
    Bitmap rotatedBMP;
    static final int REQUEST_TAKE_PHOTO = 1;
    File photoFile = null;



    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;


    private String upLoadServerUri = null;
    private String imagepath=null;
    private Uri mImageCaptureUri;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_making_item);


     //   LI=(LinearLayout)findViewById(R.id.li);
        metal=(Spinner)findViewById(R.id.metal);
        caret=(Spinner)findViewById(R.id.caret);
        category=(Spinner)findViewById(R.id.category);
        date=(TextView)findViewById(R.id.date);

        name=(EditText)findViewById(R.id.name);
        adrs=(EditText)findViewById(R.id.adrs);
        gr_wt=(EditText)findViewById(R.id.grwt);
        stone_wt=(EditText)findViewById(R.id.stonewt);
        net_wt=(EditText)findViewById(R.id.netwt);
    //    purity=(EditText)findViewById(R.id.purity);
        mobile=(EditText)findViewById(R.id.mobile);
        pic=(ImageView)findViewById(R.id.pic);


   //     LI.setVisibility(View.GONE);

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DecimalFormat mFormat= new DecimalFormat("00");
        month=month+1;
        date.setText(mFormat.format(Double.valueOf(day))+"-"+ mFormat.format(Double.valueOf(month))+"-"+year);
        DATE=(year+"-"+ mFormat.format(Double.valueOf(month))+"-"+mFormat.format(Double.valueOf(day)));
        addItemsOnSpinner1();

        if(!haveNetworkConnection()) {

            Toast.makeText(getApplicationContext(), "You don't have internet connection." , Toast.LENGTH_LONG).show();

            return;
        }else {
            new Category().execute();
        }


        metal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String items = metal.getSelectedItem().toString();
                Log.i("Selected item : ", items);

                if(items.equals("Gold")){
                    //        addItemsOnGold();
                    caret.setVisibility(View.VISIBLE);
                }else if(items.equals("Silver")) {
                    caret.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


       /* caret.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {@Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
        {   String items = caret.getSelectedItem().toString();
            Log.i("Selected item : ", items);

            if(items.equals("others")){

                LI.setVisibility(View.VISIBLE);
            }else {
                LI.setVisibility(View.GONE);
            }
        }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {}});*/


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



    public void addItemsOnSpinner1() {
        ArrayAdapter<String> adapter1;
        ArrayList<String> listitem1 = new ArrayList<>();
        listitem1.add("Gold");
        listitem1.add("Silver");
        adapter1 = new ArrayAdapter<String>(this, R.layout.spinner, R.id.text, listitem1);
        metal.setAdapter(adapter1);

    }

    public void addItemsOnGold() {
        ArrayAdapter<String> adapter2;
        ArrayList<String> listitem2 = new ArrayList<>();
        listitem2.add("916 KDM");
        listitem2.add("others");
        adapter2 = new ArrayAdapter<String>(this, R.layout.spinner, R.id.text, listitem2);
        caret.setAdapter(adapter2);


    }

    public void addItemsOnSilver() {
        ArrayAdapter<String> adapter2;
        ArrayList<String> listitem2 = new ArrayList<>();
        listitem2.add("");
        adapter2 = new ArrayAdapter<String>(this, R.layout.spinner, R.id.text, listitem2);
        caret.setAdapter(adapter2);

    }

    public void takeImageFromCamera(View view) {

        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // Open default camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, 100);

        } else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100  && resultCode == RESULT_OK) {
            //    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mcdowells);

            Bitmap photo = (Bitmap) data.getExtras().get("data");
          //  pic.setImageBitmap(photo);

         //   Bitmap bit=photo;
            encodeTobase64(photo);

         //   Bitmap bitmap = BitmapFactory.decodeResource(getResources(), pic.);
        //    BitmapDrawable drawable = (BitmapDrawable) pic.getDrawable();
        //    Bitmap bitmap = drawable.getBitmap();

         /*   ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG,70, baos);
            byte [] b=baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);*/
        }
    }




    public String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
       String encoded = Base64.encodeToString(b,Base64.DEFAULT);



        encodedImage =encoded;
    //    return encoded;
        byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
        Bitmap bm= BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        pic.setImageBitmap(bm);
        return  null;
    }


    private class Category extends AsyncTask<String, Void, String> {
        int status=0;
        String GOLD="";
        String SILVER="";
        ArrayList<String> CAT;
        ArrayList<String> PURITY;

        protected void onPreExecute() {
            //      progressdialog = ProgressDialog.show(getApplicationContext(), null, "loading, please wait...");
            super.onPreExecute();
            CAT = new ArrayList<>();
            PURITY = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... urls) {
            InputStream is = null;
            String line ="";
            String result = "";

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpPost = new HttpGet(Config.CategoryMaster);
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
            /*    JSONObject jsonObject1 = new  JSONObject(result);
                GOLD = jsonObject1.getString("stock_gold");
                SILVER = jsonObject1.getString("stock_silver");*/
                JSONObject jsonObject = new JSONObject(result);

                JSONArray JA1 =jsonObject.getJSONArray("category");
                for (int i = 0; i < JA1.length(); i++) {
                    JSONObject jsonObject1 = JA1.getJSONObject(i);
                    CAT.add(jsonObject1.getString("category"));
                }
                JSONArray JA2 =jsonObject.getJSONArray("purity");
                for (int i = 0; i < JA2.length(); i++) {
                    JSONObject jsonObject2 = JA2.getJSONObject(i);
                    PURITY.add(jsonObject2.getString("purity"));
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            ArrayAdapter<String> adapter2;
            ArrayList<String> listitem2 = new ArrayList<>();
            adapter2=new ArrayAdapter<String>(MakingItemActivity.this,R.layout.spinner,R.id.text,listitem2);
            category.setAdapter(adapter2);
            listitem2.addAll(CAT);
            adapter2.notifyDataSetChanged();


            ArrayAdapter<String> adapter3;
            ArrayList<String> listitem3 = new ArrayList<>();
            adapter3=new ArrayAdapter<String>(MakingItemActivity.this,R.layout.spinner,R.id.text,listitem3);
            caret.setAdapter(adapter3);
            listitem3.addAll(PURITY);
            adapter3.notifyDataSetChanged();

        }
    }



    public void submit(View view) {


        mt= metal.getSelectedItem().toString();
        ct=caret.getSelectedItem().toString();
        if(mt.equals("Silver")){
            ct="";
        }

        if(name.getText().toString().isEmpty() || adrs.getText().toString().isEmpty()
                || mobile.getText().toString().isEmpty()
                || gr_wt.getText().toString().isEmpty() ||
                net_wt.getText().toString().isEmpty()  || encodedImage.isEmpty()){

            Toast.makeText(getApplicationContext(), "Please Enter all the feilds",Toast.LENGTH_LONG).show();

        }else {


            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MakingItemActivity.this);
            //   alertDialog.setTitle("Confirm ...?");
            alertDialog.setMessage("Confirm ...?");

            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {

                    InputStream is = null;
                    String line ="";
                    String result = "";
                    HttpResponse response = null;
                    int status=0;
                    try {

                        HttpParams httpParams = new BasicHttpParams();
                        HttpClient client = new DefaultHttpClient(httpParams);
                        //       String url = Config.ORDERS;
                        HttpPost request = new HttpPost(Config.MakingItem);

                        JSONObject json = new JSONObject();

                        json.put("making_item_date",DATE);
                        json.put("making_item_name",name.getText().toString());
                        json.put("making_item_adrs",adrs.getText().toString());
                        json.put("making_item_mobile",mobile.getText().toString());
                        json.put("making_item_metal_type",metal.getSelectedItem().toString());
                        json.put("making_item_caret",ct);
                        json.put("making_item_image",encodedImage);
                        json.put("making_item_category",category.getSelectedItem().toString());
                        json.put("making_item_gross_wt",gr_wt.getText().toString());
                        json.put("making_item_stone_wt",stone_wt.getText().toString());
                        json.put("making_item_net_wt",net_wt.getText().toString());


                        request.setEntity(new StringEntity(json.toString()));

                        //     request.setHeader("json", json.toString());
                        request.setHeader( "Content-type", "application/json");
                        response = client.execute(request);
                        HttpEntity entity = response.getEntity();
                        status=response.getStatusLine().getStatusCode();
                        is = entity.getContent();



                    } catch(Exception e)
                    {
                        Log.e("log_tag", "Error converting result "+e.toString());
                    }

                    if (status==200){
                        Toast.makeText(getApplicationContext(), "Successfully submited ",Toast.LENGTH_LONG).show();
                        gr_wt.setText("");
                        stone_wt.setText("");
                        net_wt.setText("");
                        name.setText("");
                        adrs.setText("");
                        mobile.setText("");
                        encodedImage="";

                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    //    pic.setImageResource(getResources(),R.drawable.empty1);

                    }else {
                        Toast.makeText(getApplicationContext(), "Plz Check Connection"  ,Toast.LENGTH_LONG).show();
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
}
