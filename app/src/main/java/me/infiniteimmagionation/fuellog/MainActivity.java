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

public class MainActivity extends AppCompatActivity {//implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    public static final String mypreference = "FirstRun";
//    public DatabaseHandler database = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        DatabaseHandler database = DatabaseHandler.getInstance(this);
        if(sharedpreferences.contains("YesNo"))
        {
            if (sharedpreferences.getBoolean("YesNo", true))
            {
                    setContentView(R.layout.activity_main);
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
    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
}
