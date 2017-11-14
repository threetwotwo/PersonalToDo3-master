package com.example.acer.personaltodo2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import static com.example.acer.personaltodo2.data.TaskContract.*;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,TaskCursorAdapter.ListItemClickListener {

    //Member variables
    private TaskCursorAdapter mCursorAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        setTitle(getString(R.string.title_main_screen));

        //set click listener on FAB to start EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


        // Find the ListView which will be populated with the pet data
        mRecyclerView = findViewById(R.id.rv_tasks);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        // taskRecyclerView.setEmptyView(findViewById(R.id.empty_view));

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no task data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new TaskCursorAdapter(this,this);
        mRecyclerView.setAdapter(mCursorAdapter);


        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(1, null, this);

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertTask();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteAllConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_alert_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteAllPets();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(TaskEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    private void insertTask() {

        //Create a new map of values, where column names are keys
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_TASK_NAME, "Watch all Futurama episodes all over again");
        values.put(TaskEntry.COLUMN_TASK_DUE_DATE, "10 Aug 2018");
        values.put(TaskEntry.COLUMN_TASK_PRIORITY, 3);
        values.put(TaskEntry.COLUMN_TASK_STATUS, 1);
        values.put(TaskEntry.COLUMN_TASK_NOTES, "Planning on something big next week, but not this week");

        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(TaskEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = new String[]{
                TaskEntry._ID,
                TaskEntry.COLUMN_TASK_NAME,
                TaskEntry.COLUMN_TASK_DUE_DATE,
                TaskEntry.COLUMN_TASK_PRIORITY,
                TaskEntry.COLUMN_TASK_STATUS,
                TaskEntry.COLUMN_TASK_NOTES,
        };
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                TaskEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
// Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }


    @Override
    public void onListItemClick(int id) {
        // Create new intent to go to {@link EditorActivity}
        Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

        // Form the content URI that represents the specific pet that was clicked on,
        // by appending the "id" (passed as input to this method) onto the
        // {@link PetEntry#CONTENT_URI}.
        // For example, the URI would be "content://com.example.android.pets/pets/2"
        // if the pet with ID 2 was clicked on.
        Log.e("I AM THE ONE YOU SEEK",""+ ContentUris.withAppendedId(TaskEntry.CONTENT_URI, id));

        Uri currentPetUri = ContentUris.withAppendedId(TaskEntry.CONTENT_URI, id);

        // Set the URI on the data field of the intent
        intent.setData(currentPetUri);

        // Launch the {@link EditorActivity} to display the data for the current pet.
        startActivity(intent);
    }
}
