package me.infiniteimmagionation.fuellog;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by msekulovic on 1/12/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Singleton
    private static DatabaseHandler dbInstance;

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "fuelConsumptionTracer";

    private static final String TABLE_REFILLS = "REFILLS";

    private static final String KEY_ID = "ID";
    private static final String KEY_TPL = "TPL";
    private static final String KEY_DATE = "DATE";
    private static final String KEY_CDOP = "CDOP";
    private static final String KEY_KM = "KM";
    private static final String KEY_LIT = "LITERS";

    public static synchronized DatabaseHandler getInstance(Context context)
    {
        if (dbInstance == null) {
            dbInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return dbInstance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_REFILLS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_REFILLS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TPL + " TEXT,"
                + KEY_DATE + " BIGINT,"
                + KEY_CDOP + " FLOAT,"
                + KEY_KM + " BIGINT,"
                + KEY_LIT + " BIGINT"
                + ")";
        db.execSQL(CREATE_REFILLS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REFILLS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    void addRefill(DatabaseModel model) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(KEY_ID, model.get_id());
        values.put(KEY_TPL, model.get_tpl());

        values.put(KEY_DATE, model.get_date());
//        values.put(KEY_DATE, contact.get_date());

        values.put(KEY_CDOP, model.get_cdop());
        values.put(KEY_KM, model.get_km());

        values.put(KEY_LIT, model.get_lit());


        // Inserting Row
        db.insert(TABLE_REFILLS, null, values);
        db.close(); // Closing database connection
    }

    public List<DatabaseModel> getAllRefills() {
        List<DatabaseModel> refillList = new ArrayList<DatabaseModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_REFILLS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DatabaseModel refill = new DatabaseModel(cursor.getInt(0),cursor.getString(1), cursor.getLong(2), cursor.getFloat(3), cursor.getLong(4), cursor.getLong(5));
                // Adding contact to list
                refillList.add(refill);
            } while (cursor.moveToNext());
        }

        // return contact list
        return refillList;
    }

    public DatabaseModel getLastMileage()
    {
        DatabaseModel model = null;
        String selectQuery = "SELECT * FROM " + TABLE_REFILLS +" ORDER BY "+KEY_KM+" DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
                model = new DatabaseModel(cursor.getInt(0),cursor.getString(1), cursor.getLong(2), cursor.getFloat(3), cursor.getLong(4), cursor.getLong(5));
        }
        return model;
    }



    public int getRefillCount() {
        String countQuery = "SELECT  * FROM " + TABLE_REFILLS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public Map<String,String> getConsumptionPerPeriod(int nOfDays, long startMileage, long startDate)
    {
        long gornjaGranica = getToday();
        long donjaGranica = getPreviousDate(gornjaGranica, nOfDays);

        String selectQueryFirst = "SELECT "+KEY_KM+" FROM " + TABLE_REFILLS +" WHERE DATE BETWEEN "+Long.toString(donjaGranica)+" AND "+Long.toString(gornjaGranica)+" ORDER BY "+KEY_DATE+" DESC LIMIT 1";
        String selectQueryLast = "SELECT "+KEY_KM+" FROM " + TABLE_REFILLS +"  WHERE DATE BETWEEN "+Long.toString(donjaGranica)+" AND "+Long.toString(gornjaGranica)+" ORDER BY "+KEY_DATE+" ASC LIMIT 1";
        String litQuery = "SELECT "+KEY_LIT+" FROM " + TABLE_REFILLS +"  WHERE DATE BETWEEN "+Long.toString(donjaGranica)+" AND "+Long.toString(gornjaGranica);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor litCursor = db.rawQuery(litQuery, null);
        Cursor cursorFirst = db.rawQuery(selectQueryFirst, null);
        Cursor cursorLast = db.rawQuery(selectQueryLast, null);
        long fKm = 0, lKm = 0, uG = 0;
        // looping through all rows and adding to list
        if (cursorFirst.moveToFirst()) {
            fKm = cursorFirst.getLong(0);
        }
        if (cursorLast.moveToFirst()) {
            lKm = cursorLast.getLong(0);
        }
        if (litCursor.moveToFirst()) {
            do {
                uG += litCursor.getLong(0);
            } while (litCursor.moveToNext());
        }
        long pPut = 0;
        if (startDate <=  gornjaGranica && startDate >= donjaGranica)
        {
            pPut = fKm - startMileage;
        }
        else
        {
            pPut = fKm - lKm;
        }
        final long predjenPut = pPut;
        final long utrosakGoriva = uG;
        Map<String,String> map = new HashMap<String, String>(){{
            put("put", Long.toString(predjenPut));
            put("gorivo", Long.toString(utrosakGoriva));
        }};
        return map;
    }
    private long getToday(){
        return System.currentTimeMillis();
    }
    private long getPreviousDate(long today, int days)
    {
        Date date=new Date(today);
        Date dateBefore = new Date(date.getTime() - days * 24 * 3600 * 1000 );
        return dateBefore.getTime();
    }

}
