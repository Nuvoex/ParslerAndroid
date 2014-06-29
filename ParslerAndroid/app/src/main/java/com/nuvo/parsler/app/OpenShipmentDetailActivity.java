package com.nuvo.parsler.app;

import android.app.ProgressDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


public class OpenShipmentDetailActivity extends ActionBarActivity
        implements OpenShipmentAcceptDialogFragment.OpenShipmentDialogListener{

    private static final String TAG = "OpenShipmentDetail";
    JsonObject shipment;
    ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_shipment_detail);
        Gson gson = new Gson();
        shipment = gson.fromJson(getIntent().getStringExtra("shipment"), JsonObject.class);
        TextView pickup_address = (TextView) findViewById(R.id.pick_address_detail);
        pickup_address.setText(shipment.get("pickup_address").getAsString());
        TextView delivery_address = (TextView) findViewById(R.id.delivery_address_detail);
        delivery_address.setText(shipment.get("delivery_address").getAsString());
        pdialog = new ProgressDialog(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.open_shipment_detail, menu);
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

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.d(TAG, "In on dialog positive click");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Log.d(TAG, "In on dialog negative click");
    }

    public void onAccept(View view){
        OpenShipmentAcceptDialogFragment dialog = new OpenShipmentAcceptDialogFragment();
        dialog.show(getSupportFragmentManager(), "Accept");

    }

    public void onReject(View view){
        onAccept(view);
    }

    public void onMap(View view){
        Toast.makeText(this, "Coming soon ...", Toast.LENGTH_SHORT).show();
    }
}