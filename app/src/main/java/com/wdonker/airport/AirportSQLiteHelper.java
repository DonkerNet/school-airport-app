package com.wdonker.airport;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import au.com.bytecode.opencsv.CSVReader;

public class AirportSQLiteHelper extends SQLiteOpenHelper {

    // Database
    private static final String DATABASE_NAME = "airports.db";
    private static final int DATABASE_VERSION = 1;

    // Tables
    public static final String TABLE_AIRPORTS = "airports";

    // Columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IDENTIFIER = "identifier";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    // Queries

    private static final String SQL_CREATE =
            "create table " + TABLE_AIRPORTS + "("
                    + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_IDENTIFIER + " text not null, "
                    + COLUMN_TYPE + " integer not null, "
                    + COLUMN_COUNTRY + " text not null, "
                    + COLUMN_NAME + " text not null, "
                    + COLUMN_LATITUDE + " real not null, "
                    + COLUMN_LONGITUDE + " real not null"
                    + ");";

    private static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_AIRPORTS;

    //

    private AssetManager assetManager;

    public AirportSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.assetManager = context.getAssets();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
        HashMap<String, String> countryMap = getCountryMap(db);

        if (countryMap != null && countryMap.size() > 0) {
            createAirports(db, countryMap);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }

    private HashMap<String, String> getCountryMap(SQLiteDatabase db){
        CSVReader reader = getCsvReader("countries.csv");

        if (reader == null)
            return null;

        HashMap<String, String> results = new HashMap<>();

        db.beginTransaction();

        try {
            String[] values = reader.readNext(); // Skip first line

            while ((values = reader.readNext()) != null) {
                if (values.length < 3)
                    continue;

                String code = values[1];
                String country = values[2];

                results.put(code, country);
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        return results;
    }

    private void createAirports(SQLiteDatabase db, HashMap<String, String> countryMap){
        CSVReader reader = getCsvReader("airports.csv");

        if (reader == null)
            return;

        db.beginTransaction();

        try {
            String[] values = reader.readNext(); // Skip first line

            while ((values = reader.readNext()) != null) {
                if (values.length < 9)
                    continue;

                String countryCode = values[8];

                if (!countryMap.containsKey(countryCode))
                    continue;

                //int id = Integer.parseInt(values[0]);
                String identifier = values[1];
                AirportType type = AirportType.parse(values[2]);
                String name = values[3];
                String country = countryMap.get(countryCode);
                double latitude = Double.parseDouble(values[4]);
                double longitude = Double.parseDouble(values[5]);

                ContentValues cv = new ContentValues(3);
                //cv.put(COLUMN_ID, id);
                cv.put(COLUMN_IDENTIFIER, identifier);
                cv.put(COLUMN_TYPE, type.getValue());
                cv.put(COLUMN_NAME, name);
                cv.put(COLUMN_COUNTRY, country);
                cv.put(COLUMN_LATITUDE, latitude);
                cv.put(COLUMN_LONGITUDE, longitude);

                db.insert(TABLE_AIRPORTS, null, cv);
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private CSVReader getCsvReader(String fileName){
        InputStream stream = null;

        try {
            stream = this.assetManager.open(fileName);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        if (stream != null){
            return new CSVReader(new InputStreamReader(stream));
        }

        return null;
    }
}
