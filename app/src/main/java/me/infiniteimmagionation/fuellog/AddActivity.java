package me.infiniteimmagionation.fuellog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.saveButton)
        {
            // get status trenutni iz shared i interne memorije
            // uporedi, napravi report i vrati u view main
        }
    }
}
