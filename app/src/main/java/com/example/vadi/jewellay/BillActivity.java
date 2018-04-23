package com.example.vadi.jewellay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.os.StrictMode;

public class BillActivity extends Activity {

    private TextView price,date,totalprice,cgst,sgst,igst,view,include_oldgold_totalprice;
    private Spinner metal,caret,category;
    private EditText cust_name,cust_adrs,gr_wt,stone_wt,net_wt,mobile,discount,pan_no,gst_no,making_charges,stone_charges;
    String DATE;
    private LinearLayout LI1;
    String ct,mt;
    private CheckBox checkbox;
    private EditText old_custname,old_mobile,old_gr_wt,old_less_wt,old_net_wt,old_touch,old_rate,old_totalpricee,old_itemname;
    private Button old_submit,old_cancel;
    String x="",y="";
    Double p;
    Bundle bundle;
    private ImageView pic;
    private Uri fileUri;
    private CheckBox checkbox_cgst,checkbox_sgst,checkbox_igst;
    String encodedImage="";
    double CGST = 0.0,SGST = 0.0,IGST = 0.0;

    private Tabels Tabels;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    //    LI=(LinearLayout)findViewById(R.id.li);
        LI1=(LinearLayout)findViewById(R.id.li1);
        metal=(Spinner)findViewById(R.id.metal);
        caret=(Spinner)findViewById(R.id.caret);
        category=(Spinner)findViewById(R.id.category);
        date=(TextView)findViewById(R.id.date);
        igst=(TextView)findViewById(R.id.igst);
        cgst=(TextView)findViewById(R.id.cgst);
        sgst=(TextView)findViewById(R.id.sgst);
        totalprice=(TextView)findViewById(R.id.totalprice);
        price=(TextView)findViewById(R.id.price);
        cust_name=(EditText)findViewById(R.id.custname);
        cust_adrs=(EditText)findViewById(R.id.custadrs);
        gr_wt=(EditText)findViewById(R.id.grwt);
        stone_wt=(EditText)findViewById(R.id.stonewt);
        net_wt=(EditText)findViewById(R.id.netwt);
  //      purity=(EditText)findViewById(R.id.purity);
        mobile=(EditText)findViewById(R.id.mobile);
        pan_no=(EditText)findViewById(R.id.pan_no);
        gst_no=(EditText)findViewById(R.id.gst_no);
        discount=(EditText)findViewById(R.id.discount);
        making_charges=(EditText)findViewById(R.id.making_charges);
        stone_charges=(EditText)findViewById(R.id.stone_charges);
        view=(TextView)findViewById(R.id.view);
        checkbox=(CheckBox)findViewById(R.id.checkbox);
        include_oldgold_totalprice=(TextView)findViewById(R.id.include_oldgold_totalprice);
        pic=(ImageView)findViewById(R.id.pic);

        checkbox_cgst=(CheckBox)findViewById(R.id.checkbox_cgst);
        checkbox_sgst=(CheckBox)findViewById(R.id.checkbox_sgst);
        checkbox_igst=(CheckBox)findViewById(R.id.checkbox_igst);

        LI1.setVisibility(View.GONE);
        view.setVisibility(View.GONE);

        if(!haveNetworkConnection()) {

            Toast.makeText(getApplicationContext(), "You don't have internet connection." , Toast.LENGTH_LONG).show();

            return;
        }else {
            new Category().execute();
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

                igst.setText("");
                sgst.setText("");
                cgst.setText("");
                totalprice.setText("");
                price.setText("");
                net_wt.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        discount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    discount.setText("");
                }
            }
        });

        /*discount.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {




                if(net_wt.getText().toString().isEmpty() || price.getText().toString().isEmpty() ) {
             //       discount.setText("");
                    Toast.makeText(getApplicationContext(), "Please Enter Net Weight ",Toast.LENGTH_LONG).show();

                }else {

                    double CGST = 0, SGST = 0, total = 0;
                    if(!net_wt.getText().toString().isEmpty()){

                        double net= Double.parseDouble(net_wt.getText().toString());

                        if(metal.getSelectedItem().equals("Gold")){

                            double prc;
                            prc= Config.GOLD/10*net;
                        *//*    CGST=prc*Config.CGST/100;
                            SGST=prc*Config.SGST/100;
                            total=prc + CGST + SGST;*//*

                            price.setText(prc+"");
                         //   sgst.setText(SGST + "");
                         //   cgst.setText(CGST + "");
                            totalprice.setText(total + "");


                        }else if(metal.getSelectedItem().equals("Silver")){


                            double prc;
                            prc= Config.SILVER/1000*net;
                        *//*    CGST=prc*Config.CGST/100;
                            SGST=prc*Config.SGST/100;
                            total=prc + CGST + SGST;*//*

                            price.setText(prc+"");
                         //   sgst.setText(SGST + "");
                         //   cgst.setText(CGST + "");
                            totalprice.setText(total + "");

                        }
                    }else {
                     //   sgst.setText("");
                     //   cgst.setText("");
                        totalprice.setText("");
                        price.setText("");

                    }

                        double p=0.0;

                        if (!s.toString().isEmpty()) {
                             p = Double.parseDouble(s.toString());

                        }else {

                        }
                                double prc1 = Double.parseDouble(price.getText().toString());

                                p = 100 - p;
                                double discount = 0;

                                discount = (prc1 * p) / 100;
                                price.setText(discount + "");
                                double prc2 = Double.parseDouble(price.getText().toString());
                             *//*   CGST = prc2 * Config.CGST / 100;
                                SGST = prc2 * Config.SGST / 100;
                                total = prc2 + CGST + SGST;
                                sgst.setText(CGST + "");
                                cgst.setText(SGST + "");*//*
                                totalprice.setText(prc2 + "");

                        }

            //    }

                if(checkbox_cgst.isChecked() || checkbox_sgst.isChecked() || checkbox_igst.isChecked() ){
                    checkbox_cgst.setChecked(false);
                    checkbox_sgst.setChecked(false);
                    checkbox_igst.setChecked(false);

                    Double p=Double.parseDouble(price.getText().toString());
                    totalprice.setText(p+"");

                }
            }
        });*/
        discount.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(net_wt.getText().toString().isEmpty() || price.getText().toString().isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "Please Enter Net Weight ",Toast.LENGTH_LONG).show();

        //            discount.setText("");

                }else {
                    double CGST = 0, SGST = 0, total = 0;
                    if(!net_wt.getText().toString().isEmpty()){

                        double net= Double.parseDouble(net_wt.getText().toString());
                        if(metal.getSelectedItem().equals("Gold")){
                            double prc;
                            prc= Config.GOLD/10*net;
                            price.setText(prc+"");
                            totalprice.setText(total + "");
                        }else if(metal.getSelectedItem().equals("Silver")){
                            double prc;
                            prc= Config.SILVER/1000*net;
                            price.setText(prc+"");
                            totalprice.setText(total + "");
                        }
                    }else {
                        totalprice.setText("");
                        price.setText("");
                    }
                    double p=0.0;

                    if (!s.toString().isEmpty()) {
                        p = Double.parseDouble(s.toString());
                    }else {

                    }
                    double prc1 = Double.parseDouble(price.getText().toString());
                    double stone =0.0,makigcharg=0.0;
                    if(!stone_charges.getText().toString().isEmpty()){
                        stone = Double.parseDouble(stone_charges.getText().toString());
                    }
                    if(!making_charges.getText().toString().isEmpty()){
                        makigcharg = Double.parseDouble(making_charges.getText().toString());
                    }
                    //            double m= Double.parseDouble(s.toString());
                    //       double p= Double.parseDouble(price.getText().toString());
                    double a=p+prc1+stone+makigcharg;
                    price.setText(a+"");

                    double prc2 = Double.parseDouble(price.getText().toString());

                    totalprice.setText(prc2 + "");

                }
                if(checkbox_cgst.isChecked() || checkbox_sgst.isChecked() || checkbox_igst.isChecked() ){
                    checkbox_cgst.setChecked(false);
                    checkbox_sgst.setChecked(false);
                    checkbox_igst.setChecked(false);

                    Double p=Double.parseDouble(price.getText().toString());
                    totalprice.setText(p+"");

                }
            }
        });



        making_charges.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(net_wt.getText().toString().isEmpty() || price.getText().toString().isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "Please Enter Net Weight ",Toast.LENGTH_LONG).show();
          //          making_charges.setText("");
                }else {
                    double CGST = 0, SGST = 0, total = 0;
                    if(!net_wt.getText().toString().isEmpty()){

                        double net= Double.parseDouble(net_wt.getText().toString());
                        if(metal.getSelectedItem().equals("Gold")){
                            double prc;
                            prc= Config.GOLD/10*net;
                            price.setText(prc+"");
                            totalprice.setText(total + "");
                        }else if(metal.getSelectedItem().equals("Silver")){
                            double prc;
                            prc= Config.SILVER/1000*net;
                            price.setText(prc+"");
                            totalprice.setText(total + "");
                        }
                    }else {
                        totalprice.setText("");
                        price.setText("");
                    }
                    double p=0.0;

                    if (!s.toString().isEmpty()) {
                        p = Double.parseDouble(s.toString());
                    }else {

                    }
                    double prc1 = Double.parseDouble(price.getText().toString());
                    double stone =0.0;
                            if(!stone_charges.getText().toString().isEmpty()){
                                stone = Double.parseDouble(stone_charges.getText().toString());
                            }

        //            double m= Double.parseDouble(s.toString());
             //       double p= Double.parseDouble(price.getText().toString());
                    double a=p+prc1+stone;
                    price.setText(a+"");

                    double prc2 = Double.parseDouble(price.getText().toString());

                    totalprice.setText(prc2 + "");

                }
                if(checkbox_cgst.isChecked() || checkbox_sgst.isChecked() || checkbox_igst.isChecked() ){
                    checkbox_cgst.setChecked(false);
                    checkbox_sgst.setChecked(false);
                    checkbox_igst.setChecked(false);

                    Double p=Double.parseDouble(price.getText().toString());
                    totalprice.setText(p+"");

                }
            }
        });


        stone_charges.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(net_wt.getText().toString().isEmpty() || price.getText().toString().isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "Please Enter Net Weight ",Toast.LENGTH_LONG).show();
              //      stone_charges.setText("");
                }else {
                    double CGST = 0, SGST = 0, total = 0;
                    if(!net_wt.getText().toString().isEmpty()){

                        double net= Double.parseDouble(net_wt.getText().toString());
                        if(metal.getSelectedItem().equals("Gold")){
                            double prc;
                            prc= Config.GOLD/10*net;
                            price.setText(prc+"");
                            totalprice.setText(total + "");
                        }else if(metal.getSelectedItem().equals("Silver")){
                            double prc;
                            prc= Config.SILVER/1000*net;
                            price.setText(prc+"");
                            totalprice.setText(total + "");
                        }
                    }else {
                        totalprice.setText("");
                        price.setText("");
                    }
                    double p=0.0;

                    if (!s.toString().isEmpty()) {
                        p = Double.parseDouble(s.toString());
                    }else {

                    }
                    double prc1 = Double.parseDouble(price.getText().toString());
                    double makigchrges=0.0;
                    if(!making_charges.getText().toString().isEmpty()) {
                        makigchrges = Double.parseDouble(making_charges.getText().toString());
                    }
                    //            double m= Double.parseDouble(s.toString());
                    //       double p= Double.parseDouble(price.getText().toString());
                    double a=p+prc1+makigchrges;
                    price.setText(a+"");

                    double prc2 = Double.parseDouble(price.getText().toString());

                    totalprice.setText(prc2 + "");

                }
                if(checkbox_cgst.isChecked() || checkbox_sgst.isChecked() || checkbox_igst.isChecked() ){
                    checkbox_cgst.setChecked(false);
                    checkbox_sgst.setChecked(false);
                    checkbox_igst.setChecked(false);

                    Double p=Double.parseDouble(price.getText().toString());
                    totalprice.setText(p+"");

                }
            }
        });


        net_wt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!discount.getText().toString().isEmpty()){
                        discount.setText("");
                    }

                if(checkbox_cgst.isChecked() || checkbox_sgst.isChecked() || checkbox_igst.isChecked() ){
                    checkbox_cgst.setChecked(false);
                    checkbox_sgst.setChecked(false);
                    checkbox_igst.setChecked(false);

                    Double p=Double.parseDouble(price.getText().toString());
                    totalprice.setText(p+"");

                }

                double total = 0;
                if(!s.toString().isEmpty()){

                    double p= Double.parseDouble(s.toString());

                    if(metal.getSelectedItem().equals("Gold")){

                    double prc;
                        prc= Config.GOLD/10*p;
                      /*  CGST=prc*Config.CGST/100;
                        SGST=prc*Config.SGST/100;
                        total=prc + CGST + SGST;*/

                        price.setText(prc+"");
                    //    sgst.setText(SGST + "");
                    //    cgst.setText(CGST + "");
                        totalprice.setText(prc + "");


                    }else if(metal.getSelectedItem().equals("Silver")){


                        double prc;
                        prc= Config.SILVER/1000*p;
                      /*  CGST=prc*Config.CGST/100;
                        SGST=prc*Config.SGST/100;
                        total=prc + CGST + SGST;*/

                        price.setText(prc+"");
                  //      sgst.setText(SGST + "");
                  //      cgst.setText(CGST + "");
                        totalprice.setText(prc + "");

                    }


                }else {
                 //   sgst.setText("");
                  //  cgst.setText("");
                    totalprice.setText("");
                    price.setText("");

                }



            }
        });



      /*  caret.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
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

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean         isChecked)
            {
                // TODO Auto-generated method stub
                if(isChecked)
                {
                           x=cust_name.getText().toString();
                           y=mobile.getText().toString();
                      //    z=gr_wt.getText().toString();
                    oldgold();
                    view.setVisibility(View.VISIBLE);
                 /*   Double a=Double.parseDouble(bundle.getString("total_price"));
                    Double b=Double.parseDouble(totalprice.getText().toString());

                    if(!a.isNaN()){

                        Double c= b-a;

                        include_oldgold_totalprice.setText(c+"");
                    }
*/
                }
                else
                {
                    view.setVisibility(View.GONE);

                    if(bundle != null  ){
                        bundle.clear();
                    }
            //        bundle.clear();
                    LI1.setVisibility(View.GONE);

                }
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    View();
            }
        });




        checkbox_cgst.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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
        });

        checkbox_sgst.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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

        });

        checkbox_igst.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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
        });







    }


    public void oldgold() {

        LayoutInflater li = LayoutInflater.from(BillActivity.this);
        View rateDialog = li.inflate(R.layout.oldgold_purchase_dialog, null);


        old_custname = (EditText) rateDialog.findViewById(R.id.old_custname);
        old_mobile = (EditText) rateDialog.findViewById(R.id.old_mobile);
        old_itemname = (EditText) rateDialog.findViewById(R.id.old_itemname);
        old_gr_wt = (EditText) rateDialog.findViewById(R.id.old_gr_wt);
        old_less_wt = (EditText) rateDialog.findViewById(R.id.less_wt);
        old_net_wt = (EditText) rateDialog.findViewById(R.id.net_wt);
        old_touch = (EditText) rateDialog.findViewById(R.id.touch);
        old_rate = (EditText) rateDialog.findViewById(R.id.rate);
        old_totalpricee = (EditText) rateDialog.findViewById(R.id.totalprice);
        old_submit = (Button) rateDialog.findViewById(R.id.submit);
        old_cancel = (Button) rateDialog.findViewById(R.id.cancel);


        AlertDialog.Builder alert = new AlertDialog.Builder(BillActivity.this);
        alert.setView(rateDialog);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        old_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(old_custname.getText().toString().isEmpty() || old_mobile.getText().toString().isEmpty() ||
                        old_itemname.getText().toString().isEmpty() ||  old_gr_wt.getText().toString().isEmpty()
                        || old_net_wt.getText().toString().isEmpty() ||  old_totalpricee.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please Enter all the feilds",Toast.LENGTH_LONG).show();
                }else {



                    if(net_wt.getText().toString().isEmpty() || totalprice.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please fill previous  form",Toast.LENGTH_LONG).show();
                    }else {

                        bundle = new Bundle();

                        bundle.putString("cust_name", old_custname.getText().toString());
                        bundle.putString("mobile", old_mobile.getText().toString());
                        bundle.putString("item_name", old_itemname.getText().toString());
                        bundle.putString("gross_wt", old_gr_wt.getText().toString());
                        bundle.putString("less_wt",  old_less_wt.getText().toString());
                        bundle.putString("net_wt", old_net_wt.getText().toString());
                        bundle.putString("rate", old_rate.getText().toString());
                        bundle.putString("touch", old_touch.getText().toString());
                        bundle.putString("total_price", old_totalpricee.getText().toString());

                        Toast.makeText(getApplicationContext(), "Saved",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();

                        Double a=Double.parseDouble(bundle.getString("total_price"));
                        Double b=Double.parseDouble(totalprice.getText().toString());

                        if(!a.isNaN()){

                            LI1.setVisibility(View.VISIBLE);
                            Double c= b-a;

                            include_oldgold_totalprice.setText(c+"");
                        }
                    }
                }

            }
        });

        old_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                checkbox.setChecked(false);
            }
        });

        old_custname.setText(x);
        old_mobile.setText(y);
    //    old_gr_wt.setText(z);


       /* if(!old_gr_wt.getText().toString().isEmpty()){
            old_less_wt.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty()) {

                        Double l = Double.parseDouble(s.toString());
                        Double g = Double.parseDouble(old_gr_wt.getText().toString());
                        Double n = g - l;
                        old_net_wt.setText(n + "");
                    }
                }
            });
        }else {

            Toast.makeText(getApplicationContext(), "Enter Gr wt",Toast.LENGTH_LONG).show();
        }

      if (!old_net_wt.getText().toString().isEmpty()) {


          old_touch.addTextChangedListener(new TextWatcher() {

              public void afterTextChanged(Editable s) {
              }

              public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              }

              public void onTextChanged(CharSequence s, int start, int before, int count) {

                  if (!s.toString().isEmpty()) {
                      Double t = Double.parseDouble(s.toString());
                      Double n = Double.parseDouble(old_net_wt.getText().toString());
                      p = t / n;
                  }
              }
          });
      }else {
          Toast.makeText(getApplicationContext(), "Enter Net wt",Toast.LENGTH_LONG).show();
      }


        if (!old_net_wt.getText().toString().isEmpty()) {
            old_rate.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (!s.toString().isEmpty()) {
                        Double r = Double.parseDouble(s.toString());
                        Double total = p * r;
                        old_totalpricee.setText(total + "");
                    }
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "Enter Net wt",Toast.LENGTH_LONG).show();
        }*/
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
        getMenuInflater().inflate(R.menu.activity_bill, menu);
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

    public void submit(View view) {
        mt= metal.getSelectedItem().toString();
        ct=caret.getSelectedItem().toString();
        if(mt.equals("Silver")){
            ct="";
        }

        if(cust_name.getText().toString().isEmpty() || cust_adrs.getText().toString().isEmpty()
                || mobile.getText().toString().isEmpty()
                || gr_wt.getText().toString().isEmpty() ||
                net_wt.getText().toString().isEmpty() || price.getText().toString().isEmpty() ||
                encodedImage.isEmpty()){

            Toast.makeText(getApplicationContext(), "Please Enter all the feilds",Toast.LENGTH_LONG).show();

        }else {


            AlertDialog.Builder alertDialog = new AlertDialog.Builder(BillActivity.this);
            //   alertDialog.setTitle("Confirm ...?");
            alertDialog.setMessage("Confirm ...?");

            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {


                    String OLD_CUSTNAME="",MOBILE="",OLD_ITEMNAME="",GE_WT="",LESS_WT="",NET_WT="",RATE="",TOUCH="",TOTAL_PRICE="";


                       if(bundle != null  ){


                        OLD_CUSTNAME = bundle.getString("cust_name");
                        MOBILE = bundle.getString("mobile");
                        OLD_ITEMNAME = bundle.getString("item_name");
                        GE_WT = bundle.getString("gross_wt");
                        LESS_WT = bundle.getString("less_wt");
                        NET_WT = bundle.getString("net_wt");
                        RATE = bundle.getString("rate");
                        TOUCH = bundle.getString("touch");
                        TOTAL_PRICE = bundle.getString("total_price");

                    }

                    InputStream is = null;
                    String line ="";
                    String result = "";
                    HttpResponse response = null;
                    int status=0;
                    try {

                        HttpParams httpParams = new BasicHttpParams();
                        HttpClient client = new DefaultHttpClient(httpParams);
                        //       String url = Config.ORDERS;
                        HttpPost request = new HttpPost(Config.Billing);

                        JSONObject json = new JSONObject();

                        json.put("bill_date",DATE);
                        json.put("cust_name",cust_name.getText().toString());
                        json.put("cust_adrs",cust_adrs.getText().toString());
                        json.put("mobile",mobile.getText().toString());
                        json.put("cust_pan_number",pan_no.getText().toString());
                        json.put("cust_gst_number",gst_no.getText().toString());
                        json.put("metal_type",metal.getSelectedItem().toString());
                        json.put("caret",ct);
                        json.put("sales_image",encodedImage);
                        json.put("category",category.getSelectedItem().toString());
                        json.put("gross_wt",gr_wt.getText().toString());
                        json.put("stone_wt",stone_wt.getText().toString());
                        json.put("net_wt",net_wt.getText().toString());
                        json.put("discount",discount.getText().toString());
                        json.put("making_charges",making_charges.getText().toString());
                        json.put("stone_charges",stone_charges.getText().toString());
                        json.put("price",price.getText().toString());
                        json.put("igst",igst.getText().toString());
                        json.put("cgst",cgst.getText().toString());
                        json.put("sgst",sgst.getText().toString());
                        json.put("total_price",totalprice.getText().toString());
                        json.put("include_oldgold_totalprice",include_oldgold_totalprice.getText().toString());




                        json.put("old_item_name",OLD_ITEMNAME);
                        json.put("old_gr_wt",GE_WT);
                        json.put("old_less_wt",LESS_WT);
                        json.put("old_net_wt",NET_WT);
                        json.put("old_touch",TOUCH);
                        json.put("old_rate",RATE);
                        json.put("old_total_price",TOTAL_PRICE);



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
                       /* price.setText("");
                        cust_name.setText("");
                        cust_adrs.setText("");
                        mobile.setText("");
                        gr_wt.setText("");
                        stone_wt.setText("");
                        net_wt.setText("");
                        discount.setText("");*/
                        encodedImage="";


                        if(bundle != null  ){
                            bundle.clear();
                        }
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);

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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void View() {




                        String OLD_CUSTNAME="",MOBILE="",OLD_ITEMNAME="",GR_WT="",LESS_WT="",NET_WT="",RATE="",TOUCH="",TOTAL_PRICE="";


                        OLD_CUSTNAME=  bundle.getString("cust_name");
                        MOBILE= bundle.getString("mobile");
                        OLD_ITEMNAME= bundle.getString("item_name");
                        GR_WT=bundle.getString("gross_wt");
                        LESS_WT= bundle.getString("less_wt");
                        NET_WT= bundle.getString("net_wt");
                        RATE= bundle.getString("rate");
                        TOUCH= bundle.getString("touch");
                        TOTAL_PRICE=bundle.getString("total_price");



        LayoutInflater li = LayoutInflater.from(BillActivity.this);
                        View rateDialog = li.inflate(R.layout.oldgold_purchase_dialog, null);


                        old_custname = (EditText) rateDialog.findViewById(R.id.old_custname);
                        old_mobile = (EditText) rateDialog.findViewById(R.id.old_mobile);
                        old_itemname = (EditText) rateDialog.findViewById(R.id.old_itemname);
                        old_gr_wt = (EditText) rateDialog.findViewById(R.id.old_gr_wt);
                        old_less_wt = (EditText) rateDialog.findViewById(R.id.less_wt);
                        old_net_wt = (EditText) rateDialog.findViewById(R.id.net_wt);
                        old_touch = (EditText) rateDialog.findViewById(R.id.touch);
                        old_rate = (EditText) rateDialog.findViewById(R.id.rate);
                        old_totalpricee = (EditText) rateDialog.findViewById(R.id.totalprice);
                        old_submit = (Button) rateDialog.findViewById(R.id.submit);
                        old_cancel = (Button) rateDialog.findViewById(R.id.cancel);


                        AlertDialog.Builder alert = new AlertDialog.Builder(BillActivity.this);
                        alert.setView(rateDialog);
                        final AlertDialog alertDialog = alert.create();
                        alertDialog.setCancelable(true);
                        alertDialog.show();

                        old_submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(old_custname.getText().toString().isEmpty() || old_mobile.getText().toString().isEmpty() ||
                                        old_itemname.getText().toString().isEmpty() ||  old_gr_wt.getText().toString().isEmpty() ||
                                        old_less_wt.getText().toString().isEmpty() || old_net_wt.getText().toString().isEmpty() || old_touch.getText().toString().isEmpty() ||
                                        old_rate.getText().toString().isEmpty() || old_totalpricee.getText().toString().isEmpty())
                                {
                                    Toast.makeText(getApplicationContext(), "Please Enter all the feilds",Toast.LENGTH_LONG).show();
                                }else {

                                    //    bundle = new Bundle();

                                    bundle.putString("cust_name", old_custname.getText().toString());
                                    bundle.putString("mobile", old_mobile.getText().toString());
                                    bundle.putString("item_name", old_itemname.getText().toString());
                                    bundle.putString("gross_wt", old_gr_wt.getText().toString());
                                    bundle.putString("less_wt",  old_less_wt.getText().toString());
                                    bundle.putString("net_wt", old_net_wt.getText().toString());
                                    bundle.putString("rate", old_rate.getText().toString());
                                    bundle.putString("touch", old_touch.getText().toString());
                                    bundle.putString("total_price", old_totalpricee.getText().toString());

                                    Toast.makeText(getApplicationContext(), "Saved",Toast.LENGTH_LONG).show();
                                    alertDialog.dismiss();


                                    Double a=Double.parseDouble(bundle.getString("total_price"));
                                    Double b=Double.parseDouble(totalprice.getText().toString());

                                    if(!a.isNaN()){

                                        LI1.setVisibility(View.VISIBLE);
                                        Double c= b-a;

                                        include_oldgold_totalprice.setText(c+"");
                                    }
                                }

                            }
                        });

                        old_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                if(!OLD_CUSTNAME.isEmpty() || !MOBILE.isEmpty() || !OLD_ITEMNAME.isEmpty() || !GR_WT.isEmpty() || !LESS_WT.isEmpty()
                        || !NET_WT.isEmpty() || !RATE.isEmpty() || !TOUCH.isEmpty() || !TOTAL_PRICE.isEmpty()){


                            old_custname.setText(OLD_CUSTNAME);
                            old_mobile.setText(MOBILE);
                            old_itemname.setText(OLD_ITEMNAME);
                            old_gr_wt.setText(GR_WT);
                            old_less_wt.setText(LESS_WT);
                            old_net_wt.setText(NET_WT);
                            old_rate.setText(RATE);
                            old_touch.setText(TOUCH);
                            old_totalpricee.setText(TOTAL_PRICE);
                        }


                        old_less_wt.addTextChangedListener(new TextWatcher() {

                            public void afterTextChanged(Editable s) {}

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                                Double l=Double.parseDouble(s.toString());
                                Double g=Double.parseDouble(old_gr_wt.getText().toString());
                                Double n=g-l;
                                old_net_wt.setText(n+"");
                            }
                        });


                        old_touch.addTextChangedListener(new TextWatcher() {

                            public void afterTextChanged(Editable s) {}

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                Double t=Double.parseDouble(s.toString());
                                Double n=Double.parseDouble(old_net_wt.getText().toString());
                                p= t/n;
                            }
                        });

                        old_rate.addTextChangedListener(new TextWatcher() {

                            public void afterTextChanged(Editable s) {}

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                Double r=Double.parseDouble(s.toString());
                                Double total=p*r;
                                old_totalpricee.setText(total+"");
                            }
                        });

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
        if (requestCode == 100 && resultCode == RESULT_OK) {


            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //  pic.setImageBitmap(photo);

            //   Bitmap bit=photo;
            encodeTobase64(photo);


            /*Bitmap bm = BitmapFactory.decodeFile(data.getExtras().get("data"));
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            byte[] ba = bao.toByteArray();*/


          /*  ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG,100, baos);
            byte [] b=baos.toByteArray();
          //  String temp=Base64.encodeToString(b, Base64.DEFAULT);

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
            adapter2=new ArrayAdapter<String>(BillActivity.this,R.layout.spinner,R.id.text,listitem2);
            category.setAdapter(adapter2);
            listitem2.addAll(CAT);
            adapter2.notifyDataSetChanged();


            ArrayAdapter<String> adapter3;
            ArrayList<String> listitem3 = new ArrayList<>();
            adapter3=new ArrayAdapter<String>(BillActivity.this,R.layout.spinner,R.id.text,listitem3);
            caret.setAdapter(adapter3);
            listitem3.addAll(PURITY);
            adapter3.notifyDataSetChanged();

        }
    }


    public void getautoname(){

        Tabels =new Tabels(BillActivity.this,"data",null,1);
        mDb= Tabels.getWritableDatabase();
        mDb= Tabels.getReadableDatabase();
    }

}
