package com.example.oguzcam.befrugal;

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

/**
 * {@link ListItemListAdapter} exposes a list of weather forecasts
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
    public void bindView(View view, Context context, final Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.curentCursorPosition = cursor.getPosition();

        Log.v(TAG, cursor.getString(ListItemActivityFragment.COL_LIST_ITEM_ID));

        String listTitle = cursor.getString(ListItemActivityFragment.COL_LIST_ITEM_NAME);
        viewHolder.listTitleView.setText(listTitle);
        String listUnitAmount = cursor.getString(ListItemActivityFragment.COL_UNIT_AMOUNT);
        if (listUnitAmount != null && !listUnitAmount.trim().isEmpty()) {
            viewHolder.listUnitAmountView.setText(listUnitAmount);
        } else {
            viewHolder.listUnitAmountView.setText(R.string.not_available);
        }
        String listTotalAmount = cursor.getString(ListItemActivityFragment.COL_TOTAL_AMOUNT);
        if (listTotalAmount != null && !listTotalAmount.trim().isEmpty()) {
            viewHolder.listTotalAmountView.setText(listTotalAmount);
        } else {
            viewHolder.listTotalAmountView.setText(R.string.not_available);
        }

        viewHolder.selectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cursor.moveToPosition(viewHolder.curentCursorPosition);
                if(isChecked){
                    // TODO: Make them done or undone
                    Log.v(TAG, "Checked" + cursor.getLong(ListItemActivityFragment.COL_LIST_ITEM_ID));
                } else {
                    Log.v(TAG, "Unchecked" + cursor.getLong(ListItemActivityFragment.COL_LIST_ITEM_ID));
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
        public int curentCursorPosition;

        public ViewHolder(View view) {
            listTitleView = (TextView) view.findViewById(R.id.list_item_title);
            listUnitAmountView = (TextView) view.findViewById(R.id.list_item_unit_amount);
            listTotalAmountView = (TextView) view.findViewById(R.id.list_item_total_amount);
            selectCheckBox = (CheckBox) view.findViewById(R.id.list_item_select);
        }
    }
}