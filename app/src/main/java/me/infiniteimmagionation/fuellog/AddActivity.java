package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Date;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseHandler db = DatabaseHandler.getInstance(this);
    SharedPreferences sharedpreferences;
    public static final String mypreference = "FirstRun";

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.saveButton)
        {
            boolean fail;
            // get status trenutni iz shared i interne memorije
            // uporedi, napravi report i vrati u view main
            long mileage =0;
            int fuel =0;
            int fuelPrev = 0;
            float price=0;
            if(sharedpreferences.contains("Fuel")){
                fuelPrev = sharedpreferences.getInt("Fuel", 0);}
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
                DatabaseModel model = new DatabaseModel(tpl, System.currentTimeMillis(), price, mileage);
                db.addRefill(model);
                // pravljenje report-a
                // vracanje u main
            }
        }
    }
}
