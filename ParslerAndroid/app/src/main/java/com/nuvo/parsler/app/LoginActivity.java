package com.nuvo.parsler.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nuvo.parsler.app.Classes.ErrorMessages;
import com.nuvo.parsler.app.Classes.ParslerUrls;


public class LoginActivity extends ActionBarActivity {

    private static final String LOGIN_URL = "/mobile/login/";
    private static final String TAG = "LoginActivity";
    public static final int RESPONSE_CODE_200 = 0;
    public static final int RESPONSE_CODE_403 = 1;
    public static final int RESPONSE_CODE_404 = 2;
    public static final int RESPONSE_CODE_500 = 3;
    public static final int RESPONSE_CODE_ERROR = 4;
    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        final TextView username = (TextView) findViewById(R.id.username);
        final TextView password = (TextView) findViewById(R.id.password);
        final Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        login(username.getText().toString(), password.getText().toString());
                    }
                }
        );
    }

    private void login(String username, String password){
        if((username == null | username == "") | (password == null | password == "")){
            Log.d(TAG, "Null username or password");
            Toast.makeText(this, "Please enter a username and password", Toast.LENGTH_LONG).show();
            return;
        }
        else{
            progressDialog.show();
            JsonObject post_parameters = new JsonObject();
            post_parameters.addProperty("username", username);
            post_parameters.addProperty("password", password);
            Ion.with(this)
                    .load(ParslerUrls.getLoginUrl())
                    .setBodyParameter("username", username)
                    .setBodyParameter("password", password)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result == null){
                                progressDialog.hide();
                                Log.d(TAG, "Response Json is null. Please check server response");
                                Log.d(TAG, ParslerUrls.getLoginUrl());
                                Toast.makeText(getApplicationContext(), "Response Json is null. Please check server response", Toast.LENGTH_LONG).show();
                                return;
                            }
                            Log.d(TAG, result.get("http_status").getAsString());
                            Log.d(TAG, result.toString());
                            int response_code = getResponseCode(result.get("http_status").getAsString());
                            String response_message;
                            if (result.has("message")){
                                response_message = result.get("message").getAsString();
                            }
                            else if (result.has("msg")){
                                response_message = result.get("msg").getAsString();
                            }
                            else if (result.has("sessionid")){
                                response_message = "Login Succesful";
                            }
                            else{
                                response_message = "Error";
                            }
                            switch (response_code){
                                case RESPONSE_CODE_200:
                                {
                                    Log.d(TAG, "In response code 200");
                                    if (response_message.equals("Login Succesful")){
                                        progressDialog.hide();
                                        Toast.makeText(getApplicationContext(), ErrorMessages.Login_Successful, Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        progressDialog.hide();
                                        Toast.makeText(getApplicationContext(), ErrorMessages.Error_Invalid_User, Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                }
                                case RESPONSE_CODE_404:
                                {
                                    Log.d(TAG, "In response code 404");
                                    progressDialog.hide();
                                    Toast.makeText(getApplicationContext(), ErrorMessages.Error_404, Toast.LENGTH_LONG).show();
                                    break;
                                }
                                case RESPONSE_CODE_500:
                                {
                                    Log.d(TAG, "In response code 500. Internal server error");
                                    progressDialog.hide();
                                    Toast.makeText(getApplicationContext(), ErrorMessages.Error_500, Toast.LENGTH_LONG).show();
                                    break;
                                }
                                case RESPONSE_CODE_403:
                                {
                                    Log.d(TAG, "In response code 403");
                                    progressDialog.hide();
                                    Toast.makeText(getApplicationContext(), response_message, Toast.LENGTH_LONG).show();
                                    break;
                                }
                                case RESPONSE_CODE_ERROR:
                                {
                                    Log.d(TAG, "In response code error");
                                    progressDialog.hide();
                                    Toast.makeText(getApplicationContext(), response_message, Toast.LENGTH_LONG).show();
                                    break;
                                }
                            }
                        }
                    });
        }
    }

    public static int getResponseCode(String code){
        if (code.equals("200")) {
            return RESPONSE_CODE_200;
        }
        else if (code.equals("404")){
            return RESPONSE_CODE_404;
        }
        else if (code.equals("403")){
            return RESPONSE_CODE_403;
        }
        else if (code.equals("500")){
            return RESPONSE_CODE_500;
        }
        else {
            Log.e(TAG, "Response Code error. Something went wrong with the response codes");
            Log.d(TAG, code);
            return RESPONSE_CODE_ERROR;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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
