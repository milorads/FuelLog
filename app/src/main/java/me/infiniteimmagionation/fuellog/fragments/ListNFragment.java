package me.infiniteimmagionation.fuellog.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import me.infiniteimmagionation.fuellog.DatabaseHandler;
import me.infiniteimmagionation.fuellog.R;

public class ListNFragment extends Fragment {

    public Context context;
    SharedPreferences sharedPreferences;
    DatabaseHandler database;
    public static final String mypreference = "FirstRun";
    View v;
    int period;
    private OnFragmentInteractionListener mListener;

    public ListNFragment() {
        // Required empty public constructor
    }

    public static ListNFragment newInstance() {
        ListNFragment fragment = new ListNFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_last_n, container, false);
        TextView utrosak = (TextView)v.findViewById(R.id.stanjePotrosenoText2IP);
        TextView predjeno = (TextView)v.findViewById(R.id.stanjePredjenoText2IP);
        TextView average = (TextView)v.findViewById(R.id.customAvgText2);
//        Intent intent = getIntent();
//        int period = intent.getIntExtra("spinner", 0);
        database = DatabaseHandler.getInstance(context);
        long startMileage =0, date=0;
        sharedPreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        if(sharedPreferences.contains("Mileage")){
            startMileage = sharedPreferences.getLong("Mileage", 0);}
        if(sharedPreferences.contains("Date")){
            date = sharedPreferences.getLong("Date", 0);}
        Map<String, String> map = database.getConsumptionPerPeriod(period, startMileage, date);
        String predjenPut = (String) map.get("put");
        String utrosakGoriva = (String) map.get("gorivo");
        long predjenPutL = Long.parseLong(predjenPut);;
        long utrosakGorivaL = Long.parseLong(utrosakGoriva);;
        utrosak.setText(utrosakGoriva);
        predjeno.setText(predjenPut);
        long avg = (utrosakGorivaL*100)/predjenPutL;
        average.setText(Long.toString(avg));
        return v;
    }

    public void odabirSpinnera(int i)
    {
        period = i;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

    }
}
