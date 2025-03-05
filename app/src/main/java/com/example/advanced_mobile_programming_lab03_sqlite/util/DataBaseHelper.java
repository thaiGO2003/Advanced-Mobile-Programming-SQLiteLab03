package com.example.advanced_mobile_programming_lab03_sqlite.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.advanced_mobile_programming_lab03_sqlite.adapter.UsersDatabaseAdapter;

import static com.example.advanced_mobile_programming_lab03_sqlite.adapter.UsersDatabaseAdapter.TABLE_NAME;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(UsersDatabaseAdapter.DATABASE_CREATE);
        } catch (Exception er) {
            Log.e("Error", "exception");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS USERS"); // Xóa bảng USERS nếu tồn tại
        onCreate(db);
    }
}