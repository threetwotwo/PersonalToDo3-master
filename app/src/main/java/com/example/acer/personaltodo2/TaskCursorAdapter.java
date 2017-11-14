package com.example.acer.personaltodo2;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.acer.personaltodo2.data.TaskContract;

import static android.widget.AdapterView.*;

/**
 * This CustomCursorAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class TaskCursorAdapter extends RecyclerView.Adapter<TaskCursorAdapter.TaskViewHolder> {


    // Class variables for the Cursor that holds task data and the Context
    private Context mContext;
    private Cursor mCursor;
    private int mId;
    final private ListItemClickListener mListener;

    @Override
    public long getItemId(int position) {
        return mCursor.getPosition();
    }

    public interface ListItemClickListener {
        void onListItemClicked(long clickedItemIndex);
    }

    /**
     * Constructor for the CustomCursorAdapter that initializes the Context.
     *
     * @param mContext the current Context
     */
    public TaskCursorAdapter(Context mContext, ListItemClickListener mListener) {
        //we could write this as mContext = context instead (it's the same!)
        this.mContext = mContext;
        this.mListener = mListener;
    }


    //Inner class for creating viewholders
    class TaskViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        //class variables
        TextView tvName,
                tvPriority,
                tvDueDate,
                tvStatus;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public TaskViewHolder(View itemView) {
            super(itemView);

            // Find fields to populate in inflated template
            tvName = itemView.findViewById(R.id.task);
            tvPriority = itemView.findViewById(R.id.priority_level);
            tvDueDate = itemView.findViewById(R.id.due_date);
            tvStatus = itemView.findViewById(R.id.status);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            mListener.onListItemClicked(getLayoutPosition());
        }
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    public TaskViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);

        return new TaskViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {

        mCursor.moveToPosition(position);


        // Extract properties from cursor
        String name = mCursor.getString(mCursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TASK_NAME));
        int priority = mCursor.getInt(mCursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TASK_PRIORITY));
        String dueDate = mCursor.getString(mCursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TASK_DUE_DATE));
        int status = mCursor.getInt(mCursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TASK_STATUS));

        //Get string values of priority and status
        switch (priority) {
            case TaskContract.TaskEntry.PRIORITY_MEDIUM:
                holder.tvPriority.setText(R.string.priority_medium);
                break;
            case TaskContract.TaskEntry.PRIORITY_HIGH:
                holder.tvPriority.setText(R.string.priority_high);
                break;
            case TaskContract.TaskEntry.PRIORITY_CRITICAL:
                holder.tvPriority.setText(R.string.priority_critical);
                break;
            default:
                holder.tvPriority.setText(R.string.priority_low);
                break;
        }

        switch (status) {
            case TaskContract.TaskEntry.STATUS_COMPLETE:
                holder.tvStatus.setText(R.string.button_completed);
                break;
            default:
                holder.tvStatus.setText(R.string.button_in_progress);
                break;
        }

        // Populate fields with extracted properties
        holder.tvName.setText(name);
        holder.tvDueDate.setText(dueDate);

    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        } else return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

}