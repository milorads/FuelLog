package me.infiniteimmagionation.fuellog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Map;

public class LastNActivity extends AppCompatActivity {

    DatabaseHandler database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_n);
        TextView utrosak = (TextView)findViewById(R.id.stanjePotrosenoText2IP);
        TextView predjeno = (TextView)findViewById(R.id.stanjePredjenoText2IP);
        Intent intent = getIntent();
        int period = 0;
        intent.getIntExtra("spinner", period);
        database = DatabaseHandler.getInstance(this);
        Map<String, String> map = database.getConsumptionPerPeriod(period);
        String predjenPut = (String) map.get("put");
        String utrosakGoriva = (String) map.get("gorivo");
    }
}
