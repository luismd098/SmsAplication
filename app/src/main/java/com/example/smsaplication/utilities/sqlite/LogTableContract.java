package com.example.smsaplication.utilities.sqlite;

import android.provider.BaseColumns;

public final class LogTableContract {

    private LogTableContract () {}

    public static class LogEntry implements BaseColumns {
        public static final String TABLE_NAME = "logs";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_DATE = "date";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + "_ID INTEGER PRIMARY KEY," +
                        COLUMN_NAME_STATUS + " TEXT," +
                        COLUMN_NAME_MESSAGE + " TEXT," +
                        COLUMN_NAME_DATE + " TEXT)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
