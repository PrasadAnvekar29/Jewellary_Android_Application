package com.example.vadi.jewellay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vadi on 28/11/2017.
 */

public class Tabels extends SQLiteOpenHelper {


    private SQLiteDatabase mDb;
    public Tabels(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table purchasetabel(_id integer primary key,purchasename text,purchaseaddress text)");
        sqLiteDatabase.execSQL("create table custumerstabel(_id integer primary key,custumername text, custumeraddress text )");
        sqLiteDatabase.execSQL("create table karegarstabel(_id integer primary key,karegarname text,karegaraddress text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }


    public String[] getAllCostomersname()
    {
        try {

            String arrData[] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data
            String strSQL = "SELECT  custumername FROM custumerstabel";
            Cursor cursor = db.rawQuery(strSQL, null);

            if(cursor != null)
            {
                if (cursor.moveToFirst()) {
                    arrData = new String[cursor.getCount()];
                    int i= 0;
                    do {
                        arrData[i] = cursor.getString(0);
                        i++;
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            return arrData;
        } catch (Exception e) {
            return null;
        }
    }



}
