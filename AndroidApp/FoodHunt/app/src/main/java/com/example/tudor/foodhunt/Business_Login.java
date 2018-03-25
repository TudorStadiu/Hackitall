package com.example.tudor.foodhunt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.HandlerThread;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Business_Login extends AppCompatActivity {
    Button signIn, register;
    TextInputLayout email, password;
    EditText t_email, t_password;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business__login);

        signIn = (Button) findViewById(R.id.sign_in);
        register = (Button) findViewById(R.id.register);
        email = (TextInputLayout) findViewById(R.id.text_email);
        password = (TextInputLayout) findViewById(R.id.text_password);
        t_email = (EditText) findViewById(R.id.txt_email);
        t_password = (EditText) findViewById(R.id.txt_password);


        String emailtext = t_email.getText().toString();
        String passwordtext = t_password.getText().toString();




        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("test","test");
                final String emailtext = t_email.getText().toString();
                final String passwordtext = t_password.getText().toString();

                final CountDownLatch latch = new CountDownLatch(1);

                final boolean[] result = new boolean[1];

                Thread uiThread = new HandlerThread("loginThread") {

                    @Override
                    public void run() {
                        myThread registerManager = new myThread();
                        result[0] = registerManager.loginRequest(emailtext,passwordtext);
                        latch.countDown();
                    }
                };

                uiThread.start();
                try {
                    latch.await();
                } catch (Exception e) {e.printStackTrace();}


                if(result[0]){
                    //login succesful
                    SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("id", emailtext);
                    editor.commit();
                    Log.d("set email",emailtext);
                    Intent screen2 = new Intent(Business_Login.this, Business_ProfileActivity.class);
                    startActivity(screen2);
                } else{
                    //login failed
                    t_password.setText("");
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent screen2 = new Intent(Business_Login.this, RegisterActivity.class);
                startActivity(screen2);
            }
        });

    }


        boolean registerRequest(String email, String passwd, String mag_name, String location){

        String php = "registerRequest.php?";

        php += "email="+email;
        php += "&passwd="+passwd;
        php += "&mag_name="+mag_name;
        php += "&location="+location;

        String res = "";
        try {
            URL serverUrl = new URL("http://foodhunt.free-programming.org/"+php);
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            // Read response from web server, which will trigger the multipart HTTP request to be sent.
            BufferedReader httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String res_;
            while ((res_ = httpResponseReader.readLine()) != null) {
                res+=res_;
            }

        }catch (Exception e){e.printStackTrace();}

        if(res.equals("success"))
            return true;

        return false;
    }

    static boolean loginRequest(String email, String passwd){
        String php = "loginRequest.php?";

        php += "email="+email;
        php += "&passwd="+passwd;

        String res = "";
        try {
            URL serverUrl = new URL("http://foodhunt.free-programming.org/"+php);
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            // Read response from web server, which will trigger the multipart HTTP request to be sent.
            BufferedReader httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String res_;
            while ((res_ = httpResponseReader.readLine()) != null) {
                res+=res_;
            }

        }catch (Exception e){e.printStackTrace();}

        if(res.equals("success"))
            return true;

        return false;
    }
}
