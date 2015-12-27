package com.oguzcam.befrugal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oguzcam.befrugal.model.ListContract;

import java.util.Date;

/**
 * {@link ListItemListAdapter} exposes a list of items
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class ListItemListAdapter extends CursorAdapter {

    private static final String TAG = ListItemListAdapter.class.getSimpleName();

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.list_item_list_row;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    public ListItemListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.currentCursorPosition = cursor.getPosition();

        String listTitle = cursor.getString(ListItemActivityFragment.COL_LIST_ITEM_NAME);
        viewHolder.listTitleView.setText(listTitle);

        Double listUnitAmount = cursor.getDouble(ListItemActivityFragment.COL_UNIT_AMOUNT);
        if (listUnitAmount > 0) {
            viewHolder.listUnitAmountView.setText(Double.toString(listUnitAmount));
        } else {
            viewHolder.listUnitAmountView.setText(R.string.not_available);
        }

        Double listTotalAmount = cursor.getDouble(ListItemActivityFragment.COL_TOTAL_AMOUNT);
        if (listTotalAmount > 0) {
            viewHolder.listTotalAmountView.setText(Double.toString(listTotalAmount));
        } else {
            viewHolder.listTotalAmountView.setText(R.string.not_available);
        }

        if (cursor.getLong(ListItemActivityFragment.COL_DONE) > 0) {
            viewHolder.selectCheckBox.setChecked(true);
        } else {
            viewHolder.selectCheckBox.setChecked(false);
        }
        viewHolder.isLoaded = true;

        viewHolder.selectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!viewHolder.isLoaded || cursor.isClosed()) {
                    return;
                }

                cursor.moveToPosition(viewHolder.currentCursorPosition);
                final String itemId = cursor.getString(ListItemActivityFragment.COL_LIST_ITEM_ID);
                final long listId = cursor.getLong(ListItemActivityFragment.COL_LIST_ID);
                final long done = cursor.getLong(ListItemActivityFragment.COL_DONE);
                final double totalAmount = cursor.getDouble(ListItemActivityFragment.COL_TOTAL_AMOUNT);

                final String where = ListContract.ListItemEntry._ID + "=?";

                boolean isDone = done == 1;

                if(isChecked && !isDone){
                    ContentValues values = new ContentValues();
                    values.put(ListContract.ListItemEntry.COLUMN_DONE, 1);
                    values.put(ListContract.ListItemEntry.COLUMN_DONE_TIME, new Date().getTime());

                    context.getContentResolver().update(
                            ListContract.ListItemEntry.CONTENT_URI,
                            values,
                            where,
                            new String[]{itemId});

                    Utility.addToListTotalAmount(context, listId, totalAmount);

                    Log.v(TAG, itemId + " is converted to done.");
                } else if (!isChecked && isDone) {
                    ContentValues values = new ContentValues();
                    values.put(ListContract.ListItemEntry.COLUMN_DONE, 0);
                    values.put(ListContract.ListItemEntry.COLUMN_DONE_TIME, (Long) null);

                    context.getContentResolver().update(
                            ListContract.ListItemEntry.CONTENT_URI,
                            values,
                            where,
                            new String[]{itemId});

                    Utility.addToListTotalAmount(context, listId, totalAmount * -1);

                    Log.v(TAG, itemId + " is converted to not done.");
                }
            }
        });
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView listTitleView;
        public final TextView listUnitAmountView;
        public final TextView listTotalAmountView;
        public final CheckBox selectCheckBox;
        public int currentCursorPosition;
        public boolean isLoaded;

        public ViewHolder(View view) {
            listTitleView = (TextView) view.findViewById(R.id.list_item_title);
            listUnitAmountView = (TextView) view.findViewById(R.id.list_item_unit_amount);
            listTotalAmountView = (TextView) view.findViewById(R.id.list_item_total_amount);
            selectCheckBox = (CheckBox) view.findViewById(R.id.list_item_select);
        }
    }
}