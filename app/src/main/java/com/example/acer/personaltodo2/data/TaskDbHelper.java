package com.example.acer.personaltodo2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.acer.personaltodo2.data.TaskContract.*;

/**
 * A helper class to manage database creation and version management.
 * This class makes it easy for ContentProvider implementations to defer opening and upgrading
 * the database until first use, to avoid blocking application startup with long-running databaseupgrades.
 */

public class TaskDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "personaltodo.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link TaskDbHelper}.
     *
     * @param context of the app
     */
    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_TASKS_TABLE = "CREATE TABLE " + TaskEntry.TABLE_NAME + " ("
                + TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TaskEntry.COLUMN_TASK_NAME + " TEXT NOT NULL, "
                + TaskEntry.COLUMN_TASK_DUE_DATE + " TEXT, "
                + TaskEntry.COLUMN_TASK_PRIORITY + " INTEGER NOT NULL DEFAULT 0, "
                + TaskEntry.COLUMN_TASK_STATUS + " INTEGER NOT NULL DEFAULT 0,"
                + TaskEntry.COLUMN_TASK_NOTES + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_TASKS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
