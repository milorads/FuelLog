package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LastNFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    public static final String mypreference = "FirstRun";
    SharedPreferences sharedpreferences;
    DatabaseHandler database;

    public LastNFragment() {
        // Required empty public constructor
    }

    public static LastNFragment newInstance(String param1, String param2) {
        LastNFragment fragment = new LastNFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_last_n, container, false);

        //todo
        Initialize();

        return v;
    }

    private void Initialize(){
        TextView utrosak = (TextView)v.findViewById(R.id.stanjePotrosenoText2IP);
        TextView predjeno = (TextView)v.findViewById(R.id.stanjePredjenoText2IP);
        TextView average = (TextView)v.findViewById(R.id.customAvgText2);
        Intent intent = getActivity().getIntent();
        int period = intent.getIntExtra("spinner", 0);
        database = DatabaseHandler.getInstance(getActivity());
        long startMileage =0, date=0;
        sharedpreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        if(sharedpreferences.contains("Mileage")){
            startMileage = sharedpreferences.getLong("Mileage", 0);}
        if(sharedpreferences.contains("Date")){
            date = sharedpreferences.getLong("Date", 0);}
        Map<String, String> map = database.getConsumptionPerPeriod(period, startMileage, date);
        String predjenPut = map.get("put");
        String utrosakGoriva = map.get("gorivo");
        long predjenPutL = Long.parseLong(predjenPut);;
        long utrosakGorivaL = Long.parseLong(utrosakGoriva);;
        utrosak.setText(utrosakGoriva);
        predjeno.setText(predjenPut);
        long avg = 0;
        if(predjenPutL != 0){
            avg = (utrosakGorivaL*100)/predjenPutL;}
        average.setText(Long.toString(avg));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
