package com.nuvo.parsler.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Created by subodh on 27/6/14.
 */
public class HomePageActivity extends ActionBarActivity {

    private static final String TAG = "HomePageActivity";
    //    public static final String BASE_URL = "http://192.168.1.91:8000";
    public static final String BASE_URL = "http://ship.parsler.com";
    public static final String SHIPMENTS_URL = "/mobile/awb_list/";
    public static final String MEDIA_URL = "/media/";
    public static final String [] Titles = {"My Shipments", "Open Orders"};
    public static int TABS;
    TabAdapter mAdapter;
    ViewPager mPager;
    TabPageIndicator tabPageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mPager = (ViewPager) findViewById(R.id.home_pager);
        TABS = Titles.length;
        mAdapter = new TabAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        tabPageIndicator = (TabPageIndicator) findViewById(R.id.home_tab_strip);
        tabPageIndicator.setViewPager(mPager,0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class TabAdapter extends FragmentPagerAdapter {
        public TabAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return TABS;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "In TabAdapter.getItem");
            Log.d(TAG, "" + position);
            Fragment fragment = new TabFragment();
            Bundle args = new Bundle();
            args.putInt(TabFragment.PAGE, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Titles[position];
        }
    }

    public static class TabFragment extends Fragment {

        public static final String PAGE = "page";
        public static final String [] Titles = {"Categories", "Offers", "New"};
        String fragment_url;
        CustomListAdapter listAdapter;
        ProgressDialog pDialog;

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            View rootView;
            HomePageActivity homeActivity = (HomePageActivity) this.getActivity();
            Bundle args = getArguments();
            pDialog = new ProgressDialog(getActivity());
            pDialog.show();
            int page = args.getInt(PAGE);
            rootView = inflater.inflate(R.layout.list_layout, container, false);
            final ListView listView = (ListView) rootView.findViewById(R.id.listview);
            listAdapter = new CustomListAdapter(homeActivity);
            getShipments(BASE_URL + SHIPMENTS_URL, listAdapter, page);
            listView.setAdapter(listAdapter);
            return rootView;
        }

        private void getShipments(String url, final CustomListAdapter listAdapter, int page) {
            final Context context = getActivity();
            Log.d(TAG, "In TabFragment.getShipments");
            switch(page){
                case 1:
                    url = url + "?awb_list_type=accepted";
            }
            Log.d(TAG, url);
            Ion.with(context)
                    .load(url)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            Log.d(TAG, "In onCompleted");
                            pDialog.hide();
                            if (e != null) {
                                Toast.makeText(context, "Error loading Shipments. Try Again", Toast.LENGTH_LONG).show();
                                return;
                            }
                            else{
                                int response_code = LoginActivity.getResponseCode(result.get("http_status").getAsString());
                                switch (response_code){
                                    case LoginActivity.RESPONSE_CODE_200:
                                    {
                                        if (result.get("no_shipments").getAsBoolean()){
                                            addNone();
                                            return;
                                        }
                                        else {
                                            addShipments(result.get("awbs").getAsJsonArray());
                                        }
                                        break;
                                    }
                                    case LoginActivity.RESPONSE_CODE_403:
                                    {
                                        Toast.makeText(getActivity(), ErrorMessages.Error_403, Toast.LENGTH_LONG).show();
                                        redirect_to_login();
                                        break;
                                    }
                                    case LoginActivity.RESPONSE_CODE_500:
                                    {
                                        Toast.makeText(getActivity(), ErrorMessages.Error_500, Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    case LoginActivity.RESPONSE_CODE_404:
                                    {
                                        Toast.makeText(getActivity(), ErrorMessages.Error_404, Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    case LoginActivity.RESPONSE_CODE_ERROR:
                                    {
                                        Toast.makeText(getActivity(), ErrorMessages.Error_Unexpected, Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                }
                            }
                        }
                    });
        }

        private void addShipments(JsonArray shipments){
            Log.d(TAG, "In addShipments");
            if (shipments.size() == 0){
                Log.d(TAG, "Shipments size zero");
                addNone();
                return;
            }
            listAdapter.clear();
            for(int i = 0; i< shipments.size(); i++){
                listAdapter.add(shipments.get(i).getAsJsonObject());
            }
            listAdapter.notifyDataSetChanged();
        }

        @Override
        public void onPause() {
            super.onPause();
            pDialog.hide();
        }

        @Override
        public void onStop() {
            super.onStop();
            pDialog.hide();
        }

        private void redirect_to_login(){
            Log.d(TAG, "In redirect_to_login");
        }

        private void addNone(){
            Log.d(TAG, "In addNone");
            listAdapter.clear();
            listAdapter.notifyDataSetChanged();
        }
    }
}