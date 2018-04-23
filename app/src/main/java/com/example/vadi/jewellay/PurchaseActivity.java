package com.example.vadi.jewellay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.os.StrictMode;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class PurchaseActivity extends Activity {

    private TextView date;
    private Spinner metal, caret,category;
    private EditText gstno,companyname,mobile,price,grwt,stonewt,netwt,totalprice,igst,cgst,sgst; // weightingrams

    String DATE;
 //   private LinearLayout LI;
    String ct,mt;
    private ImageView pic;
    private Uri fileUri;
    File file;
    String encodedImage="";
    String mCurrentPhotoPath;
    Bitmap rotatedBMP;
//    private CheckBox checkbox_cgst,checkbox_sgst,checkbox_igst;
    double CGST = 0.0,SGST = 0.0,IGST = 0.0;
    private static final String TAG = "upload";


    String METAL,CATEGORY,COMPANY_NAME,GST_NO,MOBILE,PERCHASE_GRWT,PERCHASE_STONEWT,PERCHASE_NETWT,
            PERCHASE_PRICE,PERCHASE_CGST,PERCHASE_SGST,PERCHASE_IGST,PERCHASE_TOTALPRICE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

     //   LI=(LinearLayout)findViewById(R.id.li);
        metal=(Spinner)findViewById(R.id.metal);
        caret=(Spinner)findViewById(R.id.caret);
        category=(Spinner)findViewById(R.id.category);
        date=(TextView)findViewById(R.id.date);
       /* weightingrams=(EditText)findViewById(R.id.weightingrams);*/
        grwt=(EditText)findViewById(R.id.grwt);
        stonewt=(EditText)findViewById(R.id.stonewt);
        netwt=(EditText)findViewById(R.id.netwt);

        gstno=(EditText)findViewById(R.id.gstno);
     //   purity=(EditText)findViewById(R.id.purity);
        companyname=(EditText)findViewById(R.id.companyname);
        mobile=(EditText)findViewById(R.id.mobile);
        price=(EditText)findViewById(R.id.purchse_price);
        cgst=(EditText)findViewById(R.id.cgst);
        sgst=(EditText)findViewById(R.id.sgst);
        igst=(EditText)findViewById(R.id.igst);
        totalprice=(EditText)findViewById(R.id.totalprice);
        pic=(ImageView)findViewById(R.id.pic);





        KeyboardVisibilityEvent.setEventListener(
                PurchaseActivity.this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                    }
                });

    //    checkbox_cgst=(CheckBox)findViewById(R.id.checkbox_cgst);
     //   checkbox_sgst=(CheckBox)findViewById(R.id.checkbox_sgst);
     //   checkbox_igst=(CheckBox)findViewById(R.id.checkbox_igst);



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

       /* caret.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String items = caret.getSelectedItem().toString();
                Log.i("Selected item : ", items);

                if(items.equals("others")){

                    LI.setVisibility(View.VISIBLE);
                }else {
                    LI.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });*/



        /*price.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!s.toString().isEmpty() || s.toString()=="0"){

                    double prc= Double.parseDouble(s.toString());


                    if(checkbox_cgst.isChecked() || checkbox_sgst.isChecked() || checkbox_igst.isChecked() ){
                        checkbox_cgst.setChecked(false);
                        checkbox_sgst.setChecked(false);
                        checkbox_igst.setChecked(false);
                    }
                    totalprice.setText(prc+"");
                }

            }
        });*/



        /*checkbox_cgst.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean         isChecked)
            {

                double totalprc = 0.0;
                if(isChecked)
                {
                    if(price.getText().toString().isEmpty() || price.getText().toString().equals("0")) {

                        Toast.makeText(getApplicationContext(), "Please Enter Price",Toast.LENGTH_LONG).show();
                        checkbox_cgst.setChecked(false);
                    }else {
                        Double prc = Double.parseDouble(price.getText().toString());
                        Double  total = Double.parseDouble(totalprice.getText().toString());
                        CGST = prc * Config.CGST / 100;
                        cgst.setText(CGST + "");



                        totalprc = prc + CGST+SGST+IGST;
                        totalprice.setText(totalprc + "");
                    }
                } else {
                    cgst.setText("");
                    double  total1 = Double.parseDouble(totalprice.getText().toString());
                    double  total2 = total1- CGST;
                    totalprice.setText(total2 + "");
                    CGST=0.0;

                }

            }
        });*/

        /*checkbox_sgst.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean         isChecked)
            {
                // TODO Auto-generated method stub

                    double  totalprc = 0.0;
                    if (isChecked) {

                        if(price.getText().toString().isEmpty() || price.getText().toString().equals("0")) {
                            Toast.makeText(getApplicationContext(), "Please Enter Price",Toast.LENGTH_LONG).show();
                            checkbox_sgst.setChecked(false);
                        }else {
                            Double prc = Double.parseDouble(price.getText().toString());
                            Double  total = Double.parseDouble(totalprice.getText().toString());
                            SGST = prc * Config.SGST / 100;
                            sgst.setText(SGST + "");


                            totalprc = prc + CGST+SGST+IGST;
                            totalprice.setText(totalprc + "");
                        }
                    } else {
                        sgst.setText("");
                        double  total1 = Double.parseDouble(totalprice.getText().toString());

                        double  total2 = total1- SGST;
                        totalprice.setText(total2 + "");
                        SGST=0.0;
                    }

            }

        });*/

        /*checkbox_igst.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean         isChecked)
            {

                    double  totalprc = 0.0;
                    if (isChecked) {
                        if(price.getText().toString().isEmpty() || price.getText().toString().equals("0")) {

                            Toast.makeText(getApplicationContext(), "Please Enter Price",Toast.LENGTH_LONG).show();
                            checkbox_igst.setChecked(false);
                        }else {

                            Double prc = Double.parseDouble(price.getText().toString());
                            Double  total = Double.parseDouble(totalprice.getText().toString());
                            IGST = prc * Config.IGST / 100;
                            igst.setText(IGST + "");


                            totalprc = prc + CGST+SGST+IGST;
                            totalprice.setText(totalprc + "");
                        }
                    } else {
                        igst.setText("");
                        double  total1 = Double.parseDouble(totalprice.getText().toString());

                        double  total2 = total1- IGST;
                        totalprice.setText(total2 + "");
                        IGST=0.0;
                    }

            }
        });*/


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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_purchase, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refresh:
                Intent intent = getIntent();
                finish();
                startActivity(intent);

                break;
            default:
                break;
        }
        return true;
    }

    public void addItemsOnSpinner1() {
        ArrayAdapter<String> adapter;
        ArrayList<String> listitem = new ArrayList<>();
        listitem.add("Gold");
        listitem.add("Silver");
        adapter = new ArrayAdapter<String>(this, R.layout.spinner, R.id.text, listitem);
        metal.setAdapter(adapter);

    }

    public void addItemsOnGold() {
        ArrayAdapter<String> adapter1;
        ArrayList<String> listitem1 = new ArrayList<>();
        listitem1.add("916 kdm");
        listitem1.add("others");
        adapter1 = new ArrayAdapter<String>(this, R.layout.spinner, R.id.text, listitem1);
        caret.setAdapter(adapter1);

    }

    public void addItemsOnSilver() {
        ArrayAdapter<String> adapter2;
        ArrayList<String> listitem2 = new ArrayList<>();
       listitem2.add("");
        adapter2 = new ArrayAdapter<String>(this, R.layout.spinner, R.id.text, listitem2);
        caret.setAdapter(adapter2);

    }


    public void submit(View view) {


        mt= metal.getSelectedItem().toString();
        ct=caret.getSelectedItem().toString();
        if(mt.equals("Silver")){
            ct="";
        }


        if(gstno.getText().toString().isEmpty() || mobile.getText().toString().isEmpty() ||
                companyname.getText().toString().isEmpty() || grwt.getText().toString().isEmpty()
                || netwt.getText().toString().isEmpty() || price.getText().toString().isEmpty()
                || encodedImage.isEmpty()) {

            Toast.makeText(getApplicationContext(), "Please Enter all the feilds",Toast.LENGTH_LONG).show();
        }else {


            AlertDialog.Builder alertDialog = new AlertDialog.Builder(PurchaseActivity.this);
        //   alertDialog.setTitle("Confirm ...?");
        alertDialog.setMessage("Confirm ...?");

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

             /*   METAL=metal.getSelectedItem().toString();
                CATEGORY=category.getSelectedItem().toString();
                COMPANY_NAME=companyname.getText().toString();
                GST_NO=gstno.getText().toString();
                MOBILE=mobile.getText().toString();
                PERCHASE_GRWT=grwt.getText().toString();
                PERCHASE_STONEWT=stonewt.getText().toString();
                PERCHASE_NETWT=netwt.getText().toString();
                PERCHASE_PRICE=price.getText().toString();
                PERCHASE_CGST=cgst.getText().toString();
                PERCHASE_SGST=sgst.getText().toString();
                PERCHASE_IGST=igst.getText().toString();
                PERCHASE_TOTALPRICE=totalprice.getText().toString();*/


          //      new UploadTask().execute(rotatedBMP);

                HttpResponse response = null;
                int status=0;

                try {

                    HttpParams httpParams = new BasicHttpParams();
                    HttpClient client = new DefaultHttpClient(httpParams);
                    HttpPost request = new HttpPost(Config.Purchase);

                    JSONObject json = new JSONObject();


                    json.put("purchase_date",DATE);
                    json.put("metal_type",metal.getSelectedItem().toString());
                    json.put("caret",ct);
              //      json.put("caret",caret.getSelectedItem().toString());
                    json.put("category",category.getSelectedItem().toString());
                    json.put("company_name",companyname.getText().toString());
                    json.put("gst_no",gstno.getText().toString());
                    json.put("mobile",mobile.getText().toString());


                    json.put("purchase_grwt",grwt.getText().toString());
                    json.put("purchase_stonewt",stonewt.getText().toString());
                    json.put("purchase_netwt",netwt.getText().toString());

                    json.put("purchase_price",price.getText().toString());
                    json.put("purchase_cgst",cgst.getText().toString());
                    json.put("purchase_sgst",sgst.getText().toString());
                    json.put("purchase_igst",igst.getText().toString());
                    json.put("purchase_totalprice",totalprice.getText().toString());
                    json.put("purchase_bill_pic",encodedImage);



            //        json.put("weight_in_grams",weightingrams.getText().toString());

                    request.setEntity(new StringEntity(json.toString()));
                    request.setHeader( "Content-type", "application/json");

                    response = client.execute(request);
                    HttpEntity entity = response.getEntity();
                    status=response.getStatusLine().getStatusCode();
                    //      is = entity.getContent();
                } catch(Exception e)
                {
                    e.printStackTrace();
                    Log.e("log_tag", "Error converting result "+e.toString());
                }
                if (status==200){
                    Toast.makeText(getApplicationContext(), "Successfully submited ",Toast.LENGTH_LONG).show();

              //      weightingrams.setText("");
                    gstno.setText("");
                    mobile.setText("");
                    companyname.setText("");
                    encodedImage="";

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), " plz check connection ",Toast.LENGTH_LONG).show();
                 //   purity.setText("");
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

  /*  private class UploadTask extends AsyncTask<Bitmap, Void, Void> {

        int respse=0;
        protected Void doInBackground(Bitmap... bitmaps) {
            if (bitmaps[0] == null)
                return null;
            setProgress(0);

            Bitmap bitmap = bitmaps[0];
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
            InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream

            DefaultHttpClient httpclient = new DefaultHttpClient();

       *//*     HttpParams httpParams = new BasicHttpParams();
            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost request = new HttpPost(Config.Purchase);*//*

            JSONObject json = new JSONObject();
            try {

                json.put("purchase_date",DATE);
                json.put("metal_type",METAL);
                json.put("caret",ct);
                json.put("category",CATEGORY);
                json.put("company_name",COMPANY_NAME);
                json.put("gst_no",GST_NO);
                json.put("mobile",MOBILE);
                json.put("purchase_grwt",PERCHASE_GRWT);
                json.put("purchase_stonewt",PERCHASE_STONEWT);
                json.put("purchase_netwt",PERCHASE_NETWT);
                json.put("purchase_price",PERCHASE_PRICE);
                json.put("purchase_cgst",PERCHASE_CGST);
                json.put("purchase_sgst",PERCHASE_SGST);
                json.put("purchase_igst",PERCHASE_IGST);
                json.put("purchase_totalprice",PERCHASE_TOTALPRICE);
                json.put("purchase_bill_pic",encodedImage);

            } catch (JSONException e) {
                e.printStackTrace();
            }



            try {
                HttpPost httppost = new HttpPost(Config.Purchase); // server

                MultipartEntity reqEntity = new MultipartEntity();
                //			reqEntity.addPart("book_name",nmber, in);
                reqEntity.addPart("image",System.currentTimeMillis() + ".jpg", in);
                reqEntity.addPart("data", json.toString(),in, "application/json");


                httppost.setEntity(reqEntity);

            //    Log.i(TAG, "request " + httppost.getRequestLine());
                HttpResponse response = null;
                try {
                    response = httpclient.execute(httppost);

                    respse=response.getStatusLine().getStatusCode();
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    if (response != null)
                        Log.i(TAG, "response " + response.getStatusLine().toString());
                } finally {

                }
            } finally {

            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(respse==200) {
                Toast.makeText(PurchaseActivity.this, "Succcessful Submited", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(PurchaseActivity.this, "PLZ Check connection", Toast.LENGTH_LONG).show();
            }
        }
    }
*/








    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void takeImageFromCamera(View view) {

       /* if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // Open default camera
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, 100);
                }
            }*/

        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
       Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, 100);

        } else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String storageDir = Environment.getExternalStorageDirectory() + "/picupload";
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        file=image;
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
    //    Log.i(TAG, "photo path = " + mCurrentPhotoPath);
        return image;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            encodeTobase64(photo);
       //     setPic();
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = pic.getWidth();
        int targetH = pic.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor << 1;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        Matrix mtx = new Matrix();
        mtx.postRotate(0);
        // Rotating Bitmap
        rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

        if (rotatedBMP != bitmap)
            bitmap.recycle();

        pic.setImageBitmap(rotatedBMP);

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
            adapter2=new ArrayAdapter<String>(PurchaseActivity.this,R.layout.spinner,R.id.text,listitem2);
            category.setAdapter(adapter2);
            listitem2.addAll(CAT);
            adapter2.notifyDataSetChanged();


            ArrayAdapter<String> adapter3;
            ArrayList<String> listitem3 = new ArrayList<>();
            adapter3=new ArrayAdapter<String>(PurchaseActivity.this,R.layout.spinner,R.id.text,listitem3);
            caret.setAdapter(adapter3);
            listitem3.addAll(PURITY);
            adapter3.notifyDataSetChanged();

        }
    }


}
