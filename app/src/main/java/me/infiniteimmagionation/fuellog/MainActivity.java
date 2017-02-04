package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{//, AddFragment.OnFragmentInteractionListener, EditFragment.OnFragmentInteractionListener, ListNFragment.OnFragmentInteractionListener {

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
            setContentView(R.layout.activity_main);
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
//                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
//                {
//                    AddFragment a = AddFragment.newInstance();
//                    a.context=mainContext;
//                    getSupportFragmentManager().beginTransaction().add(R.id.activity_add, a).commit();
//                }
//                else{
                startActivity(intent);
//                }
            }
        });
        FloatingActionButton myFab2 = (FloatingActionButton) findViewById(R.id.fab2);
        final Intent intent2 = new Intent(this, EditActivity.class);
        myFab2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
//                {
//                    EditFragment f = EditFragment.newInstance();
//                    f.context = mainContext;
//                    getSupportFragmentManager().beginTransaction().add(R.id.activity_add, f).commit();
//                }
//                else
//                {
                startActivity(intent2);}
//            }
        });
    }

    private void InitializeItems()
    {
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
            startLastNIntent();
        }
    }

    private void startLastNIntent(){
        Intent intent = new Intent(this, LastNActivity.class);
        Spinner s = (Spinner)findViewById(R.id.previousLookup);
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
