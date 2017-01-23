package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Map;

public class LastNActivity extends AppCompatActivity {

    // Shared preference variable
    public static final String mypreference = "FirstRun";
    SharedPreferences sharedpreferences;
    DatabaseHandler database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_n);
        TextView utrosak = (TextView)findViewById(R.id.stanjePotrosenoText2IP);
        TextView predjeno = (TextView)findViewById(R.id.stanjePredjenoText2IP);
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
        String predjenPut = (String) map.get("put");
        String utrosakGoriva = (String) map.get("gorivo");
        utrosak.setText(utrosakGoriva);
        predjeno.setText(predjenPut);
    }
}
