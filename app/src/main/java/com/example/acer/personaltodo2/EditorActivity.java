package com.example.acer.personaltodo2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import static com.example.acer.personaltodo2.data.TaskContract.*;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText;
    private EditText mDueDateEditText;
    private EditText mNotesEditText;
    private RadioGroup mRadioGroup;
    private RadioButton mInProgressButton;
    private RadioButton mCompletedButton;
    private Spinner mPrioritySpinner;

    private int mPriority = 0;
    private int mStatus = 0;

    private Uri mCurrentTaskUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        mCurrentTaskUri = intent.getData();

        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new pet.
        if (mCurrentTaskUri == null) {
            // This is a new pet, so change the app bar to say "Add a Pet"
            setTitle(getString(R.string.editor_activity_title_new_task));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit Pet"
            setTitle(getString(R.string.editor_activity_title_edit_task));
            getLoaderManager().initLoader(1, null, this);
        }
        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_task_name);
        mDueDateEditText = (EditText) findViewById(R.id.edit_due_date);
        mNotesEditText = (EditText) findViewById(R.id.edit_notes);
        mNameEditText = (EditText) findViewById(R.id.edit_task_name);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mInProgressButton = (RadioButton) findViewById(R.id.radio_in_progress);
        mCompletedButton = (RadioButton) findViewById(R.id.radio_completed);

        //check in-progress radio button by default
        mInProgressButton.setChecked(true);

        mPrioritySpinner = findViewById(R.id.spinner_priority);
        setUpSpinner();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new task, hide the "Delete" menu item.
        if (mCurrentTaskUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_save:
                //check if name field is blank,
                //show error message if blank
                if (TextUtils.isEmpty(mNameEditText.getText())) {
                    mNameEditText.setError("This field cannot be empty");
                    return false;
                }
                //save task to database
                saveTask();
                //exit activity
                finish();
                return true;
            case R.id.action_delete:
                //pop up confirmation dialog for deletion
                deleteTask();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteTask();
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

    private void deleteTask() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentTaskUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentTaskUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_task_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_task_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    private void saveTask() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String dueDateString = mDueDateEditText.getText().toString().trim();
        String noteString = mNotesEditText.getText().toString().trim();

        radioButtonChecked();

        //Creates a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_TASK_NAME, nameString);
        values.put(TaskEntry.COLUMN_TASK_DUE_DATE, dueDateString);
        values.put(TaskEntry.COLUMN_TASK_NOTES, noteString);
        values.put(TaskEntry.COLUMN_TASK_PRIORITY, mPriority);
        values.put(TaskEntry.COLUMN_TASK_STATUS, mStatus);

        //check if name field is null while adding new task,
        //return to home screen if so
        if (mCurrentTaskUri == null &&
                TextUtils.isEmpty(nameString)) {
            return;
        }

        if (mCurrentTaskUri == null) {
            //Insert a new row in the database, returning the content URI for the new task.
            Uri newUri = getContentResolver().insert(TaskEntry.CONTENT_URI, values);
            //show toast message to let user know if data insertion was successful
            //-1 means there was an error
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_task_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_task_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsUpdated = getContentResolver().update(mCurrentTaskUri, values, null, null);
            if (rowsUpdated == 0) {
                Toast.makeText(this, getString(R.string.editor_update_task_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_task_successful),
                        Toast.LENGTH_SHORT).show();
            }

        }


    }


    private void radioButtonChecked() {

        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_completed:
                mStatus = 1;
                break;
            default:
                mStatus = 0;
                break;
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setUpSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter prioritySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_priority_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        prioritySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mPrioritySpinner.setAdapter(prioritySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.priority_medium))) {
                        mPriority = TaskEntry.PRIORITY_MEDIUM;
                    } else if (selection.equals(getString(R.string.priority_high))) {
                        mPriority = TaskEntry.PRIORITY_HIGH;
                    } else if (selection.equals(getString(R.string.priority_critical))) {
                        mPriority = TaskEntry.PRIORITY_CRITICAL;
                    } else {
                        mPriority = TaskEntry.PRIORITY_LOW;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPriority = TaskEntry.PRIORITY_LOW;
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all task attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                TaskEntry._ID,
                TaskEntry.COLUMN_TASK_NAME,
                TaskEntry.COLUMN_TASK_DUE_DATE,
                TaskEntry.COLUMN_TASK_PRIORITY,
                TaskEntry.COLUMN_TASK_STATUS,
                TaskEntry.COLUMN_TASK_NOTES};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentTaskUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {

            String name = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_TASK_NAME));
            String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_TASK_DUE_DATE));
            String notes = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_TASK_NOTES));
            int priority = cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_TASK_PRIORITY));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_TASK_STATUS));

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mDueDateEditText.setText(dueDate);
            mNotesEditText.setText(notes);

            switch (priority) {
                case TaskEntry.PRIORITY_MEDIUM:
                    mPrioritySpinner.setSelection(TaskEntry.PRIORITY_MEDIUM);
                    break;
                case TaskEntry.PRIORITY_HIGH:
                    mPrioritySpinner.setSelection(TaskEntry.PRIORITY_HIGH);
                    break;
                case TaskEntry.PRIORITY_CRITICAL:
                    mPrioritySpinner.setSelection(TaskEntry.PRIORITY_CRITICAL);
                    break;
                default:
                    mPrioritySpinner.setSelection(TaskEntry.PRIORITY_LOW);
                    break;
            }

            switch (status) {
                case TaskEntry.STATUS_COMPLETE:
                    mCompletedButton.setChecked(true);
                    break;
                default:
                    mInProgressButton.setChecked(true);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mDueDateEditText.setText("");
        mNotesEditText.setText("");
        mPrioritySpinner.setSelection(TaskEntry.PRIORITY_LOW);
        mInProgressButton.setChecked(true);
    }
}