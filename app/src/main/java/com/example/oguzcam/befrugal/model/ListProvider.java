package com.example.oguzcam.befrugal.model;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ListProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ListDbHelper mOpenHelper;

    static final int LIST = 100;
    static final int LIST_ITEM = 300;
    static final int LIST_ITEM_WITH_LIST_ID = 301;

    private static final SQLiteQueryBuilder sListItemWithListQueryBuilder;

    static{
        sListItemWithListQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //list_item INNER JOIN list ON list_item.list_id = list._id
        sListItemWithListQueryBuilder.setTables(
                ListContract.ListItemEntry.TABLE_NAME + " INNER JOIN " +
                        ListContract.ListEntry.TABLE_NAME +
                        " ON " + ListContract.ListItemEntry.TABLE_NAME +
                        "." + ListContract.ListItemEntry.COLUMN_LIST_ID +
                        " = " + ListContract.ListEntry.TABLE_NAME +
                        "." + ListContract.ListEntry._ID);
    }

    //list_item.list_id = ?
    private static final String sListIdSelection =
            ListContract.ListItemEntry.TABLE_NAME +
                    "." + ListContract.ListItemEntry.COLUMN_LIST_ID + " = ? ";

    private Cursor getListItemsByListItem(Uri uri, String[] projection, String sortOrder) {
        long listId = ListContract.ListItemEntry.getListIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sListIdSelection;
        selectionArgs = new String[]{Long.toString(listId)};

        return sListItemWithListQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ListContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ListContract.PATH_LIST, LIST);
        matcher.addURI(authority, ListContract.PATH_LIST_ITEM, LIST_ITEM);

        matcher.addURI(authority, ListContract.PATH_LIST_ITEM + "/#", LIST_ITEM_WITH_LIST_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ListDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case LIST_ITEM_WITH_LIST_ID:
                return ListContract.ListItemEntry.CONTENT_ITEM_TYPE;
            case LIST_ITEM:
                return ListContract.ListItemEntry.CONTENT_TYPE;
            case LIST:
                return ListContract.ListEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case LIST_ITEM_WITH_LIST_ID:
            {
                retCursor = getListItemsByListItem(uri, projection, sortOrder);
                break;
            }
            // "list_item"
            case LIST_ITEM: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ListContract.ListItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "list"
            case LIST: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ListContract.ListEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case LIST_ITEM: {
                long _id = db.insert(ListContract.ListItemEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ListContract.ListItemEntry.buildListItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LIST: {
                long _id = db.insert(ListContract.ListEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ListContract.ListEntry.buildListUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted = 0;

        final int match = sUriMatcher.match(uri);

        if( selection == null) {
            selection = "1";
        }
        switch (match) {
            case LIST_ITEM: {
                rowsDeleted = db.delete(ListContract.ListItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case LIST: {
                rowsDeleted = db.delete(ListContract.ListEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated = 0;

        // Student: Use the uriMatcher to match the WEATHER and LOCATION URI's we are going to
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LIST_ITEM: {
                rowsUpdated = db.update(ListContract.ListItemEntry.TABLE_NAME, values, selection, selectionArgs);
                if (rowsUpdated < 1 ) {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case LIST: {
                rowsUpdated = db.update(ListContract.ListEntry.TABLE_NAME, values, selection, selectionArgs);
                if ( rowsUpdated < 1 ) {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    // This is a method specifically to assist the testing framework in running smoothly.
    // More can be seen at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}