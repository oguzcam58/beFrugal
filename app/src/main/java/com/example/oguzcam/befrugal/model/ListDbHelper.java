/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.oguzcam.befrugal.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.oguzcam.befrugal.model.ListContract.ListEntry;
import com.example.oguzcam.befrugal.model.ListContract.ListItemEntry;

/**
 * Manages a local database for weather data.
 */
public class ListDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "befrugal.db";

    public ListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_LIST_TABLE = "CREATE TABLE " + ListEntry.TABLE_NAME + " (" +
                ListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                ListEntry.COLUMN_LIST_NAME + " TEXT NOT NULL, " +
                ListEntry.COLUMN_CREATION_DATE + " INTEGER NOT NULL, " +
                ListEntry.COLUMN_DONE + " INTEGER NOT NULL DEFAULT 0);";
        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE);

        final String SQL_CREATE_LIST_ITEM_TABLE = "CREATE TABLE " + ListItemEntry.TABLE_NAME + " (" +
                ListItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // Foreign key
                ListItemEntry.COLUMN_LIST_ID + " INTEGER NOT NULL, " +
                // Other Columns
                ListItemEntry.COLUMN_LIST_ITEM_NAME + " TEXT NOT NULL, " +
                ListItemEntry.COLUMN_CREATION_DATE + " INTEGER NOT NULL, " +
                ListItemEntry.COLUMN_QUANTITY + " REAL, " +
                ListItemEntry.COLUMN_TOTAL_AMOUNT + " REAL, " +
                ListItemEntry.COLUMN_UNIT_TYPE + " TEXT, " +
                ListItemEntry.COLUMN_UNIT_AMOUNT + " REAL, " +
                ListItemEntry.COLUMN_DONE + " INTEGER NOT NULL DEFAULT 0, " +
                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + ListItemEntry.COLUMN_LIST_ID + ") REFERENCES " +
                ListEntry.TABLE_NAME + " (" + ListEntry._ID + "));";

        sqLiteDatabase.execSQL(SQL_CREATE_LIST_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // TODO: Do not drop exists table. Just for debugging
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ListEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ListItemEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
