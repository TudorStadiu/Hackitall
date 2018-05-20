package com.example.tudor.foodhunt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class User_Login extends AppCompatActivity {

    Button search;
    Button map;
    Button products;

    LoginButton loginButton;
    CallbackManager callbackManager;
    String guest_id;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__login);

        search = (Button)findViewById(R.id.guest);
        map = (Button)findViewById(R.id.guest2);
        products = (Button) findViewById(R.id.guest3);

        map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent screen2 = new Intent(User_Login.this, LocationActivity.class);
                startActivity(screen2);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent screen2 = new Intent(User_Login.this, user_base.class);
                startActivity(screen2);
            }
        });

        products.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent screen2 = new Intent(User_Login.this, viewProducts.class);
                startActivity(screen2);
            }
        });


        /*
        FacebookSdk.setApplicationId("297270834106150");
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_user__login);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","public_profile");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                String user_id = loginResult.getAccessToken().getUserId();
                String access_token = loginResult.getAccessToken().getToken();
                Log.d("informatii utilizator", "user: "+user_id+"\ntoken: "+access_token);
                SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("id",user_id);
                editor.commit();




               // boolean loggedIn = true;

                loginButton.setVisibility(View.INVISIBLE);
                Intent main = new Intent(User_Login.this, LocationActivity.class);
                startActivity(main);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("id", guest_id);
        editor.commit();
    */


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
