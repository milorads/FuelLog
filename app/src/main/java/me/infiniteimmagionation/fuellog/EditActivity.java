package me.infiniteimmagionation.fuellog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        DatabaseHandler db = DatabaseHandler.getInstance(this);
        listView = (ListView) findViewById(R.id.listView);
        List<DatabaseModel> models = db.getAllRefills();

        ArrayList<DatabaseModel> arrayOfUsers = new ArrayList<DatabaseModel>();
        DatabaseModelAdapter adapter = new DatabaseModelAdapter(this, arrayOfUsers);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        String model = "";
        for (DatabaseModel m : models) {
            adapter.add(m);
        }

    }
}
