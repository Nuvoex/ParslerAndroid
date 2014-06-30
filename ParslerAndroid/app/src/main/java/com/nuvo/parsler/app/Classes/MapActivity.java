package com.nuvo.parsler.app.Classes;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nuvo.parsler.app.R;

public class MapActivity extends ActionBarActivity {
    static final LatLng DELHI = new LatLng(28.47, 77.03);//77.03, 28.47);
    static final LatLng GURGAON = new LatLng(28.38, 77.12);//77.12, 28.38);
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        // check if map is created successfully or not
        if (googleMap == null) {
            Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        }

        if (googleMap!=null){
            googleMap.addMarker(new MarkerOptions().position(DELHI)
                    .title("Pickup Address")
                    .snippet("M-18 Arya Group Of Companies Near Radha Krishna Mandir, Old Dlf Colony Sector-14, Ganpati Honda, Gurgaon")).showInfoWindow();
            googleMap.addMarker(new MarkerOptions()
                    .position(GURGAON)
                    .title("Delivery Address")
                    .snippet("House No. 288 Sector 10/A Gurgaon Haryana Near Huda Market, Hero Honda Chowk, Gurgaon"));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DELHI, 10));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
