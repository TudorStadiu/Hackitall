package com.example.tudor.foodhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.CountDownLatch;


public class Business_ProfileActivity extends AppCompatActivity {
    EditText offer, time;
    Button set_offer, home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business__profile);

        offer = (EditText) findViewById(R.id.txt_offer);
        time = (EditText)  findViewById(R.id.txt_time);
        home = (Button) findViewById(R.id.to_home);
        set_offer = (Button) findViewById(R.id.button);




        set_offer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("kys","1");
                final String ofr = offer.getText().toString();
                final String tm = "20000000"+time.getText().toString().replace(":","")+"00";
                home.setVisibility(View.VISIBLE);
                home.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent i = new Intent(Business_ProfileActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                });


                final CountDownLatch latch = new CountDownLatch(1);

                final boolean[] result = new boolean[1];

                Log.d("kys","0");

                Thread uiThread = new HandlerThread("loginThread") {

                    @Override
                    public void run() {
                        Log.d("kys","3");
                        myThread registerManager = new myThread();
                        Log.d("kys","4");
                        String token = "";
                        SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
                        token = settings.getString("id", token);
                        Log.d("kys","5 6 "+token);
                        String[] getter = registerManager.getInfo(token);
                        Log.d("kys","7");
                        if(getter!=null && getter.length > 2) {
                            Log.d("kys","8 "+getter[0] + " " + getter[1]);
                            result[0] = registerManager.offerRequest(getter[0], "test.png", getter[1]+":"+getter[2], ofr, tm);
                        }
                        Log.d("kys","9");
                        latch.countDown();
                    }
                };
                Log.d("kys","2");
                uiThread.start();
                try {
                    latch.await();
                } catch (Exception e) {e.printStackTrace();}

                Log.d("kys","10");
                if(result[0]){
                    Log.d("kys","11");
                    offer.setText("");
                    time.setText("");
                } else{
                    //nothing
                }
            }
        });
    }
}
