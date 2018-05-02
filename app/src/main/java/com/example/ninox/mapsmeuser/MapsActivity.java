package com.example.ninox.mapsmeuser;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String DATA_URL = "http://bus.azurewebsites.net/getpos.php";
    public static final String JSON_ARRAY = "result";
    public static final String TAG_USERNAME = "id";
    private GoogleMap mMap;
    private ArrayList<String> positions;
    private JSONArray result;
    private static final String TAG = "MyActivity";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private MapStyleOptions style;
    private MapStyleOptions style1;
    private MapStyleOptions style2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        positions = new ArrayList<String>();
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view2);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open , R.string.colse);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();



        style = MapStyleOptions.loadRawResourceStyle(this, R.raw.style);
        style1 = MapStyleOptions.loadRawResourceStyle(this, R.raw.jj);

        style2 = MapStyleOptions.loadRawResourceStyle(this, R.raw.normal);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case(R.id.normal_mode):
                        mDrawerLayout.closeDrawers();

                        mMap.setMapStyle(style2);

                        break;
                    case(R.id.nav_account):
                        mDrawerLayout.closeDrawers();

                        mMap.setMapStyle(style);
                        break;
                    case(R.id.Logout):
                        finish();
                        break;
                    case(R.id.nav_settings):
                        mDrawerLayout.closeDrawers();

                        mMap.setMapStyle(style1);
                }
                return true;
            }
        });

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

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
       // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
      //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


      // setUpMap();


        final Handler h = new Handler();
        final int delay = 10000;



        h.postDelayed(new Runnable(){
            public void run(){
                mMap.clear();

                setUpMap();
                getData();

                h.postDelayed(this, delay);
            }
        }, delay);



    }

    protected void setUpMap() {
        Log.v(TAG, "new setUpMap1" );

        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").snippet("Snippet"));
        Log.v(TAG, "new setUpMap2" );

        // Enable MyLocation Layer of Google Map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        Log.v(TAG, "new setUpMap3" );

        mMap.setMyLocationEnabled(true);
        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.v(TAG, "new setUpMap4" );

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        provider = "network";
        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);

        Log.v(TAG, "new setUpMap5" );

        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);



        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        Log.v(TAG, "new setUpMap6"+myLocation.getLatitude() );
        mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").snippet("Consider yourself located"));
        Log.v(TAG, "new setUpMap7"+ myLocation.getLongitude() );
    }

    private void getData(){

        StringRequest stringRequest = new StringRequest(DATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            result = j.getJSONArray(JSON_ARRAY);
                            DrowPos(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void DrowPos(JSONArray j){
        String la;
        String lo;
        double dla;
        double dlo;
        int id;

        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                la = json.getString("lati");
                lo = json.getString("long");

                dla=  Double.parseDouble(la);
                dlo = Double.parseDouble(lo);
                Log.v(TAG, "9" );
                LatLng sydney = new LatLng(dla,dlo);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Bus").icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)));

                positions.add(json.getString(TAG_USERNAME));
                Log.v(TAG, "new markers" );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)){
            return true;}
        return super.onOptionsItemSelected(item);
    }
}
