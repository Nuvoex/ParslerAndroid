package com.nuvo.parsler.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nuvo.parsler.app.Classes.ErrorMessages;
import com.nuvo.parsler.app.Classes.JsonHelper;
import com.nuvo.parsler.app.Classes.ParslerUrls;
import com.nuvo.parsler.app.Classes.ResponseCodes;
import com.nuvo.parsler.app.Classes.UpdateStatusCodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class StatusUpdateActivity extends ActionBarActivity {
    private static final String TAG = "StatusUpdate";
    static final int REQUEST_INVOICE_IMAGE_CAPTURE = 1;
    static final int REQUEST_SHIPMENT_IMAGE_CAPTURE = 2;
    static final String EMPTY_STRING = "";
    JsonObject shipment;
    int status_code;
    ImageView invoice_image;
    String invoice_image_file_path;
    String shipment_image_file_path;
    ImageView shipment_image;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Gson gson = new Gson();
        if (savedInstanceState != null){
            status_code = savedInstanceState.getInt("status_code");
            shipment = gson.fromJson(savedInstanceState.getString("shipment"), JsonObject.class);
        }
        else{
            Intent intent = getIntent();
            status_code = intent.getExtras().getInt("status_code");
            shipment = gson.fromJson(intent.getStringExtra("shipment"), JsonObject.class);
        }
        invoice_image_file_path = EMPTY_STRING;
        shipment_image_file_path = EMPTY_STRING;
        switch (status_code){
            case UpdateStatusCodes.STATUS_PICKUP_COMPLETED:
                setContentView(R.layout.pickup_completed);
                invoice_image = (ImageView) findViewById(R.id.invoice_image);
                shipment_image = (ImageView) findViewById(R.id.shipment_image);
                progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                break;
            default:
                setContentView(R.layout.pickup_completed);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.status_update, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putInt("status_code", status_code);
        outState.putString("shipment", shipment.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void takePictureIntent(View view) {
        int id = view.getId();
        int request_id;
        switch(id){
            case R.id.cam_shipment:
                request_id = REQUEST_SHIPMENT_IMAGE_CAPTURE;
                Log.d(TAG, "Shipment image requested");
                break;
            case R.id.cam_invoice:
                request_id = REQUEST_INVOICE_IMAGE_CAPTURE;
                Log.d(TAG, "Invoice image requested");
                break;
            default:
                Log.e(TAG, "Some error with button ids");
                request_id = REQUEST_SHIPMENT_IMAGE_CAPTURE;
                break;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(request_id);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "Error occurred whil creating file");
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, request_id);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap;
            switch (requestCode){
                case REQUEST_INVOICE_IMAGE_CAPTURE:
                    bitmap = BitmapFactory.decodeFile(invoice_image_file_path, options);
                    invoice_image.setImageBitmap(bitmap);
                    Log.d(TAG, "Setting Invoice Image");
                    break;
                case REQUEST_SHIPMENT_IMAGE_CAPTURE:
                    Log.d(TAG, "Setting Shipment Image");
                    bitmap = BitmapFactory.decodeFile(shipment_image_file_path, options);
                    shipment_image.setImageBitmap(bitmap);
                    break;
            }
        }
    }

    private File createImageFile(int request_code) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        switch(request_code){
            case REQUEST_SHIPMENT_IMAGE_CAPTURE:
                shipment_image_file_path = image.getAbsolutePath();
                Log.d(TAG, shipment_image_file_path);
                break;
            case REQUEST_INVOICE_IMAGE_CAPTURE:
                invoice_image_file_path = image.getAbsolutePath();
                Log.d(TAG, invoice_image_file_path);
                break;
        }
        Log.d(TAG, image.getAbsolutePath());
        return image;
    }

    public void status_submit(View view){
        int view_id = view.getId();
        switch (view_id){
            case R.id.pickup_completed_submit:
                mark_pickup_complete();
                break;
        }
    }

    private void mark_pickup_complete(){
        if((shipment_image_file_path.equals(EMPTY_STRING)) | (invoice_image_file_path.equals(EMPTY_STRING))){
            Toast.makeText(this, "Please click both the images", Toast.LENGTH_LONG).show();
            return;
        }
        else{
            Ion.with(getApplicationContext(), ParslerUrls.getAcceptRejectUpdateUrl(JsonHelper.getValueOrNone(shipment, "id")))
                    .uploadProgressBar(progressBar)
                    .setMultipartParameter("action", "PCL")
                    .setMultipartFile("shipment_image", new File(shipment_image_file_path))
                    .setMultipartFile("invoice_image", new File(invoice_image_file_path))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            updateCallBack(e, result);
                        }
                    });

        }
    }

    private void updateCallBack(Exception e, JsonObject result){
        if (result == null){
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
                Toast.makeText(getApplicationContext(), ErrorMessages.Update_Successful, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                startActivity(intent);
                break;
            }
            case ResponseCodes.CODE_404:
            {
                Log.d(TAG, "In response code 404");
                Toast.makeText(getApplicationContext(), ErrorMessages.Error_404, Toast.LENGTH_LONG).show();
                break;
            }
            case ResponseCodes.CODE_500:
            {
                Log.d(TAG, "In response code 500. Internal server error");
                Toast.makeText(getApplicationContext(), ErrorMessages.Error_500, Toast.LENGTH_LONG).show();
                break;
            }
            case ResponseCodes.CODE_403:
            {
                Log.d(TAG, "In response code 403");
                Toast.makeText(getApplicationContext(), ErrorMessages.Error_403, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            }
            case ResponseCodes.CODE_ERROR:
            {
                Log.d(TAG, "In response code error");
                Toast.makeText(getApplicationContext(), ErrorMessages.Error_Unexpected, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }
}