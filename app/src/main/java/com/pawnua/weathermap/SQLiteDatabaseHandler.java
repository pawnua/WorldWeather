package com.pawnua.weathermap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pawnua.weathermap.model.CityAmount;

import java.util.ArrayList;

/**
 * Created by Nick on 13.11.2014.
 */
public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
// Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "citiesManager";
    // Contacts table name
    private static final String TABLE_NAME = "cityViews";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "city_name";
    private static final String KEY_AMOUNT = "views_amount";

    ArrayList<CityAmount> city_list = new ArrayList<CityAmount>();

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_AMOUNT + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
// Create tables again
        onCreate(db);
    }


    // Adding new city
    public void Add_City(String CityName, int Count) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, CityName); // City Name
        values.put(KEY_AMOUNT, Count); // Contact Phone
// Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    // Getting amount city view
    int Get_Amount(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
                        KEY_NAME, KEY_AMOUNT}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor.moveToFirst()) {

            int idColIndex = cursor.getColumnIndex(KEY_ID);
            int nameColIndex = cursor.getColumnIndex(KEY_NAME);
            int amountColIndex = cursor.getColumnIndex(KEY_AMOUNT);

            int amount = cursor.getInt(amountColIndex);

            cursor.close();
            db.close();
            return amount;

        }

        return 0;
    }

    // Getting amount city view
    public void increaseCityViewAmount(String CityName, int Count) {

        SQLiteDatabase db = this.getReadableDatabase();

        // First, read current amount
        Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
                        KEY_NAME, KEY_AMOUNT}, KEY_NAME + "=?",
                new String[] { CityName }, null, null, null, null);

        if (cursor.moveToFirst()) {

            // if exist - increase (update)

            int idColIndex = cursor.getColumnIndex(KEY_ID);
            int amountColIndex = cursor.getColumnIndex(KEY_AMOUNT);

            int amount = cursor.getInt(amountColIndex);
            int id = cursor.getInt(idColIndex);

            cursor.close();

            // Add_City
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, CityName); // City Name
            values.put(KEY_AMOUNT, amount + Count); // Amount

            // Update Row
            db.update(TABLE_NAME, values, KEY_ID + " = ?", new String[] { String.valueOf(id)});

            db.close();

        }
        else {

            // 0 - add new row

            // Add_City
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, CityName); // City Name
            values.put(KEY_AMOUNT, Count); // Amount

            // Inserting Row
            db.insert(TABLE_NAME, null, values);

            db.close();

        }


    }

    // Getting All Contacts
    public ArrayList<CityAmount> Get_AllCities() {

        try {
            city_list.clear();
// Select All Query
            String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + KEY_AMOUNT + " DESC ";
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    CityAmount city = new CityAmount();
                    city.amount = cursor.getInt(cursor.getColumnIndex(KEY_AMOUNT));
                    city.city = cursor.getString(cursor.getColumnIndex(KEY_NAME));
// Adding contact to list
                    city_list.add(city);
                } while (cursor.moveToNext());
            }
// return contact list
            cursor.close();
            db.close();
            return city_list;
        } catch (Exception e) {
        }
        return city_list;
    }

}
