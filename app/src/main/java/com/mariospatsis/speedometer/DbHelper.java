package com.mariospatsis.speedometer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "app.db";
    public static final int DATABASE_VERSION = 1;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = " ,";
    public static final String SQL_CREATE_LIMIT_TABLE =
            "CREATE TABLE IF NOT EXISTS "+ DbSpeedometer.Limit.TABLE_NAME + " ("
                    + DbSpeedometer.Limit._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    DbSpeedometer.Limit.COLUMN_SPEEDLIMIT + TEXT_TYPE + ")";
    public static final String SQL_CREATE_EVENTS_TABLE =
            "CREATE TABLE IF NOT EXISTS "+ DbSpeedometer.Events.TABLE_NAME + " ("
                    + DbSpeedometer.Events._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    DbSpeedometer.Events.COLUMN_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    DbSpeedometer.Events.COLUMN_LONGTITUDE + TEXT_TYPE + COMMA_SEP +
                    DbSpeedometer.Events.COLUMN_SPEED + TEXT_TYPE + COMMA_SEP +
                    DbSpeedometer.Events.COLUMN_TIMESTAMP + " datetime default current_timestamp" + ")";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_LIMIT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENTS_TABLE);
        //Default Limit
        sqLiteDatabase.execSQL("INSERT OR IGNORE INTO " + DbSpeedometer.Limit.TABLE_NAME + " VALUES" +
                "('1','5');");

        System.out.println("DB CRATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DbSpeedometer.Limit.TABLE_NAME );
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DbSpeedometer.Events.TABLE_NAME );

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    //SpeedLimit
    public SpeedLimit getSpeedLimit() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + DbSpeedometer.Limit.TABLE_NAME + " WHERE "
                + DbSpeedometer.Limit._ID + " = " + 1;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        SpeedLimit sp = new SpeedLimit();
        sp.setId(c.getInt(c.getColumnIndex(DbSpeedometer.Limit._ID)));
        sp.setSlimit((c.getString(c.getColumnIndex(DbSpeedometer.Limit.COLUMN_SPEEDLIMIT))));

        return sp;
    }

    //EVENTS
    public long insertEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbSpeedometer.Events.COLUMN_LATITUDE, event.getLatitude());
        values.put(DbSpeedometer.Events.COLUMN_LONGTITUDE, event.getLongtitude());
        values.put(DbSpeedometer.Events.COLUMN_SPEED, event.getSpeed());

        long event_id = db.insert(DbSpeedometer.Events.TABLE_NAME, null, values);

        return event_id;
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<Event>();

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + DbSpeedometer.Events.TABLE_NAME;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Event event = new Event();

                event.setId(c.getInt(c.getColumnIndex(DbSpeedometer.Events._ID)));
                event.setLatitude((c.getString(c.getColumnIndex(DbSpeedometer.Events.COLUMN_LATITUDE))));
                event.setLongtitude((c.getString(c.getColumnIndex(DbSpeedometer.Events.COLUMN_LONGTITUDE))));
                event.setSpeed((c.getString(c.getColumnIndex(DbSpeedometer.Events.COLUMN_SPEED))));
                event.setTimestamp((c.getString(c.getColumnIndex(DbSpeedometer.Events.COLUMN_TIMESTAMP))));




                events.add(event);

            } while (c.moveToNext());
        }


        c.close();
//        db.close();
        return events;
    }



    // close the database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


}
