package com.oguzcam.befrugal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.oguzcam.befrugal.model.ListContract;

import java.math.BigDecimal;

/**
 * Created by oguzcam on 12/27/15.
 */
public class Utility {
    private static final String TAG = Utility.class.getSimpleName();

    // Add difference to given list total amount
    public static void addToListTotalAmount(Context context, long listId, double difference) {
        if (context == null || difference == 0 || listId == 0) {
            return;
        }

        Log.v(TAG, "Difference to add total amount is = " + Double.toString(difference));
        final String where = ListContract.ListEntry._ID + "=?";
        final String[] selectionArgs = new String[]{Long.toString(listId)};
        Cursor cursor = context.getContentResolver().query(
                ListContract.ListEntry.CONTENT_URI,
                new String[]{ListContract.ListEntry.COLUMN_TOTAL_AMOUNT},
                where,
                selectionArgs,
                null
        );
        if(cursor != null && cursor.moveToFirst()) {
            double totalAmount = cursor.getDouble(0);
            Log.v(TAG, "List data taken from db to update total amount = " + totalAmount);
            ContentValues values = new ContentValues();
            values.put(ListContract.ListEntry.COLUMN_TOTAL_AMOUNT, totalAmount + difference);
            context.getContentResolver().update(
                    ListContract.ListEntry.CONTENT_URI,
                    values,
                    where,
                    selectionArgs);
        }
    }

    public static String asAmount(BigDecimal amount) {
        return String.format("%.2f", amount);
    }

    public static String asAmount(Double amount) {
        return String.format("%.2f", amount);
    }

    public static String setItemNames(Cursor data, int fieldPosition) {
        StringBuilder builder = new StringBuilder();
        while (true) {
            builder.append(data.getString(fieldPosition) + ",");
            if (data.isLast()) {
                data.moveToFirst();
                break;
            }
            data.moveToNext();
        }
        return builder.toString();
    }
}
