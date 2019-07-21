package com.mariospatsis.speedometer;

import android.provider.BaseColumns;

public class DbSpeedometer {
    private DbSpeedometer(){}

    public static class Limit implements BaseColumns {
        public static final String TABLE_NAME = "slimit";
        public static final String COLUMN_SPEEDLIMIT = "speedlimit";
    }

    public static class Events implements BaseColumns{
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGTITUDE = "longtitude";
        public static final String COLUMN_SPEED = "speed";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
