package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {//implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    public static final String mypreference = "FirstRun";
    DatabaseHandler database;
//    public DatabaseHandler database = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        database = DatabaseHandler.getInstance(this);
        if(sharedpreferences.contains("YesNo"))
        {
            if (sharedpreferences.getBoolean("YesNo", true))
            {
                    setContentView(R.layout.activity_main);
                InitializeItems();
                FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
                final Intent intent = new Intent(this, AddActivity.class);
                myFab.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(intent);
                    }
                });
            }
            else
            {
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    private void InitializeItems()
    {
        TextView stanjeT1 = (TextView)findViewById(R.id.stanjeDanText2);
        TextView stanjeT2 = (TextView)findViewById(R.id.stanjePredjenoText2);
        TextView stanjeT3 = (TextView)findViewById(R.id.stanjePotrosenoText2);
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
    }

    private static String getTodayDate()
    {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
}
