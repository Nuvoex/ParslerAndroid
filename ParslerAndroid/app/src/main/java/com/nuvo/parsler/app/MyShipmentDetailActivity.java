package com.nuvo.parsler.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nuvo.parsler.app.Classes.JsonHelper;
import com.nuvo.parsler.app.Classes.UpdateStatusCodes;


public class MyShipmentDetailActivity extends ActionBarActivity {

    private static final String TAG = "MyShipmentDetail";
    JsonObject shipment;
    ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState");
        setContentView(R.layout.activity_my_shipment_detail);
        Gson gson = new Gson();
        if (savedInstanceState != null){
            shipment = gson.fromJson(savedInstanceState.getString("shipment"), JsonObject.class);
            Log.d(TAG, "saved instance not null");
        }
        else{
            shipment = gson.fromJson(getIntent().getStringExtra("shipment"), JsonObject.class);
        }
        TextView customer_name = (TextView) findViewById(R.id.customer_name);
        customer_name.setText(JsonHelper.getValueOrNone(shipment, "customer_name"));
        TextView phone1 = (TextView) findViewById(R.id.phone_1);
        phone1.setText(JsonHelper.getValueOrNone(shipment, "phone1"));
        TextView phone2 = (TextView) findViewById(R.id.phone_2);
        phone2.setText(JsonHelper.getValueOrNone(shipment, "phone2"));
        TextView pickup_address = (TextView) findViewById(R.id.pick_address_detail);
        pickup_address.setText(JsonHelper.getValueOrNone(shipment, "pickup_address"));
        TextView delivery_address = (TextView) findViewById(R.id.delivery_address_detail);
        delivery_address.setText(JsonHelper.getValueOrNone(shipment, "delivery_address"));
        TextView description = (TextView) findViewById(R.id.description_detail);
        description.setText(JsonHelper.getValueOrNone(shipment, "product_description"));
        TextView weight = (TextView) findViewById(R.id.weight);
        weight.setText(JsonHelper.getValueOrNone(shipment, "weight") + " grams");
        TextView quantity = (TextView) findViewById(R.id.quantity);
        quantity.setText(JsonHelper.getValueOrNone(shipment, "quantity"));
        TextView expected_amount = (TextView) findViewById(R.id.expected_amount);
        expected_amount.setText(JsonHelper.getValueOrNone(shipment, "expected_amount"));
        pdialog = new ProgressDialog(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_shipment_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void status_update(View view){
        int status_button = view.getId();
        int status_code = 0;
        switch(status_button){
            case R.id.pickup_completed_button:
                status_code = UpdateStatusCodes.STATUS_PICKUP_COMPLETED;
                break;
            case R.id.deferred_button:
                status_code = UpdateStatusCodes.STATUS_DEFERRED;
                break;
            case R.id.cancelled_button:
                status_code = UpdateStatusCodes.STATUS_CANCELLED;
                break;
            case R.id.customer_not_available_button:
                status_code = UpdateStatusCodes.STATUS_CUSTOMER_NA;
                break;
            default:
                status_code = 0;
                break;
        }
        Intent intent = new Intent(this, StatusUpdateActivity.class);
        intent.putExtra("status_code", status_code);
        intent.putExtra("shipment", shipment.toString());
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putString("shipment", shipment.toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}