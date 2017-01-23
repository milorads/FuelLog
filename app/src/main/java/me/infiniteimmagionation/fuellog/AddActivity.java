package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseHandler db = DatabaseHandler.getInstance(this);
    SharedPreferences sharedpreferences;
    public static final String mypreference = "FirstRun";

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        Button saveDugme = (Button)findViewById(R.id.saveAddButton);
        saveDugme.setOnClickListener(this);
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
            EditText mi = (EditText)findViewById(R.id.mileageText);
            EditText pr = (EditText)findViewById(R.id.priceText);
            EditText fl = (EditText)findViewById(R.id.fuelText);
            Spinner sp = (Spinner)findViewById(R.id.perLiterTotal);
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
                new AlertDialog.Builder(AddActivity.this)
                        .setTitle("Warning")
                        .setMessage("Mileage/Fuel/Price must be a number and can not be empty")
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
            if(!fail)
            {
                DatabaseModel model = new DatabaseModel(tpl, System.currentTimeMillis(), price, mileage, fuel);
                db.addRefill(model);

                // pravljenje report-a
                WriteReport(model, fuel, fuelPrev, startMileage, startDate);
                // vracanje u main
                finish();
            }
        }
    }
    private void WriteReport(DatabaseModel model, int fuel, int prevFuel, long startMileage, long startDate)
    {
        // write new fuel state to sharedprefs
        long sDateL = 0;
        if(sharedpreferences.contains("Date")){
            sDateL = sharedpreferences.getLong("Date", 0);}
        sharedpreferences.edit().putInt("Fuel", fuel).apply();
        String FILENAME = "Report"+Long.toString(model.get_date());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(model.get_date()));
        String textmsg = "Report for " + dateString + "\n\n";
        long range = model.get_km() - startMileage;
        long pastTime = model.get_date() - startDate;
        Date sDate = new Date(sDateL);
        Date mDate = new Date(model.get_date());
        long days = TimeUnit.MILLISECONDS.toDays(pastTime);
        long s = TimeUnit.DAYS.convert(pastTime, TimeUnit.MILLISECONDS);

        textmsg+="For the period of " + days + ", the range of: "+range+" was covered.\n";
        textmsg+="Average consumption was: "+"";

        try {
            FileOutputStream fileout=openFileOutput(FILENAME+".txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write(textmsg);
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
