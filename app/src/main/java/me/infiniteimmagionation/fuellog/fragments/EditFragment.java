package me.infiniteimmagionation.fuellog.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.infiniteimmagionation.fuellog.DatabaseHandler;
import me.infiniteimmagionation.fuellog.DatabaseModel;
import me.infiniteimmagionation.fuellog.DatabaseModelAdapter;
import me.infiniteimmagionation.fuellog.EditActivity;
import me.infiniteimmagionation.fuellog.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends Fragment {

    public DatabaseHandler database;
    public Context context;
    ListView listView;
    List<DatabaseModel> modelList;
    View v;

    private OnFragmentInteractionListener mListener;

    public EditFragment() {
        // Required empty public constructor
    }

    public static EditFragment newInstance() {
        EditFragment fragment = new EditFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_edit, container, false);
        database = DatabaseHandler.getInstance(context);

        modelList = new ArrayList<DatabaseModel>();
        listView = (ListView) v.findViewById(R.id.listView);
        Initialize();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {

    }

    private void Initialize()
    {
        List<DatabaseModel> models = database.getAllRefills();

        ArrayList<DatabaseModel> arrayOfUsers = new ArrayList<DatabaseModel>();
        DatabaseModelAdapter adapter = new DatabaseModelAdapter(context, arrayOfUsers);
        final ListView listView = (ListView) v.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        for (DatabaseModel m : models) {
            adapter.add(m);
            modelList.add(m);
        }
        // ListView setOnItemClickListener function apply here.

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
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
                builder.setTitle("Editor");
                builder.setCancelable(false);
//                builder.setIcon(R.drawable.galleryalart);

                builder.setView(v).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        popupHandler(position, v);
                        Initialize();
                    }}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
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

    private EditText mi;
    private EditText pr;
    private EditText fl;
    private Spinner sp;
    private String tpl;

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
        catch (Exception e){
//            Toast.makeText(context.this,"Error", Toast.LENGTH_SHORT).show(); return;}
        try
        {
            fail = false;
            mileage = Integer.parseInt(mi.getText().toString());
            fuel = Integer.parseInt(fl.getText().toString());
            price = Float.parseFloat(pr.getText().toString());
        }
        catch (Exception ee)
        {
            fail = true;
//            Toast.makeText(EditActivity.this,"Error", Toast.LENGTH_SHORT).show();
        }
        if(!fail)
        {
            int id = model.get_id();
            //    public DatabaseModel(String totalOrPerLiter, float refillPrice, long km, long liters){
            DatabaseModel newModel = new DatabaseModel(tpl, price, mileage, fuel);
            boolean a = database.editDatabaseEntry(id, newModel);
            if (a){
//                Toast.makeText(EditActivity.this,"Success", Toast.LENGTH_SHORT).show();
                     }
            else{
//                    Toast.makeText(EditActivity.this,"Error", Toast.LENGTH_SHORT).show();
                }}
        }
    }
}
