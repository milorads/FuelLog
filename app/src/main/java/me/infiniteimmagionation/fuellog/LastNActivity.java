package me.infiniteimmagionation.fuellog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LastNActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_n);
        TextView utrosak = (TextView)findViewById(R.id.stanjePotrosenoText2IP);
        TextView predjeno = (TextView)findViewById(R.id.stanjePredjenoText2IP);
    }
}
