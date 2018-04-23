package com.example.vadi.jewellay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.InputStream;

public class LoginActivity extends Activity {

    private EditText username;
    private EditText password;
    private TextView register;
    private Button register_submit;
    private EditText register_confirm_password,register_password,register_email,register_username;
    private Button cancel;

     AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        register=(TextView)findViewById(R.id.register);

        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);


        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                register();
            }
        });
    }

    public void Login(View view) {
       /* if(username.getText().toString().equals("admin")&& password.getText().toString().equals("admin")){
            Intent intent= new Intent (getApplicationContext(),HomeActivity.class);
            startActivity(intent);
        }else {
            Toast.makeText(getApplicationContext(), "Invalid Username Or Password ",Toast.LENGTH_LONG).show();
        }*/
        if(username.getText().toString().isEmpty() || username.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Invalid Username Or Password ",Toast.LENGTH_LONG).show();
        }else {
            login();
        }
     //   Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
     //   startActivity(intent);

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        username.setText("");
        password.setText("");
    }



    public void register(){
        LayoutInflater li = LayoutInflater.from(LoginActivity.this);
        View rateDialog = li.inflate(R.layout.register, null);


        register_username = (EditText) rateDialog.findViewById(R.id.register_username);
        register_email = (EditText) rateDialog.findViewById(R.id.register_email);
        register_password = (EditText) rateDialog.findViewById(R.id.register_password);
        register_confirm_password = (EditText) rateDialog.findViewById(R.id.register_confirm_password);
        register_submit = (Button) rateDialog.findViewById(R.id.register_submit);
        cancel = (Button) rateDialog.findViewById(R.id.cancel);

        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
        alert.setView(rateDialog);
        alertDialog = alert.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        register_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(register_username.getText().toString().isEmpty() || register_email.getText().toString().isEmpty()
                        || register_password.getText().toString().isEmpty() || register_confirm_password.getText().toString().isEmpty()){

                    Toast.makeText(getApplicationContext(), "Please Enter all the feilds",Toast.LENGTH_LONG).show();
                }else {
                    String pass=register_password.getText().toString();
                    if(register_confirm_password.getText().toString().equals(pass)){

                 //       confirm();
                    }else {
                        Toast.makeText(getApplicationContext(), "Password do not Match",Toast.LENGTH_LONG).show();

                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }


    public void confirm() {
        try{

            InputStream is = null;
            String line ="";
            String result = "";
            HttpResponse response = null;
            int status=0;
            try {

                HttpParams httpParams = new BasicHttpParams();
                HttpClient client = new DefaultHttpClient(httpParams);
                //       String url = Config.ORDERS;
                HttpPost request = new HttpPost(Config.Register);

                JSONObject json = new JSONObject();
                json.put("name",register_username.getText().toString());
                json.put("email",register_email.getText().toString());
                json.put("password",register_password.getText().toString());

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
                alertDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Successfully Registered ",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getApplicationContext(), "Plz Check Connection"  ,Toast.LENGTH_LONG).show();
            }

        }catch (Exception e){

        }
    }


    public void login() {

        try{

            InputStream is = null;
            String line ="";
            String result = "";
            HttpResponse response = null;
            int status=0;
            try {

                HttpParams httpParams = new BasicHttpParams();
                HttpClient client = new DefaultHttpClient(httpParams);
                //       String url = Config.ORDERS;
                HttpPost request = new HttpPost(Config.Login);

                JSONObject json = new JSONObject();
                json.put("email",username.getText().toString());
                json.put("password",password.getText().toString());

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
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(getApplicationContext(), "Invalid Username or password",Toast.LENGTH_LONG).show();

            }

        }catch (Exception e){

        }


    }




}
