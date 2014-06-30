package com.nuvo.parsler.app.Classes;

/**
 * Created by subodh on 29/6/14.
 */
public class ParslerUrls {
    private static final String BASE_URL = "http://ship.parsler.com";
//    public static final String BASE_URL = "http://192.168.1.90:8000";
    private static final String SHIPMENTS_URL = "/awb_list/";
    private static final String MOBILE_URL = "/mobile";
    private static final String MEDIA_URL = "/media/";
    private static final String LOGIN_URL = "/login/";
    private static final String ACCEPT_REJECT_UPDATE_URL = "/awb/";

    public static String getShipmentsUrl(Boolean open){
        if(open) return BASE_URL + MOBILE_URL + SHIPMENTS_URL;
        else return BASE_URL + MOBILE_URL + SHIPMENTS_URL + "?awb_list_type=accepted";
    }

    public static String getAcceptRejectUpdateUrl(String id){
        return BASE_URL + MOBILE_URL + ACCEPT_REJECT_UPDATE_URL + id + "/actions/";
    }

    public static String getBaseUrl(){
        return BASE_URL;
    }

    public static String getLoginUrl(){
        return BASE_URL + MOBILE_URL + LOGIN_URL;
    }
}
