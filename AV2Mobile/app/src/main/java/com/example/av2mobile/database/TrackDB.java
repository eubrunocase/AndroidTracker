package com.example.av2mobile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class TrackDB extends SQLiteOpenHelper {
    private static final String DATABASE= "percuso_database";
    private static final int VERSION = 1;

    public TrackDB(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
String createTable = "CREATE TABLE waypoints(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
        "latitude NUMERIC NOT NULL, longitude NUMERIC NOT NULL, altitude NUMERIC NOT NULL);";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
     String dropWaypointsTable = "DROP TABLE IF EXISTS waypoints";
     sqLiteDatabase.execSQL(dropWaypointsTable);
     onCreate(sqLiteDatabase);
    }

    public void save(Waypoint waypoint) {
        ContentValues values = new ContentValues();
        values.put("latitude", waypoint.getLatitude());
        values.put("longitude", waypoint.getLongitude());
        values.put("altitude", waypoint.getAltitude());
        getWritableDatabase().insert("waypoints", null, values);
    }

    public ArrayList<Waypoint> getAll() {
        ArrayList<Waypoint> waypoints = new ArrayList<>();

        String[] columns = {"id", "longitude", "latitude", "altitude"};
        try(Cursor cursor = getWritableDatabase().query("waypoints", columns, null, null, null,null, null)) {
            while(cursor.moveToNext()) {
                Waypoint waypoint = new Waypoint();
                waypoint.setId(cursor.getInt(0));
                waypoint.setLongitude(cursor.getDouble(1));
                waypoint.setLatitude(cursor.getDouble(2));
                waypoint.setAltitude(cursor.getDouble(3));
                waypoints.add(waypoint);
            }

        } catch (SQLException e) {
            Log.e("DatabaseError", "Error fetching waypoints", e);
        }
        return waypoints;
    }

    public void delete() {
        getWritableDatabase().execSQL("DELETE FROM waypoints");
    }
}
