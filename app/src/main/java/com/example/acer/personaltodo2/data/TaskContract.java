package com.example.acer.personaltodo2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Acer on 11/2/2017.
 */

public final class TaskContract {

    //empty constructor
    private TaskContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.acer.personaltodo2";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    public static final String PATH_TASK = "task";

    /**
     * Inner class that defines constant values for the task database table.
     * Each entry in the table represents a single task.
     */
    public static final class TaskEntry implements BaseColumns {
        /**
         * The content URI to access the task data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TASK);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of tasks.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASK;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single task.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASK;

        /**
         * Name of database table for tasks
         */
        public final static String TABLE_NAME = "tasks";

        /**
         * Unique ID number for the task (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the task.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_TASK_NAME = "name";
        public final static String COLUMN_TASK_DUE_DATE = "due_date";
        public final static String COLUMN_TASK_PRIORITY = "priority";
        public final static String COLUMN_TASK_STATUS = "status";
        public final static String COLUMN_TASK_NOTES = "notes";

        /**
         * possible values for critical
         */
                public static final int PRIORITY_LOW = 0;
        public static final int PRIORITY_MEDIUM = 1;
        public static final int PRIORITY_HIGH = 2;
        public static final int PRIORITY_CRITICAL = 3;

        /**
         * possible values for STATUS
         */
        public static final int STATUS_NOT_COMPLETE = 0;
        public static final int STATUS_COMPLETE = 1;

    }
}
