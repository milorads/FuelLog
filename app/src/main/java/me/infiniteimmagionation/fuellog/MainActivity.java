package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import me.infiniteimmagionation.fuellog.RotationHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, EditFragment.OnFragmentInteractionListener{//, AddFragment.OnFragmentInteractionListener, EditFragment.OnFragmentInteractionListener, ListNFragment.OnFragmentInteractionListener {

    SharedPreferences sharedpreferences;
    public static final String mypreference = "FirstRun";
    public DatabaseHandler database;

    @Override
    protected void onResume()
    {
        super.onResume();
        InitializeItems();
        initFab();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mainContext = this;
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        database = DatabaseHandler.getInstance(this);
        if(sharedpreferences.contains("YesNo"))
        {
            if (sharedpreferences.getBoolean("YesNo", true))
            {
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setContentView(R.layout.activity_main);
                    InitializeItems();
                    initFab();
                }
                else{
                    setContentView(R.layout.activity_main_landscape);
                }
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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            RotationHandler.classOption e = RotationHandler.getCurrentClass();
//            switch(e)
//            {
//                case Add:
//                    //
//                    Intent i1 = new Intent(this, AddActivity.class);
//                    startActivity(i1);
//                    break;
//                case Edit:
//                    //
//                    Intent i2 = new Intent(this, EditActivity.class);
//                    startActivity(i2);
//                    break;
//                case LastN:
//                    Intent i3 = new Intent(this, LastNActivity.class);
//                    startActivity(i3);
//                    //
//                    break;
//                case Main:
//                    //
                    setContentView(R.layout.activity_main);
//                    break;
//            }
        }
        else{
            setContentView(R.layout.activity_main_landscape);
        }
    }

    private void startRegisterIntent(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void initFab()
    {
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        final Intent intent = new Intent(this, AddActivity.class);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    startActivity(intent);
                }
                else{
                    RightActivity newFragment = new RightActivity();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.rightFragment, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    getFragmentManager().executePendingTransactions();
                }
            }
        });
        FloatingActionButton myFab2 = (FloatingActionButton) findViewById(R.id.fab2);
        final Intent intent2 = new Intent(this, EditActivity.class);
        myFab2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    startActivity(intent2);
                }
                else{
                    EditFragment newFragment = new EditFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.rightFragment, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    getFragmentManager().executePendingTransactions();
                }

            }
//            }
        });
    }

    private void InitializeItems()
    {
        RotationHandler.getInstance();
        RotationHandler.setClass(RotationHandler.classOption.Main);

        Button prikazButton = (Button) findViewById(R.id.prikazButton);
        prikazButton.setOnClickListener(this);
        TextView stanjeT1 = (TextView)findViewById(R.id.stanjeDanText2);
        TextView stanjeT2 = (TextView)findViewById(R.id.stanjePredjenoText2);
        TextView stanjeT3 = (TextView)findViewById(R.id.stanjePotrosenoText2);
        TextView stanjeT4 = (TextView)findViewById(R.id.averageText2);
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
        final long avg;
        if(predjenPut ==0){avg = 0;}else {avg = (utrosenoGorivo*100)/predjenPut;}

        stanjeT4.setText(Long.toString(avg));
    }


    private static String getTodayDate()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId()==R.id.prikazButton)
        {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                startLastNIntent();
            }
            else{
startLastNFragment();
            }

        }
    }

    private void startLastNIntent(){
        Intent intent = new Intent(this, LastNActivity.class);
        Spinner s = (Spinner)findViewById(R.id.previousLookup);
        intent.putExtra("spinner", checkSpinner(s));
        startActivity(intent);
    }

    private void startLastNFragment(){
        LastNFragment newFragment = new LastNFragment();
        Bundle bundle = new Bundle();
        Spinner s = (Spinner)findViewById(R.id.previousLookup);
        bundle.putInt("spinner", checkSpinner(s));
        // set Fragmentclass Arguments
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.rightFragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        getFragmentManager().executePendingTransactions();
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

    @Override
    public void clickedPostition(int i, long mileage, int fuel, float price, String totalOrPerLiter) {
            Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("chosenEdit", i);
        if(mileage != 0 && fuel != 0 && price != 0 && !totalOrPerLiter.equals("")){
            intent.putExtra("chosenMileage", mileage);
            intent.putExtra("chosenFuel", fuel);
            intent.putExtra("chosenPrice", price);
            intent.putExtra("chosenTPL", totalOrPerLiter);
        }
            startActivity(intent);

    }
}
