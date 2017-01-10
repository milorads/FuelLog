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

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    public static final String mypreference = "FirstRun";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        if(sharedpreferences.contains("YesNo"))
        {
            if (sharedpreferences.getBoolean("YesNo", true))
            {
                setContentView(R.layout.activity_main);
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

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

}
