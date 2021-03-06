package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;


public class RightActivity extends Fragment implements View.OnClickListener{

    private DatabaseHandler db = DatabaseHandler.getInstance(getActivity());
    SharedPreferences sharedpreferences;
    public static final String mypreference = "FirstRun";
    private DatabaseHandler database;
    private OnFragmentInteractionListener mListener;

    public RightActivity() {

    }

    public static RightActivity newInstance(String param1, String param2) {
        RightActivity fragment = new RightActivity();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View classView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        classView = inflater.inflate(R.layout.fragment_right, container, false);

        Initialize();

        return classView;
    }

    void Initialize(){
        RotationHandler.getInstance();
        RotationHandler.setClass(RotationHandler.classOption.Add);


        sharedpreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        Button saveDugme = (Button)classView.findViewById(R.id.saveAddButton);
        saveDugme.setOnClickListener(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        long mlg =0;
        int fuel =0;
        float price = 0;
        String tpl = "";
        try {
            EditText mi = (EditText) classView.findViewById(R.id.mileageText);
            EditText pr = (EditText) classView.findViewById(R.id.priceText);
            EditText fl = (EditText) classView.findViewById(R.id.fuelText);
            Spinner sp = (Spinner) classView.findViewById(R.id.perLiterTotal);
            mlg = Integer.parseInt(mi.getText().toString());
            fuel = Integer.parseInt(fl.getText().toString());
            price = Float.parseFloat(pr.getText().toString());
            tpl = sp.getSelectedItem().toString();
        }
        catch (Exception e){

        }
        if(tpl != null){
            mListener.addData(mlg, fuel, price, tpl);}
        else{
            mListener.addData(mlg, fuel, price, "");
        }
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void addData(long mileage, int fuel, float price, String totalOrPerLiter);
    }

    @Override
    public void onClick(View temp) {
        if(temp.getId()==R.id.saveAddButton)
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
            EditText mi = (EditText)classView.findViewById(R.id.mileageText);
            EditText pr = (EditText)classView.findViewById(R.id.priceText);
            EditText fl = (EditText)classView.findViewById(R.id.fuelText);
            Spinner sp = (Spinner)classView.findViewById(R.id.perLiterTotal);
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
                new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.warn))
                        .setMessage(getResources().getString(R.string.fuel_mileage_warning))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        }).show();
            }
            if(!fail)
            {
                long mil =0;
                sharedpreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                if(sharedpreferences.contains("Mileage")){
                    mil = sharedpreferences.getLong("Mileage", 0);}
                DatabaseModel model = new DatabaseModel(tpl, System.currentTimeMillis(), price, mileage, fuel);
                if(db.addRefill(model, mil)){
                    // pravljenje report-a
                    WriteReport(model, fuel, fuelPrev, startMileage, startDate);
                    // vracanje u main
                    clearText(mi);clearText(pr);clearText(fl);
                    Toast.makeText(getActivity(), getResources().getString(R.string.succ), Toast.LENGTH_SHORT).show();
                    //getActivity().finish();
                }
                else{
                    Toast.makeText(getActivity(), getResources().getString(R.string.mileage_higher), Toast.LENGTH_SHORT).show();
                    //getActivity().finish();
                }
            }
        }
    }

    private void clearText(TextView txt){
        txt.setText("0");
    }

    private void WriteReport(DatabaseModel model, int fuel, int prevFuel, long startMileage, long startDate)
    {
        // write new fuel state to sharedprefs
        sharedpreferences.edit().putInt("Fuel", fuel).apply();
        String FILENAME = getResources().getString(R.string.report1)+Long.toString(model.get_date());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(model.get_date()));
        String textmsg = getResources().getString(R.string.report2) + dateString + "\n\n";
        long range = model.get_km() - startMileage;
        long pastTime = model.get_date() - startDate;
        long days = TimeUnit.MILLISECONDS.toDays(pastTime);
        database = DatabaseHandler.getInstance(getActivity());
        List<DatabaseModel> lista = database.getAllRefills();
        long utrosenoGorivo = 0;
        for (DatabaseModel m:lista) {

            utrosenoGorivo += m.get_lit();
        }
        final long avg = (utrosenoGorivo*100)/range;
        textmsg+=getResources().getString(R.string.report3) + days + getResources().getString(R.string.report4)+range+getResources().getString(R.string.report5)+"\n";
        textmsg+=getResources().getString(R.string.report6)+ Long.toString(avg);

        try {
            FileOutputStream fileout=getActivity().openFileOutput(FILENAME+".txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write(textmsg);
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
