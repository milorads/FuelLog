package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by msekulovic on 1/23/2017.
 */

public class DatabaseModelAdapter extends ArrayAdapter<DatabaseModel> {

    public DatabaseModelAdapter(Context context, ArrayList<DatabaseModel> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DatabaseModel model = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item, parent, false);
        }
        // Lookup view for data population
        TextView dmID = (TextView) convertView.findViewById(R.id.itemID);
        TextView dmTPL = (TextView) convertView.findViewById(R.id.itemTotalPerLiter);
        TextView dmDate = (TextView) convertView.findViewById(R.id.itemDate);
        TextView dmCDop = (TextView) convertView.findViewById(R.id.itemRefillPrice);
        TextView dmKM = (TextView) convertView.findViewById(R.id.itemKM);
        TextView dmLit = (TextView) convertView.findViewById(R.id.itemLiters);
        // Populate the data into the template view using the data object
        dmID.setText("Identifier: "+Integer.toString(model.get_id()));
        dmTPL.setText(model.get_tpl());
        Date date = new Date(model.get_date());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        dmDate.setText("Date of input: "+formatter.format(date.getTime()));
        dmCDop.setText("Price: "+Float.toString(model.get_cdop()));
        dmKM.setText("Mileage covered: "+Long.toString(model.get_km()));
        dmLit.setText("Liters added: "+Long.toString(model.get_lit()));
        // Return the completed view to render on screen
        return convertView;
    }
}