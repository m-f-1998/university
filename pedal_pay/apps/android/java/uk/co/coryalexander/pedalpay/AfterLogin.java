package uk.co.coryalexander.pedalpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class AfterLogin extends AppCompatActivity implements OnMapReadyCallback {

    HashMap<String, LatLng> stations = new HashMap<String, LatLng>();
    static int hour, minute, day, month, year;
    static boolean showDate = false;
    static Marker selectedMarker;
    Button btnBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BottomNavigationView navBottom = (BottomNavigationView) findViewById(R.id.navBottom);
      navBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.action_book:
                        break;
                    case R.id.action_checkOut:
                        finish();
                        Intent checkOutIntent = new Intent(getApplicationContext(), CheckOutActivity.class);
                        startActivity(checkOutIntent);

                        break;
                    case R.id.action_profile:
                        finish();
                        Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(profileIntent);
                        break;
                }
                return false;
            }
        });
        stations.put("haymarket", new LatLng(55.9456, -3.2183));
        stations.put("waverley", new LatLng(55.9520, -3.1900));
        stations.put("holyrood", new LatLng(55.9527, -3.1723));
        stations.put("sighthill", new LatLng(55.9217, -3.2868));
        stations.put("granton", new LatLng(55.9828, -3.2334));
        stations.put("fortKinnaird", new LatLng(55.9343, -3.1045));
        stations.put("leith", new LatLng(55.9755, -3.1665));
        stations.put("edinburghZoo", new LatLng(55.9424, -3.2686));
        stations.put("morningside", new LatLng(55.9277, -3.2101));
        stations.put("liberton", new LatLng(55.9132, -3.1600));
        stations.put("heriotwatt", new LatLng(55.9097, -3.3203));


        btnBook = (Button) findViewById(R.id.btnBook);
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookIntent = new Intent(getApplicationContext(), BookActivity.class);
                if(AfterLogin.selectedMarker == null) {
                    //Toast t = Toast.makeText(getApplicationContext(), "Tried to book with no marker selected?", Toast.LENGTH_LONG);
                   // t.show();
                    return;
                }
                bookIntent.putExtra("location", AfterLogin.selectedMarker.getTitle());
                startActivity(bookIntent);

            }
        });





    }

     void showDate() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(stations.get("haymarket")).title("Haymarket Station"));
        googleMap.addMarker(new MarkerOptions().position(stations.get("waverley")).title("Waverley Station"));
        googleMap.addMarker(new MarkerOptions().position(stations.get("holyrood")).title("Holyrood Palace"));
        googleMap.addMarker(new MarkerOptions().position(stations.get("sighthill")).title("Sighthill"));
        googleMap.addMarker(new MarkerOptions().position(stations.get("granton")).title("Granton"));
        googleMap.addMarker(new MarkerOptions().position(stations.get("fortKinnaird")).title("Fort Kinnaird"));
        googleMap.addMarker(new MarkerOptions().position(stations.get("leith")).title("Leith"));
        googleMap.addMarker(new MarkerOptions().position(stations.get("edinburghZoo")).title("Edinburgh Zoo"));
        googleMap.addMarker(new MarkerOptions().position(stations.get("morningside")).title("Morningside"));
        googleMap.addMarker(new MarkerOptions().position(stations.get("liberton")).title("Liberton"));
        googleMap.addMarker(new MarkerOptions().position(stations.get("heriotwatt")).title("Heriot-Watt"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(stations.get("haymarket")));

        //Calculate the bounds of the markers

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(String key : stations.keySet()) {
            builder.include(stations.get(key));
        }

        LatLngBounds bounds = builder.build();

        //Obtain movement description object

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = 30;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        //Move the map
        googleMap.moveCamera(cu);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                AfterLogin.selectedMarker = marker;
                btnBook.setVisibility(View.VISIBLE);
                return false;
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
