package com.nuvo.parsler.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.nuvo.parsler.app.Classes.ParslerUrls;

/**
 * Created by subodh on 27/6/14.
 */
public class CustomListAdapter extends ArrayAdapter<JsonObject> {
    private Context context;
    public static final String BASE_URL = ParslerUrls.getBaseUrl();
    static String awb_number;
    static String current_status;
    static JsonObject current_item;
    static String client_name;

    public CustomListAdapter(Context context){
        super(context, 0);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CustomGridAdapterViewHolder holder;
        current_item = getItem(position);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.list_item, null);

            holder = new CustomGridAdapterViewHolder();

            holder.awb_number = (TextView) convertView.findViewById(R.id.awb_number);
            holder.current_status = (TextView) convertView.findViewById(R.id.current_status);
            holder.client_name = (TextView) convertView.findViewById(R.id.client_name);

            convertView.setTag(holder);
        }
        else{
            holder = (CustomGridAdapterViewHolder) convertView.getTag();
        }

        awb_number =  current_item.get("awb").getAsString();
        current_status = current_item.get("status").getAsString();
        client_name = current_item.get("category").getAsString();

        holder.awb_number.setText(awb_number);
        holder.current_status.setText(current_status);
        holder.client_name.setText(client_name);

        return convertView;
    }

    private class CustomGridAdapterViewHolder{
        TextView awb_number;
        TextView current_status;
        TextView category;
        TextView client_name;
        int position;
    }
}
