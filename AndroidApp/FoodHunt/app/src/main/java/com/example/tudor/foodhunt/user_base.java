package com.example.tudor.foodhunt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import org.w3c.dom.Text;

import java.util.concurrent.CountDownLatch;

public class user_base extends AppCompatActivity {

    EditText editText;
    Button search;
    Button back;
    TextView result;

    Double lat;
    Double lng;

    final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            lat = location.getLatitude();
            lng = location.getLongitude();
            Log.d("location",lat+" "+lng);

            final CountDownLatch latch = new CountDownLatch(1);

            Thread uiThread = new HandlerThread("loginThread") {

                @Override
                public void run() {
                    myThread registerManager = new myThread();
                    SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
                    String token = "";
                    token = settings.getString("id", token);
                    registerManager.sendLocation(token,lat+":"+lng);
                    latch.countDown();
                }
            };

            uiThread.start();
            try {
                latch.await();
            } catch (Exception e) {e.printStackTrace();}

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 42;

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    Log.d("rip","locationManager");
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, mLocationListener);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private GoogleMap mMap;

    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

    }

    void location(){

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Log.d("kys","here we go");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(user_base.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(user_base.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(user_base.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            Log.d("rip","locationManager");
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, mLocationListener);
            Location curr = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lat = curr.getLatitude();
            lng = curr.getLongitude();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_base);

        editText = (EditText)findViewById(R.id.editText);
        search = (Button) findViewById(R.id.button3);
        back = (Button) findViewById(R.id.button4);
        result = (TextView) findViewById(R.id.textView2);

        location();

        final FoodHunt foodHunt = new FoodHunt();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(user_base.this, User_Login.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = editText.getText().toString();
                editText.setText("");
                result.setText("Cautare...");

                FoodHunt.Tuple _result = foodHunt.smartSearch(search,lat,lng);

                if(_result != null) {

                    double reducere = 1;
                    if (_result.product.pretVechi > 0)
                        reducere = _result.product.pretNou / _result.product.pretVechi;

                    String toPrint = _result.magazinMap.magazin + " la distanta " + (int) foodHunt.distanceBetween(lat, lng, _result.magazinMap.latitude, _result.magazinMap.longitude) + "m :\n" + _result.product + "\nReducere " + (int) ((1 - reducere) * 100) + "%";
                    result.setText(toPrint);
                } else {
                    result.setText("Cautarea nu a returnat nimic");
                }
            }
        });
    }
}
