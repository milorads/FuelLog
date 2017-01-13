package me.infiniteimmagionation.fuellog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by msekulovic on 1/12/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "fuelConsumptionTracer";

    // Contacts table name
    private static final String TABLE_REFILLS = "refils";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TPL = "tpl";
    private static final String KEY_DATE = "date";
    private static final String KEY_CDOP = "cdop";
    private static final String KEY_KM = "km";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_REFILLS_TABLE = "CREATE TABLE IF NOT EXISTS" + TABLE_REFILLS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TPL + " TEXT,"
                + KEY_DATE + " DATE"
                + KEY_CDOP + " FLOAT"
                + KEY_KM + " BIGINT"
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
        values.put(KEY_ID, model.get_id());
        values.put(KEY_TPL, model.get_tpl());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        date = model.get_date();
        values.put(KEY_DATE, dateFormat.format(date));
//        values.put(KEY_DATE, contact.get_date());

        values.put(KEY_CDOP, model.get_cdop());
        values.put(KEY_KM, model.get_km());

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
                DatabaseModel refill = new DatabaseModel(cursor.getInt(0),cursor.getString(1), new Date(cursor.getLong(2)*1000), cursor.getFloat(3), cursor.getLong(4));
                // Adding contact to list
                refillList.add(refill);
            } while (cursor.moveToNext());
        }

        // return contact list
        return refillList;
    }

    public int getRefillCount() {
        String countQuery = "SELECT  * FROM " + TABLE_REFILLS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
