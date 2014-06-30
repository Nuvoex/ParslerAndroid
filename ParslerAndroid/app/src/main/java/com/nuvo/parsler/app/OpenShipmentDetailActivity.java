package com.nuvo.parsler.app;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nuvo.parsler.app.Classes.ErrorMessages;
import com.nuvo.parsler.app.Classes.MapActivity;
import com.nuvo.parsler.app.Classes.ParslerUrls;
import com.nuvo.parsler.app.Classes.ResponseCodes;


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
        pickup_address.setText(getValueOrNone(shipment, "pickup_address"));
        TextView delivery_address = (TextView) findViewById(R.id.delivery_address_detail);
        delivery_address.setText(getValueOrNone(shipment, "delivery_address"));
        TextView description = (TextView) findViewById(R.id.description_detail);
        description.setText(getValueOrNone(shipment, "product_description"));
        TextView weight = (TextView) findViewById(R.id.weightDetail);
        weight.setText(getValueOrNone(shipment, "weight") + " grams");
        TextView quantity = (TextView) findViewById(R.id.quantity);
        quantity.setText(getValueOrNone(shipment, "quantity"));
        TextView expected_amount = (TextView) findViewById(R.id.expected_amount);
        expected_amount.setText(getValueOrNone(shipment, "expected_amount"));
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
    public void onDialogAcceptClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.d(TAG, "In on dialog accept click");
        Log.d(TAG, shipment.get("awb").getAsString());
        update_status("accept", shipment.get("id").getAsString());
    }

    @Override
    public void onDialogRejectClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.d(TAG, "In on dialog reject click");
        Log.d(TAG, shipment.get("awb").getAsString());
        update_status("decline", shipment.get("id").getAsString());
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Log.d(TAG, "In on dialog negative click");
    }

    public void onAccept(View view){
        OpenShipmentAcceptDialogFragment dialog = new OpenShipmentAcceptDialogFragment();
        Bundle args = new Bundle();
        args.putString("action", "accept");
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "Accept");
    }

    public void onReject(View view){
        OpenShipmentAcceptDialogFragment dialog = new OpenShipmentAcceptDialogFragment();
        Bundle args = new Bundle();
        args.putString("action", "reject");
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "Reject");
    }

    public void onMap(View view){
//        Toast.makeText(this, "Coming soon ...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    private String getValueOrNone(JsonObject jsonObject, String key){
        try{
            return jsonObject.get(key).getAsString();
        }catch(Exception e){
            Log.d(TAG, e.toString());
            return "NA";
        }
    }

    public void update_status(String action, String id){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        Ion.with(this)
                .load(ParslerUrls.getAcceptRejectUpdateUrl(id))
                .setBodyParameter("action", action)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result == null){
                            progressDialog.hide();
                            Log.d(TAG, "Response Json is null. Please check server response");
                            Toast.makeText(getApplicationContext(), "Response Json is null. Please check server response", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.d(TAG, result.get("http_status").getAsString());
                        Log.d(TAG, result.toString());
                        int response_code = ResponseCodes.getResponseCode(result.get("http_status").getAsString());
                        switch (response_code){
                            case ResponseCodes.CODE_200:
                            {
                                Log.d(TAG, "In response code 200");
                                progressDialog.hide();
                                Toast.makeText(getApplicationContext(), ErrorMessages.Update_Successful, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                                startActivity(intent);
                                break;
                            }
                            case ResponseCodes.CODE_404:
                            {
                                Log.d(TAG, "In response code 404");
                                progressDialog.hide();
                                Toast.makeText(getApplicationContext(), ErrorMessages.Error_404, Toast.LENGTH_LONG).show();
                                break;
                            }
                            case ResponseCodes.CODE_500:
                            {
                                Log.d(TAG, "In response code 500. Internal server error");
                                progressDialog.hide();
                                Toast.makeText(getApplicationContext(), ErrorMessages.Error_500, Toast.LENGTH_LONG).show();
                                break;
                            }
                            case ResponseCodes.CODE_403:
                            {
                                Log.d(TAG, "In response code 403");
                                progressDialog.hide();
                                Toast.makeText(getApplicationContext(), ErrorMessages.Error_403, Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case ResponseCodes.CODE_ERROR:
                            {
                                Log.d(TAG, "In response code error");
                                progressDialog.hide();
                                Toast.makeText(getApplicationContext(), ErrorMessages.Error_Unexpected, Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                    }
                });

    }
}