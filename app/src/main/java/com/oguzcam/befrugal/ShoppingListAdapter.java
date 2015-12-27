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

import com.oguzcam.befrugal.R;
import com.oguzcam.befrugal.model.ListContract;

import java.util.Date;

/**
 * {@link ShoppingListAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ShoppingListAdapter extends CursorAdapter {

    private static final String TAG = ShoppingListAdapter.class.getSimpleName();

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.shopping_list_row;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    public ShoppingListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        String listTitle = cursor.getString(ShoppingListFragment.COL_LIST_NAME);
        viewHolder.listTitleView.setText(listTitle);

        double listTotalAmount = cursor.getDouble(ShoppingListFragment.COL_TOTAL_AMOUNT);
        if (listTotalAmount != 0) {
            viewHolder.listTotalAmountView.setText(Utility.asAmount(listTotalAmount));
        } else {
            viewHolder.listTotalAmountView.setText(R.string.not_available);
        }

        final int done = cursor.getInt(ShoppingListFragment.COL_DONE);
        viewHolder.selectCheckBox.setChecked(done == 1);

        viewHolder.currentCursorPosition = cursor.getPosition();


        viewHolder.selectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cursor.moveToPosition(viewHolder.currentCursorPosition);
                long listId = cursor.getLong(ShoppingListFragment.COL_LIST_ID);
                if(isChecked && done != 1){
                    Log.v(TAG, "Checked" + listId);

                    ContentValues values = new ContentValues();
                    values.put(ListContract.ListEntry.COLUMN_DONE, 1);
                    values.put(ListContract.ListEntry.COLUMN_DONE_TIME, new Date().getTime());

                    String where = ListContract.ListEntry._ID + "=?";

                    context.getContentResolver().update(
                            ListContract.ListEntry.CONTENT_URI,
                            values,
                            where,
                            new String[]{Long.toString(listId)}
                            );
                } else if (!isChecked && done == 1) {
                    Log.v(TAG, "Unchecked" + listId);

                    ContentValues values = new ContentValues();
                    values.put(ListContract.ListEntry.COLUMN_DONE, 0);
                    values.put(ListContract.ListEntry.COLUMN_DONE_TIME, (Long) null);

                    String where = ListContract.ListEntry._ID + "=?";

                    context.getContentResolver().update(
                            ListContract.ListEntry.CONTENT_URI,
                            values,
                            where,
                            new String[]{Long.toString(listId)}
                    );
                }
            }
        });
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView listTitleView;
        public final TextView listTotalAmountView;
        public final CheckBox selectCheckBox;
        public int currentCursorPosition;

        public ViewHolder(View view) {
            listTitleView = (TextView) view.findViewById(R.id.shopping_list_title);
            listTotalAmountView = (TextView) view.findViewById(R.id.list_total_amount);
            selectCheckBox = (CheckBox) view.findViewById(R.id.list_select);
        }
    }
}