package com.example.tudor.foodhunt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import static android.content.Context.LOCATION_SERVICE;

class Offer {

    String name;
    String location;
    String description;
    String time;
    String photoPath;

    public Offer(String name, String location, String description, String time, String photoPath) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.time = time;
        this.photoPath = photoPath;
    }

    public String toString() {
        return name + " " + location + " " + description + " " + time + " " + photoPath;
    }
}

class myThread implements Runnable {

    public void run() {

    }

    boolean registerRequest(String email, String passwd, String mag_name, String location) {
        String php = "registerRequest.php?";

        php += "email=" + email;
        php += "&passwd=" + passwd;
        php += "&mag_name=" + mag_name;
        php += "&location=" + location;

        String res = "";
        try {
            URL serverUrl = new URL("http://foodhunt.free-programming.org/" + php);
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            // Read response from web server, which will trigger the multipart HTTP request to be sent.
            BufferedReader httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String res_;
            while ((res_ = httpResponseReader.readLine()) != null) {
                res += res_;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (res.equals("success"))
            return true;

        return false;
    }

    boolean loginRequest(String email, String passwd) {
        String php = "loginRequest.php?";

        php += "email=" + email;
        php += "&passwd=" + passwd;

        String res = "";
        try {
            URL serverUrl = new URL("http://foodhunt.free-programming.org/" + php);
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            // Read response from web server, which will trigger the multipart HTTP request to be sent.
            BufferedReader httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String res_;
            while ((res_ = httpResponseReader.readLine()) != null) {
                res += res_;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (res.equals("success"))
            return true;

        return false;
    }

    boolean uploadPhoto(String photo) {
        String res = "";
        try {

            URL serverUrl = new URL("http://foodhunt.free-programming.org/uploadPhoto.php");
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            String boundaryString = "----SomeRandomText";
            String fileUrl = photo;
            File logFileToUpload = new File(fileUrl);

            // Indicate that we want to write to the HTTP request body
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);

            OutputStream outputStreamToRequestBody = urlConnection.getOutputStream();
            BufferedWriter httpRequestBodyWriter =
                    new BufferedWriter(new OutputStreamWriter(outputStreamToRequestBody));

            // Include value from the myFileDescription text area in the post data
            httpRequestBodyWriter.write("\n\n--" + boundaryString + "\n");
            httpRequestBodyWriter.write("Content-Disposition: form-data; name=\"myFileDescription\"");
            httpRequestBodyWriter.write("\n\n");
            httpRequestBodyWriter.write("TestFile");

            // Include the section to describe the file
            httpRequestBodyWriter.write("\n--" + boundaryString + "\n");
            httpRequestBodyWriter.write("Content-Disposition: form-data;"
                    + "name=\"file\";"
                    + "filename=\"" + logFileToUpload.getName() + "\""
                    + "\nContent-Type: image/jpg\n\n");
            httpRequestBodyWriter.flush();

            // Write the actual file contents
            FileInputStream inputStreamToLogFile = new FileInputStream(logFileToUpload);

            int bytesRead;
            byte[] dataBuffer = new byte[1024];
            while ((bytesRead = inputStreamToLogFile.read(dataBuffer)) != -1) {
                outputStreamToRequestBody.write(dataBuffer, 0, bytesRead);
            }

            outputStreamToRequestBody.flush();

            // Mark the end of the multipart http request
            httpRequestBodyWriter.write("\n--" + boundaryString + "--\n");
            httpRequestBodyWriter.flush();

            // Close the streams
            outputStreamToRequestBody.close();
            httpRequestBodyWriter.close();

            // Read response from web server, which will trigger the multipart HTTP request to be sent.
            BufferedReader httpResponseReader =
                    new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String res_;
            while ((res_ = httpResponseReader.readLine()) != null) {
                res += res_;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (res.equals("success"))
            return true;

        return false;
    }

    Vector<Offer> getOffers() {
        Log.d("start", "getting offers");
        String res = "";
        try {

            URL serverUrl = new URL("http://foodhunt.free-programming.org/getOffers.php");
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            // Read response from web server, which will trigger the multipart HTTP request to be sent.
            BufferedReader httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String res_;
            while ((res_ = httpResponseReader.readLine()) != null) {
                res += res_;
            }

            String[] arr = res.split("<br>");
            Vector<Offer> offers = new Vector<>();

            for (int i = 0; i < arr.length; i += 5) {
                offers.add(new Offer(arr[i], arr[i + 1], arr[i + 2], arr[i + 3], arr[i + 4]));
            }

            return offers;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    boolean sendLocation(String email, String location) {
        String php = "sendLocation.php?";

        php += "email=" + email;
        php += "&location=" + location;

        String res = "";
        try {
            URL serverUrl = new URL("http://foodhunt.free-programming.org/" + php);
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            // Read response from web server, which will trigger the multipart HTTP request to be sent.
            BufferedReader httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String res_;
            while ((res_ = httpResponseReader.readLine()) != null) {
                res += res_;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (res.equals("success"))
            return true;

        return false;
    }

    int getDeviceCount(String location) {
        String php = "getDeviceCount.php?";

        php += "location=" + location;

        String res = "";
        try {
            URL serverUrl = new URL("http://foodhunt.free-programming.org/" + php);
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            // Read response from web server, which will trigger the multipart HTTP request to be sent.
            BufferedReader httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String res_;
            while ((res_ = httpResponseReader.readLine()) != null) {
                res += res_;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            return Integer.parseInt(res.replace(" ",""));
        } catch (Exception e) {
            return -1;
        }
    }

    boolean offerRequest(String mag_name, String image, String location, String description, String time) {
        String php = "offerRequest.php?";

        php += "mag_name=" + mag_name;
        php += "&img=" + image;
        php += "&location=" + location;
        php += "&description=" + description;
        php += "&time=" + time;

        String res = "";
        try {
            URL serverUrl = new URL("http://foodhunt.free-programming.org/" + php);
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            // Read response from web server, which will trigger the multipart HTTP request to be sent.
            BufferedReader httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String res_;
            while ((res_ = httpResponseReader.readLine()) != null) {
                res += res_;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (res.equals("success"))
            return true;

        return false;
    }

    String[] getInfo(String id){

        String res = "";
        try {
            URL serverUrl = new URL("http://foodhunt.free-programming.org/getter.php?id="+id);
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            // Read response from web server, which will trigger the multipart HTTP request to be sent.
            BufferedReader httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String res_;
            while ((res_ = httpResponseReader.readLine()) != null) {
                res += res_;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!res.equals("") && res.contains(":") && !res.contains("error")){
            return res.split(":");
        }
        return null;
    }

    Bitmap getBitmap(String photoPath) {
        try {
            URL myURL = new URL("http://foodhunt.free-programming.org/" + photoPath);
            Bitmap bmp = BitmapFactory.decodeStream(myURL.openConnection().getInputStream());
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void start() {
        new Thread(this, "mapsThread");
    }
}

class auxMarker {
    String name;
    String description;
    Double lat;
    Double lng;
    String time;
    Bitmap bmp;

    public auxMarker(String name, String description, String time, Double lat, Double lng, Bitmap bmp) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.lat = lat;
        this.lng = lng;
        this.bmp = bmp;
    }
}

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Button log_out = (Button) findViewById(R.id.lgout);

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LocationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Log.d("kys","here we go");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(LocationActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(LocationActivity.this,
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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        final CountDownLatch latch = new CountDownLatch(1);

        final Vector<auxMarker>[] markers = new Vector[1];

        Thread uiThread = new HandlerThread("MapThread") {
            Vector<Offer> offers;
            @Override
            public void run() {
                Log.d("kys","1");
                myThread mapsManager = new myThread();
                offers = mapsManager.getOffers();
                Log.d("kys","2");
                markers[0] = new Vector<>();
                if (offers != null) {
                    for (final Offer o : offers) {
                        Log.d("kys","3");
                        String[] coord = o.location.split(":");
                        double lat = Double.parseDouble(coord[0]);
                        double lng = Double.parseDouble(coord[1]);

                        LatLng mark = new LatLng(lat, lng);

                        int count = mapsManager.getDeviceCount(lat+":"+lng);
                        String s = "";
                        if(count <= 3)
                            s = "uploads/3.png";
                        else if(count <= 10)
                            s = "uploads/2.png";
                        else
                            s = "uploads/1.png";

                        Bitmap bmp = new myThread().getBitmap(s);

                        if (bmp != null) {
                            Log.d("kys","4");
                            markers[0].add(new auxMarker(o.name,o.description,o.time,lat,lng,bmp));
                        }
                    }
                } else Log.d("err", "null offers");

                latch.countDown();
            }
        };
        Log.d("kys","5");
        uiThread.start();
        try {
            latch.await();
        } catch (Exception e) {e.printStackTrace();}

        Log.d("kys","6");
        if(markers[0]!= null){
            Log.d("kys","7");
            for(auxMarker m : markers[0]){
                String[] niceTime = m.time.split(" ")[1].split(":");

                mMap.addMarker(new MarkerOptions().title(m.name)
                        .position(new LatLng(m.lat,m.lng))
                        .snippet(niceTime[0]+":"+niceTime[1])
                        .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(m.bmp, 130, 200, false))))
                        .setSnippet(m.description+" ("+niceTime[0]+":"+niceTime[1]+")");
            }
        } else Log.d("err", "ma dau batut");

        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        mMap.animateCamera(cameraUpdate);

    }

}