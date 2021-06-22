package com.wdonker.airport;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class AirportDataSource {
    private SQLiteDatabase database;
    private AirportSQLiteHelper helper;
    private String[] allColumns;

    public AirportDataSource(Context context){
        helper = new AirportSQLiteHelper(context);

        this.allColumns = new String[]{
                AirportSQLiteHelper.COLUMN_ID,
                AirportSQLiteHelper.COLUMN_IDENTIFIER,
                AirportSQLiteHelper.COLUMN_TYPE,
                AirportSQLiteHelper.COLUMN_COUNTRY,
                AirportSQLiteHelper.COLUMN_NAME,
                AirportSQLiteHelper.COLUMN_LATITUDE,
                AirportSQLiteHelper.COLUMN_LONGITUDE};
    }

    public void open() throws SQLException {
        this.database = this.helper.getWritableDatabase();
    }

    public void close() {
        this.helper.close();
    }

    public List<Airport> getAllAirports() {
        ArrayList<Airport> airports = new ArrayList<>();

        Cursor cursor = this.database.query(
                AirportSQLiteHelper.TABLE_AIRPORTS,
                this.allColumns,
                null, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Airport airport = readAirport(cursor);
            airports.add(airport);

            cursor.moveToNext();
        }

        cursor.close();

        return airports;
    }

    public Airport getAirportById(int id) {
        Cursor cursor = this.database.query(
                true, AirportSQLiteHelper.TABLE_AIRPORTS,
                this.allColumns,
                AirportSQLiteHelper.COLUMN_ID + "=" + id,
                null, null, null, null, null);

        if (cursor.moveToFirst()){
            return readAirport(cursor);
        }

        return null;
    }

    public Airport getAirportByIdentifier(String identifier){
        Cursor cursor = this.database.query(
                true, AirportSQLiteHelper.TABLE_AIRPORTS,
                this.allColumns,
                AirportSQLiteHelper.COLUMN_IDENTIFIER + "='" + identifier + "'",
                null, null, null, null, null);

        if (cursor.moveToFirst()){
            return readAirport(cursor);
        }

        return null;
    }

    private Airport readAirport(Cursor cursor){
        int id = cursor.getInt(0);
        String identifier = cursor.getString(1);
        AirportType type = AirportType.fromInt(cursor.getInt(2));
        String country = cursor.getString(3);
        String name = cursor.getString(4);
        double latitude = cursor.getDouble(5);
        double longitude = cursor.getDouble(6);
        return new Airport(id, identifier, type, country, name, latitude, longitude);
    }
}
