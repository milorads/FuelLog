package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    public DatabaseHandler database;
    Context context;
    ListView listView;
    List<DatabaseModel> modelList;
    private EditText mi;
    private EditText pr;
    private EditText fl;
    private Spinner sp;
    private String tpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Initialize();
    }

    private void Initialize(){
        database = DatabaseHandler.getInstance(this);
        context = this;

        modelList = new ArrayList<DatabaseModel>();
        listView = (ListView) findViewById(R.id.listView);
        InitializeList();

    }

    private void InitializeList()
    {
        List<DatabaseModel> models = database.getAllRefills();

        ArrayList<DatabaseModel> arrayOfUsers = new ArrayList<DatabaseModel>();
        DatabaseModelAdapter adapter = new DatabaseModelAdapter(this, arrayOfUsers);
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        for (DatabaseModel m : models) {
            adapter.add(m);
            modelList.add(m);
        }
        // ListView setOnItemClickListener function apply here.
        listViewListenerInit();
    }

    private void listViewListenerInit(){
        listView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                //
//                Toast.makeText(EditActivity.this, modelList.get(position).get_tpl(), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // Get the layout inflater
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View v = inflater.inflate(R.layout.edit_database_item, null);
                mi = (EditText) v.findViewById(R.id.editMileageText);
                pr = (EditText) v.findViewById(R.id.editPriceText);
                fl = (EditText) v.findViewById(R.id.editFuelText);
                sp = (Spinner) v.findViewById(R.id.editTPLSpinner);
                mi.setText(Long.toString(modelList.get(position).get_km()));
                pr.setText(Float.toString(modelList.get(position).get_cdop()));
                fl.setText(Long.toString(modelList.get(position).get_lit()));
                if (modelList.get(position).get_tpl().equals("Total")){
                    sp.setSelection(1);}
                else{sp.setSelection(0);}
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the
                // dialog layout
                builder.setTitle(getResources().getString(R.string.editor));
                builder.setCancelable(false);
//                builder.setIcon(R.drawable.galleryalart);

                builder.setView(v).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        popupHandler(position, v);
                    }}).setNegativeButton(getResources().getString(R.string.canc), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.create();
                builder.show();

            }
        });
    }

    private void popupHandler(int modelPosition, View v)
    {
        DatabaseModel model = modelList.get(modelPosition);
        boolean fail;
        long mileage =0;
        int fuel =0;
        float price = 0;
        try {
            mi = (EditText) v.findViewById(R.id.editMileageText);
            pr = (EditText) v.findViewById(R.id.editPriceText);
            fl = (EditText) v.findViewById(R.id.editFuelText);
            sp = (Spinner) v.findViewById(R.id.editTPLSpinner);
            tpl = sp.getSelectedItem().toString();
        }
        catch (Exception e){Toast.makeText(EditActivity.this,getResources().getString(R.string.err), Toast.LENGTH_SHORT).show(); return;}
            try
        {
            fail = false;
            mileage = Integer.parseInt(mi.getText().toString());
            fuel = Integer.parseInt(fl.getText().toString());
            price = Float.parseFloat(pr.getText().toString());
        }
        catch (Exception e)
        {
            fail = true;
            Toast.makeText(EditActivity.this,getResources().getString(R.string.err), Toast.LENGTH_SHORT).show();
        }
        if(!fail)
        {
            int id = model.get_id();
            //    public DatabaseModel(String totalOrPerLiter, float refillPrice, long km, long liters){
            DatabaseModel newModel = new DatabaseModel(tpl, price, mileage, fuel);
            boolean a = database.editDatabaseEntry(id, newModel);
            if (a){
            Toast.makeText(EditActivity.this,getResources().getString(R.string.succ), Toast.LENGTH_SHORT).show();}
            else{Toast.makeText(EditActivity.this,getResources().getString(R.string.err), Toast.LENGTH_SHORT).show();}
        }
    }
}
