package com.example.tudor.foodhunt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout email, password, business, location;
    EditText email_t, password_t, business_t, location_t;
    Button register,set_location;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = (TextInputLayout) findViewById(R.id.t_email);
        password = (TextInputLayout) findViewById(R.id.t_password);
        business = (TextInputLayout) findViewById(R.id.t_business);
        location = (TextInputLayout) findViewById(R.id.t_location);

        email_t = (EditText) findViewById(R.id.txt_email);
        password_t = (EditText)findViewById(R.id.txt_password);
        business_t = (EditText)findViewById(R.id.txt_business);
        location_t = (EditText)findViewById(R.id.txt_location);

        Intent intent = getIntent();
        String latlng = intent.getStringExtra("latlng");

        if(latlng!=null && latlng.contains(":")) {
            String last = "";
            String[] ress = latlng.split(":");
            last = ress[0].substring(0,4) + ":" + ress[1].substring(0,4);
            location_t.setText(last);
        }

        register = (Button) findViewById(R.id.btn_register);
        set_location = (Button) findViewById(R.id.set_pin);

        set_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent screen2 = new Intent(RegisterActivity.this, GetLocationActivity.class);
                startActivity(screen2);
            }
        });



        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String emailtext = email_t.getText().toString();
                final String passwordtext = password_t.getText().toString();
                final String businesstext = business_t.getText().toString();

                //intent get extra
                Intent intent = getIntent();
                String latlng = intent.getStringExtra("latlng");

                String txt = "";
                if(latlng != null && latlng.contains(":"))
                    txt = latlng;


                final String locationtext = txt;

                location_t.setText(txt);

                final CountDownLatch latch = new CountDownLatch(1);

                final boolean[] result = new boolean[1];

                Thread uiThread = new HandlerThread("loginThread") {

                    @Override
                    public void run() {
                        myThread registerManager = new myThread();
                        if(!locationtext.equals(""))
                            result[0] = registerManager.registerRequest(emailtext,passwordtext,businesstext,locationtext);
                        latch.countDown();
                    }
                };

                uiThread.start();
                try {
                    latch.await();
                } catch (Exception e) {e.printStackTrace();}


                if(result[0]){
                    Intent screen2 = new Intent(RegisterActivity.this, Business_Login.class);
                    startActivity(screen2);
                } else{
                    password_t.setText("");
                }
            }
        });







    }
}
