package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    public static final String mypreference = "FirstRun";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        addButtonListener();
    }

    private void addButtonListener(){
        Button saveDugme = (Button)findViewById(R.id.saveButton);
        saveDugme.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onClickHandler(v);
    }

    private void onClickHandler(View v){
        EditText mileage = (EditText)findViewById(R.id.editText);
        EditText fuelleft = (EditText)findViewById(R.id.editText2);
        if(v.getId()==R.id.saveButton)
        {
            int iMileage = 0;
            int iFuel = 0;
            try
            {
                iMileage = Integer.parseInt(mileage.getText().toString());
                iFuel = Integer.parseInt(fuelleft.getText().toString());
                if (iMileage<=0 && iFuel<=0)
                {
                    dialogBuilder(getResources().getString(R.string.fuel_mileage_prompt));
                }
                else if(iMileage<=0)
                {
                    dialogBuilder(getResources().getString(R.string.mileage_prompt));
                }
                else if(iFuel<=0)
                {
                    dialogBuilder(getResources().getString(R.string.fuel_prompt));
                }
                else
                {
                    saveMileageFuel(iMileage, iFuel);
                    savePreference();
                }
            }
            catch (Exception e)
            {
                emptyDialogBuilder();
            }
        }
    }

    private void emptyDialogBuilder(){
        final Intent intent = new Intent(this, RegisterActivity.class);
        new AlertDialog.Builder(RegisterActivity.this)
                .setTitle(getResources().getString(R.string.warn))
                .setMessage(getResources().getString(R.string.fuel_mileage_warning))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(intent);
                    }
                }).show();
    }

    private void dialogBuilder(String message)
    {
        AlertDialog.Builder alt = new AlertDialog.Builder(RegisterActivity.this);
        alt.setMessage(message).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // save mileage fuel and pref update.
                try
                {
                    EditText mileage = (EditText) findViewById(R.id.editText);
                    EditText fuelleft = (EditText) findViewById(R.id.editText2);
                    long iMileage = Integer.parseInt(mileage.getText().toString());
                    int iFuel = Integer.parseInt(fuelleft.getText().toString());
                    saveMileageFuel(iMileage, iFuel);
                    savePreference();
                }
                catch (Exception e)
                {
                    finish();
                    // info cant be empty
                    new AlertDialog.Builder(RegisterActivity.this)
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
                finish();
            }
        }).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
             dialog.cancel();
            }
        });
        AlertDialog alert = alt.create();
        alert.setTitle(getResources().getString(R.string.mileage_fuel_confirmation));
        alert.show();
    }

    private void savePreference()
    {
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        if(sharedpreferences.contains("YesNo"))
        {
            if (sharedpreferences.getBoolean("YesNo", false))
            {
                sharedpreferences.edit().putBoolean("YesNo",true).apply();
            }
        }
        else
        {
            sharedpreferences.edit().putBoolean("YesNo",true).apply();
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void saveMileageFuel(long mileage, int fuel)
    {
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        sharedpreferences.edit().putLong("Mileage", mileage).apply();
        sharedpreferences.edit().putInt("Fuel", fuel).apply();
        if(!sharedpreferences.contains("Date")){
            sharedpreferences.edit().putLong("Date", System.currentTimeMillis()).apply();}

    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
}
