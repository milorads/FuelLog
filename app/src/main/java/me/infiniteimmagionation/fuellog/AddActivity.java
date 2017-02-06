package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseHandler db = DatabaseHandler.getInstance(this);
    SharedPreferences sharedpreferences;
    public static final String mypreference = "FirstRun";
    private DatabaseHandler database;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Initialize();
    }

    private void Initialize(){
        RotationHandler.getInstance();
        RotationHandler.setClass(RotationHandler.classOption.Add);
        Intent intent = getIntent();
        long mileageFromIntent =0;
        int fuelFromIntent =0;
        float priceFromIntent = 0;
        String tplFromIntent ="";
        boolean intentSucc = false;
        Bundle a = intent.getExtras();
        if(intent!=null && a !=null){
        EditText mi = (EditText)findViewById(R.id.mileageText);
        EditText pr = (EditText)findViewById(R.id.priceText);
        EditText fl = (EditText)findViewById(R.id.fuelText);
        Spinner sp = (Spinner)findViewById(R.id.perLiterTotal);
        //String tpl = sp.getSelectedItem().toString();
            //long mileage, int fuel, float price, String totalOrPerLiter
            if(intent.getExtras().containsKey("chosenMileage")){
                mileageFromIntent = intent.getLongExtra("chosenMileage", 0);
                fuelFromIntent = intent.getIntExtra("chosenMileage", 0);
                priceFromIntent = intent.getFloatExtra("chosenPrice", 0);
                tplFromIntent = intent.getStringExtra("chosenTPL");
                intentSucc = true;
            }
            if(intentSucc){
                mi.setText(Long.toString(mileageFromIntent));
                pr.setText(Float.toString(priceFromIntent));
                fl.setText(Long.toString(fuelFromIntent));
                if (tplFromIntent.equals("Total")){
                    sp.setSelection(1);}
                else{sp.setSelection(0);}
            }
        }

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
                        .setTitle(getResources().getString(R.string.warn))
                        .setMessage(getResources().getString(R.string.fuel_mileage_warning))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
            if(!fail)
            {
                long mil =0;
                sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                if(sharedpreferences.contains("Mileage")){
                    mil = sharedpreferences.getLong("Mileage", 0);}
                DatabaseModel model = new DatabaseModel(tpl, System.currentTimeMillis(), price, mileage, fuel);
                if(db.addRefill(model, mil)){
                    // pravljenje report-a
                    WriteReport(model, fuel, fuelPrev, startMileage, startDate);
                    // vracanje u main
                    finish();
                }
                else{
                    Toast.makeText(this, getResources().getString(R.string.mileage_higher), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
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
        database = DatabaseHandler.getInstance(this);
        List<DatabaseModel> lista = database.getAllRefills();
        long utrosenoGorivo = 0;
        for (DatabaseModel m:lista) {

            utrosenoGorivo += m.get_lit();
        }
        final long avg = (utrosenoGorivo*100)/range;
        textmsg+=getResources().getString(R.string.report3) + days + getResources().getString(R.string.report4)+range+getResources().getString(R.string.report5)+"\n";
        textmsg+=getResources().getString(R.string.report6)+ Long.toString(avg);

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
