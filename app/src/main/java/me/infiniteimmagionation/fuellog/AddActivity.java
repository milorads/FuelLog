package me.infiniteimmagionation.fuellog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Date;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseHandler db = DatabaseHandler.getInstance(this);

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
            /**
             * CRUD Operations
             * */
            long mileage =0;
            int fuel =0;
            float price=0;
            EditText mi = (EditText)findViewById(R.id.mileageText);
            EditText pr = (EditText)findViewById(R.id.priceText);
            EditText fl = (EditText)findViewById(R.id.fuelText);
            Spinner sp = (Spinner)findViewById(R.id.perLiterTotal);
            String tpl = sp.getSelectedItem().toString();
            try {
                mileage = Integer.parseInt(mi.getText().toString());
                fuel = Integer.parseInt(fl.getText().toString());
                price = Integer.parseInt(pr.getText().toString());
            }
            catch (Exception e)
            {}
            DatabaseModel model = new DatabaseModel(tpl,System.currentTimeMillis(),price, mileage);
            db.addRefill(model);
    }
    }
}
