package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainLandscapeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainLandscapeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainLandscapeFragment extends Fragment implements View.OnClickListener{

    SharedPreferences sharedpreferences;
    public static final String mypreference = "FirstRun";
    public DatabaseHandler database;
    private OnFragmentInteractionListener mListener;
    View v;

    public MainLandscapeFragment() {
        // Required empty public constructor
    }


    public static MainLandscapeFragment newInstance(String param1, String param2) {
        MainLandscapeFragment fragment = new MainLandscapeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_main, container, false);

        sharedpreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        database = DatabaseHandler.getInstance(getActivity());
        if(sharedpreferences.contains("YesNo"))
        {
            if (sharedpreferences.getBoolean("YesNo", true))
            {
                    InitializeItems();
                    initFab();
                    return v;
            }
            else
            {
                startRegisterIntent();
            }
        }
        else
        {
            startRegisterIntent();
        }

        return v;
    }

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

    private void startRegisterIntent(){
        Intent intent = new Intent(getActivity(), RegisterActivity.class);
        startActivity(intent);
    }

    private void initFab()
    {
        FloatingActionButton myFab = (FloatingActionButton) v.findViewById(R.id.fab);
        final Intent intent = new Intent(getActivity(), AddActivity.class);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        FloatingActionButton myFab2 = (FloatingActionButton) v.findViewById(R.id.fab2);
        final Intent intent2 = new Intent(getActivity(), EditActivity.class);
        myFab2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(intent2);
            }
        });
    }

    private void InitializeItems()
    {
        Button prikazButton = (Button) v.findViewById(R.id.prikazButton);
        prikazButton.setOnClickListener(this);
        TextView stanjeT1 = (TextView)v.findViewById(R.id.stanjeDanText2);
        TextView stanjeT2 = (TextView)v.findViewById(R.id.stanjePredjenoText2);
        TextView stanjeT3 = (TextView)v.findViewById(R.id.stanjePotrosenoText2);
        TextView stanjeT4 = (TextView)v.findViewById(R.id.averageText2);
        String stanjeNaDan = getTodayDate();
        stanjeT1.setText(stanjeNaDan);

        long startMileage = 0;
        if(sharedpreferences.contains("Mileage")){
            startMileage = sharedpreferences.getLong("Mileage", 0);}
        DatabaseModel model = database.getLastMileage();
        long predjenPut = 0;
        if (model!=null)
            predjenPut = model.get_km() -startMileage;
        stanjeT2.setText(predjenPut + " km");

        long utrosenoGorivo = 0;
        List<DatabaseModel> lista = database.getAllRefills();
        for (DatabaseModel m:lista) {

            utrosenoGorivo += m.get_lit();
        }
        stanjeT3.setText(Long.toString(utrosenoGorivo));

        final long avg = (utrosenoGorivo*100)/predjenPut;
        stanjeT4.setText(Long.toString(avg));
    }


    private static String getTodayDate()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public void onClick(View v)
    {
        if(v.getId()==R.id.prikazButton)
        {
            startLastNIntent();
        }
    }

    private void startLastNIntent(){
        Intent intent = new Intent(getActivity(), LastNActivity.class);
        Spinner s = (Spinner)v.findViewById(R.id.previousLookup);
        intent.putExtra("spinner", checkSpinner(s));
        startActivity(intent);
    }

    private int checkSpinner(Spinner s)
    {
        String text = s.getSelectedItem().toString();
        int broj = 0;
        switch (text) {
            case "7 Days":
                broj= 7;
                break;
            case "1 Month":
                broj= 30;
                break;
            case "2 Months":
                broj= 60;
                break;
            case "6 Months":
                broj= 180;
                break;
            case "12 Months":
                broj= 365;
                break;
        }
        return broj;
    }
}
