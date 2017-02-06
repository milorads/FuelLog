package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Map;

public class LastNActivity extends AppCompatActivity {
//
    // Shared preference variable
    public static final String mypreference = "FirstRun";
    SharedPreferences sharedpreferences;
    DatabaseHandler database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_last_n);
            Initialize();
        }
        else{
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("class", RotationHandler.getCurrentClass().toString());
            startActivity(i);
        }

    }

    private void Initialize(){
        RotationHandler.getInstance();
        RotationHandler.setClass(RotationHandler.classOption.LastN);

        TextView utrosak = (TextView)findViewById(R.id.stanjePotrosenoText2IP);
        TextView predjeno = (TextView)findViewById(R.id.stanjePredjenoText2IP);
        TextView average = (TextView)findViewById(R.id.customAvgText2);
        Intent intent = getIntent();
        int period = intent.getIntExtra("spinner", 0);
        database = DatabaseHandler.getInstance(this);
        long startMileage =0, date=0;
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_last_n);
        }
        else{
            //setContentView(R.layout.activity_main_landscape);
            Intent i = new Intent(this, MainLandscapeFragment.class);
            startActivity(i);
        }
    }
}
