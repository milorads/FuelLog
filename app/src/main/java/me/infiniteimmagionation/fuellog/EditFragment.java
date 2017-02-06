package me.infiniteimmagionation.fuellog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    public DatabaseHandler database;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "FirstRun";
    Context context;
    ListView listView;
    List<DatabaseModel> modelList;
    private EditText mi;
    private EditText pr;
    private EditText fl;
    private Spinner sp;
    private String tpl;

    public EditFragment() {
        // Required empty public constructor
    }

    public static EditFragment newInstance(String param1, String param2) {
        EditFragment fragment = new EditFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_edit, container, false);

        // do something
        Initialize();

        return v;
    }

    private void Initialize(){
        RotationHandler.getInstance();
        RotationHandler.setClass(RotationHandler.classOption.Edit);

        context = getActivity();
        database = DatabaseHandler.getInstance(context);
        sharedpreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        modelList = new ArrayList<DatabaseModel>();
        listView = (ListView) v.findViewById(R.id.listView);
        InitializeList();

    }

    private void InitializeList()
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
        listViewListenerInit();
    }

    private int tbsPosition = -1;
    View classView;

    private void listViewListenerInit(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                //
                tbsPosition=position;
//                Toast.makeText(EditActivity.this, modelList.get(position).get_tpl(), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // Get the layout inflater
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                classView = inflater.inflate(R.layout.edit_database_item, null);
                mi = (EditText) classView.findViewById(R.id.editMileageText);
                pr = (EditText) classView.findViewById(R.id.editPriceText);
                fl = (EditText) classView.findViewById(R.id.editFuelText);
                sp = (Spinner) classView.findViewById(R.id.editTPLSpinner);
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

                builder.setView(classView).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        popupHandler(position, classView);
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

    private void popupHandler(int modelPosition, View temp)
    {
        long startMileage = 0;
        if(sharedpreferences.contains("Mileage")){
            startMileage = sharedpreferences.getLong("Mileage", 0);}
        DatabaseModel model = modelList.get(modelPosition);
        boolean fail;
        long mileage =0;
        int fuel =0;
        float price = 0;
        try {
            mi = (EditText) temp.findViewById(R.id.editMileageText);
            pr = (EditText) temp.findViewById(R.id.editPriceText);
            fl = (EditText) temp.findViewById(R.id.editFuelText);
            sp = (Spinner) temp.findViewById(R.id.editTPLSpinner);
            tpl = sp.getSelectedItem().toString();
        }
        catch (Exception e){
            Toast.makeText(context,getResources().getString(R.string.err), Toast.LENGTH_SHORT).show(); return;}
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
            Toast.makeText(context,getResources().getString(R.string.err), Toast.LENGTH_SHORT).show();
        }
        if(!fail)
        {
            int id = model.get_id();
            //    public DatabaseModel(String totalOrPerLiter, float refillPrice, long km, long liters){
            DatabaseModel newModel = new DatabaseModel(tpl, price, mileage, fuel);
            boolean a = database.editDatabaseEntry(id, newModel, startMileage);
            if (a){
                Toast.makeText(context,getResources().getString(R.string.succ), Toast.LENGTH_SHORT).show();}
            else{Toast.makeText(context,getResources().getString(R.string.err), Toast.LENGTH_SHORT).show();}
        }
        InitializeList();
    }

    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        long mlg =0;
        int fuel =0;
        float price = 0;
        //tpl
        try {
            mi = (EditText) classView.findViewById(R.id.editMileageText);
            pr = (EditText) classView.findViewById(R.id.editPriceText);
            fl = (EditText) classView.findViewById(R.id.editFuelText);
            sp = (Spinner) classView.findViewById(R.id.editTPLSpinner);
            mlg = Integer.parseInt(mi.getText().toString());
            fuel = Integer.parseInt(fl.getText().toString());
            price = Float.parseFloat(pr.getText().toString());
            tpl = sp.getSelectedItem().toString();
        }
        catch (Exception e){

        }
        if(tpl != null){
        mListener.clickedPostition(tbsPosition, mlg, fuel, price, tpl);}
        else{
            mListener.clickedPostition(tbsPosition, mlg, fuel, price, "");
        }
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void clickedPostition(int i, long mileage, int fuel, float price, String totalOrPerLiter);
    }
}
