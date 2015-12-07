package com.example.oguzcam.befrugal;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link ShoppingListAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ShoppingListAdapter extends CursorAdapter {

    // More than one view type
//    private static final int VIEW_TYPE_TODAY = 0;
//    private static final int VIEW_TYPE_FUTURE_DAY = 1;
//    private static final int VIEW_TYPE_COUNT = 2;
//
//    @Override
//    public int getItemViewType(int position) {
//        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return VIEW_TYPE_COUNT;
//    }

    /**
     * Copy/paste note: Replace existing newView() method in ShoppingListAdapter with this one.
     */
//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        // Choose the layout type
//        int viewType = getItemViewType(cursor.getPosition());
//        int layoutId = -1;
//
//        if(viewType == VIEW_TYPE_TODAY) {
//            layoutId = R.layout.list_item_forecast_today;
//        } else {
//            layoutId = R.layout.list_item_forecast;
//        }
//
//        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
//        ViewHolder viewHolder = new ViewHolder(view);
//        view.setTag(viewHolder);
//        return view;
//    }

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
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String listTitle = cursor.getString(ShoppingListFragment.COL_LIST_NAME);
        TextView listTitleView = viewHolder.listTitleView;
        listTitleView.setText(listTitle);
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView listTitleView;

        public ViewHolder(View view) {
            listTitleView = (TextView) view.findViewById(R.id.shopping_list_title);
        }
    }
}