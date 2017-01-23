package me.infiniteimmagionation.fuellog.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.infiniteimmagionation.fuellog.AddActivity;
import me.infiniteimmagionation.fuellog.DatabaseHandler;
import me.infiniteimmagionation.fuellog.DatabaseModel;
import me.infiniteimmagionation.fuellog.R;


public class AddFragment extends Fragment implements View.OnClickListener{

    SharedPreferences sharedpreferences;
    private OnFragmentInteractionListener mListener;
    public static final String mypreference = "FirstRun";
    View v;
    public Context context;

    public AddFragment() {
        // Required empty public constructor
    }

    public static AddFragment newInstance() {
        AddFragment fragment = new AddFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_add, container, false);
        sharedpreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        Button saveDugme = (Button)v.findViewById(R.id.saveAddButton);
        saveDugme.setOnClickListener(this);
        return v;
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.saveAddButton)
        {
            boolean fail;
            // get status trenutni iz shared i interne memorije
            // uporedi, napravi report i vrati u view main
            long mileage =0;
            int fuel =0;
            int fuelPrev = 0;
            float price=0;
            long startMileage = 0;
            long startDate = 0;
            if(sharedpreferences.contains("Fuel")){
                fuelPrev = sharedpreferences.getInt("Fuel", 0);}
            if(sharedpreferences.contains("Mileage")){
                startMileage = sharedpreferences.getLong("Mileage", 0);}
            if(sharedpreferences.contains("Date")){
                startDate = sharedpreferences.getLong("Date", 0);}
            /**
             * CRUD Operations
             * */
            EditText mi = (EditText)v.findViewById(R.id.mileageText);
            EditText pr = (EditText)v.findViewById(R.id.priceText);
            EditText fl = (EditText)v.findViewById(R.id.fuelText);
            Spinner sp = (Spinner)v.findViewById(R.id.perLiterTotal);
            String tpl = sp.getSelectedItem().toString();
            try
            {
                fail = false;
                mileage = Integer.parseInt(mi.getText().toString());
                fuel = Integer.parseInt(fl.getText().toString());
                price = Integer.parseInt(pr.getText().toString());
            }
            catch (Exception e)
            {
                fail = true;
                new AlertDialog.Builder(context)
                        .setTitle("Warning")
                        .setMessage("Mileage/Fuel/Price must be a number and can not be empty")
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
                            }
                        }).show();
            }
            if(!fail)
            {
                DatabaseModel model = new DatabaseModel(tpl, System.currentTimeMillis(), price, mileage, fuel);
                database.addRefill(model);

                // pravljenje report-a
                WriteReport(model, fuel, fuelPrev, startMileage, startDate);
                // vracanje u main
//                finish();
            }
        }
    }

    private DatabaseHandler database;

    private void WriteReport(DatabaseModel model, int fuel, int prevFuel, long startMileage, long startDate)
    {
        // write new fuel state to sharedprefs
        sharedpreferences.edit().putInt("Fuel", fuel).apply();
        String FILENAME = "Report"+Long.toString(model.get_date());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(model.get_date()));
        String textmsg = "Report for " + dateString + "\n\n";
        long range = model.get_km() - startMileage;
        long pastTime = model.get_date() - startDate;
        long days = TimeUnit.MILLISECONDS.toDays(pastTime);
        database = DatabaseHandler.getInstance(context);
        List<DatabaseModel> lista = database.getAllRefills();
        long utrosenoGorivo = 0;
        for (DatabaseModel m:lista) {

            utrosenoGorivo += m.get_lit();
        }
        final long avg = (utrosenoGorivo*100)/range;
        textmsg+="For the period of " + days + ", the range of: "+range+" was covered.\n";
        textmsg+="Average consumption was: "+ Long.toString(avg);

        try {
            FileOutputStream fileout=context.openFileOutput(FILENAME+".txt", context.MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write(textmsg);
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
