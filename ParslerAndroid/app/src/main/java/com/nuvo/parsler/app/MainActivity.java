package com.nuvo.parsler.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.cookie.CookieMiddleware;
import com.nuvo.parsler.app.Classes.ParslerUrls;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = "Main Activity";
    private static MainActivity mcontext;
    public static Ion ion;
    public static URI server_uri;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);
        ImageView imageView = (ImageView) findViewById(R.id.logo_image);
        imageView.setImageResource(R.drawable.parsler_logo);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        try{
            server_uri = new URI(ParslerUrls.getBaseUrl());
        }catch(URISyntaxException e){
            server_uri = URI.create("http://ship.parsler.com");
            Log.e(TAG, "Could not specify server uri in Main Activity. " +
                    "Using default http://ship.parsler.com");
        }
        mcontext = this;
        ion = Ion.getDefault(this);
        CookieStore cookieStore = ion.getCookieMiddleware().getCookieStore();
        List<HttpCookie> cookie_list = cookieStore.get(server_uri);
        Boolean sessionIdFound = false;
        for(HttpCookie cookie: cookie_list){
            Log.d(TAG, cookie.getName());
            if (cookie.getName().equals("sessionid")){
                Log.d(TAG, "Cookie with name sessionid found");
                if ((cookie.getValue() != null || cookie.getValue() != "") & !cookie.hasExpired()){
                    sessionIdFound = true;
                    Log.d(TAG, "Sesssion Id found");
                }
            }
        }
        Handler myHandler = new Handler();
        Intent intent;
        if( !sessionIdFound) {
            Log.d(TAG, "Sesssion Id NOT found");
            intent = new Intent(this, LoginActivity.class);
        }
        else{
            intent = new Intent(this, HomePageActivity.class);
        }
        final Intent newIntent = intent;
        progressDialog.hide();
        myHandler.postDelayed(new Runnable() {
            //nothing
            @Override
            public void run() {
                startActivity(newIntent);
            }
        }, 2000);
    }

    public static MainActivity getUniversalContext(){
        if (mcontext == null){
            Log.v("Main", "Instance of MainActivity does not exist");
        }
        return mcontext;
    }

    public static Ion getIonInstance(){
        return ion;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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