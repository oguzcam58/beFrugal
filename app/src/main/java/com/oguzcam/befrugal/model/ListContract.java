package com.oguzcam.befrugal.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for shopping list.
 */
public class ListContract {
    public static final String CONTENT_AUTHORITY = "com.oguzcam.befrugal.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_LIST = "list";
    public static final String PATH_LIST_ITEM = "list_item";
    public static final String PATH_LIST_ITEM_BY_LIST_ID = "list_item_by_list_id";

    /* Inner class that defines the table contents of the list table */
    public static final class ListEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIST).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIST;

        // Table name
        public static final String TABLE_NAME = PATH_LIST;

        public static final String COLUMN_LIST_NAME = "list_name";
        public static final String COLUMN_CREATION_TIME = "list_creation_time";
        public static final String COLUMN_LAST_UPDATED_TIME = "list_last_updated_time";
        public static final String COLUMN_DONE_TIME = "list_done_time";
        public static final String COLUMN_TOTAL_AMOUNT = "list_total_amount";
        public static final String COLUMN_DONE = "list_done";


        public static Uri buildListUri() {
            return CONTENT_URI;
        }
    }

    /* Inner class that defines the table contents of the list_detail table */
    public static final class ListItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIST_ITEM).build();

        public static final Uri CONTENT_URI_BY_LIST_ID =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIST_ITEM_BY_LIST_ID).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIST_ITEM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIST_ITEM;

        public static final String TABLE_NAME = PATH_LIST_ITEM;

        // Column with the foreign key from the list table.
        public static final String COLUMN_LIST_ID = "list_id";

        public static final String COLUMN_LIST_ITEM_NAME = "item_name";
        // Date, stored as long in milliseconds
        public static final String COLUMN_CREATION_TIME = "item_creation_time";
        public static final String COLUMN_LAST_UPDATED_TIME = "item_last_updated_time";
        public static final String COLUMN_DONE_TIME = "item_done_time";
        public static final String COLUMN_QUANTITY = "item_total_qty";
        public static final String COLUMN_TOTAL_AMOUNT = "item_total_amount";
        public static final String COLUMN_UNIT_TYPE = "item_unit_type";
        public static final String COLUMN_UNIT_AMOUNT = "item_unit_amount";
        public static final String COLUMN_DONE = "item_done";

        public static Uri buildListItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildListItemWithListId(long listItemId) {
            return CONTENT_URI_BY_LIST_ID.buildUpon().appendPath(Long.toString(listItemId)).build();
        }

        public static long getListIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }
}
