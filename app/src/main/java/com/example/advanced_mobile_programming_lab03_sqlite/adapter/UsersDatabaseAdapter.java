package com.example.advanced_mobile_programming_lab03_sqlite.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.advanced_mobile_programming_lab03_sqlite.model.UserModel;
import com.example.advanced_mobile_programming_lab03_sqlite.util.DataBaseHelper;

import org.json.JSONException;

import java.util.ArrayList;

public class UsersDatabaseAdapter {

    static ArrayList<UserModel> users = new ArrayList<>();

    static final String DATABASE_NAME = "UsersDatabase.db";
    static final String TABLE_NAME = "USERS";
    static final int DATABASE_VERSION = 1;

    // Câu lệnh SQL tạo mới cơ sở dữ liệu.
    public static final String DATABASE_CREATE =
            "CREATE TABLE USERS (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_name TEXT, " +
                    "user_email TEXT, " +
                    "password TEXT, " +
                    "full_name TEXT, " +
                    "image_blob BLOB);"; // Thêm cột image_blob

    private static final String TAG = "UsersDatabaseAdapter";

    // Khai báo biến db kiểu SQLiteDatabase để thực thi các phương thức với cơ sở dữ liệu
    public static SQLiteDatabase db;

    // Khai báo đối tượng kiểu Context của ứng dụng sử dụng cơ sở dữ liệu này.
    private final Context context;

    // Database open/upgrade helper
    private static DataBaseHelper dbHelper;

    // Instance duy nhất của lớp UsersDatabaseAdapter
    private static UsersDatabaseAdapter instance;

    // Constructor private để ngăn chặn việc khởi tạo từ bên ngoài
    private UsersDatabaseAdapter(Context context) {
        this.context = context;
        dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Phương thức static để lấy instance duy nhất
    public static UsersDatabaseAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new UsersDatabaseAdapter(context);
        }
        return instance;
    }

    // Phương thức mở Database
    public UsersDatabaseAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    // Phương thức đóng Database
    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    // Phương thức trả về instance của Database
    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    // Phương thức insert bản ghi vào Table
    public long insertEntry(String userName, String userEmail, String password, String fullName, byte[] imageBlob) {
        try {
            ContentValues newValues = new ContentValues();
            // Gán dữ liệu cho mỗi cột
            newValues.put("user_name", userName);
            newValues.put("user_email", userEmail);
            newValues.put("password", password);
            newValues.put("full_name", fullName);
            if (imageBlob != null && imageBlob.length > 0) {
                newValues.put("image_blob", imageBlob); // Lưu ảnh nếu có
                Log.d(TAG, "Image Blob Size: " + imageBlob.length);
            } else {
                Log.w(TAG, "No image provided, saving as NULL.");
            }

            // Chèn dữ liệu vào bảng
            long result = db.insert(TABLE_NAME, null, newValues);
            Log.i("Row Insert Result", "Inserted row ID: " + result);
            Toast.makeText(context.getApplicationContext(), "User Info Saved! Total Row Count is " + getRowCount(), Toast.LENGTH_SHORT).show();
            return result;
        } catch (Exception ex) {
            Log.e(TAG, "Error inserting entry", ex);
            Toast.makeText(context.getApplicationContext(), "Error saving user info", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }


    // Phương thức lấy số lượng bản ghi trong bảng
    public int getRowCount() {
        if (db != null) {
            try {
                return (int) android.database.DatabaseUtils.queryNumEntries(db, TABLE_NAME);
            } catch (Exception e) {
                Log.e(TAG, "Error getting row count", e);
            }
        }
        return 0;
    }

    // Phương thức lấy tất cả các hàng được lưu trong Table
    public ArrayList<UserModel> getRows() throws JSONException {
        users.clear();
        UserModel user;
        SQLiteDatabase readableDb = dbHelper.getReadableDatabase();
        Cursor projCursor = readableDb.query(TABLE_NAME, null, null, null, null, null, null);

        while (projCursor.moveToNext()) {
            int id = projCursor.getInt(projCursor.getColumnIndex("ID"));
            String userName = projCursor.getString(projCursor.getColumnIndex("user_name"));
            String userEmail = projCursor.getString(projCursor.getColumnIndex("user_email"));
            String password = projCursor.getString(projCursor.getColumnIndex("password"));
            String fullName = projCursor.getString(projCursor.getColumnIndex("full_name"));

            // Kiểm tra nếu cột image_blob tồn tại
            int imageBlobIndex = projCursor.getColumnIndex("image_blob");
            byte[] imageBlob = null;
            if (imageBlobIndex != -1) {
                imageBlob = projCursor.getBlob(imageBlobIndex);
            }

            user = new UserModel(id,userName, password, userEmail, fullName, imageBlob);
            users.add(user);
        }
        projCursor.close();
        return users;
    }



    // Phương thức xoá bản ghi trong Table sử dụng khoá chính là ID
    public int deleteEntry(int ID) {
        String where = "ID=?";
        int numberOfEntriesDeleted = db.delete(TABLE_NAME, where, new String[]{ID+""});
        Toast.makeText(context.getApplicationContext(), "Number fo Entry Deleted Successfully: " + numberOfEntriesDeleted, Toast.LENGTH_SHORT).show();
        return numberOfEntriesDeleted;
    }

    // Phương thức xoá tất cả các bản ghi trong bảng Table
    public void truncateTable() {
        dbHelper.getReadableDatabase();
        db.delete(TABLE_NAME, "1", null);
        Toast.makeText(context.getApplicationContext(), "Table Data Truncated!", Toast.LENGTH_LONG).show();
    }

    public UserModel getUserById(int userId) {
        UserModel user = null;
        SQLiteDatabase readableDb = dbHelper.getReadableDatabase();

        Log.d("DEBUG", "Fetching user with ID: " + userId);

        Cursor cursor = readableDb.query(TABLE_NAME, null, "ID=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null) {
            Log.d("DEBUG", "Cursor initialized. Count: " + cursor.getCount());
        }

        if (cursor != null && cursor.moveToFirst()) {
            Log.d("DEBUG", "User found. Reading data...");

            int userNameIndex = cursor.getColumnIndex("user_name");
            int userEmailIndex = cursor.getColumnIndex("user_email");
            int passwordIndex = cursor.getColumnIndex("password");
            int fullNameIndex = cursor.getColumnIndex("full_name");
            int imageBlobIndex = cursor.getColumnIndex("image_blob");

            Log.d("DEBUG", "Column Indexes - user_name: " + userNameIndex + ", user_email: " + userEmailIndex);

            String userName = cursor.getString(userNameIndex);
            String userEmail = cursor.getString(userEmailIndex);
            String password = cursor.getString(passwordIndex);
            String fullName = cursor.getString(fullNameIndex);
            byte[] imageBlob = cursor.getBlob(imageBlobIndex);

            Log.d("DEBUG", "User Data - Name: " + userName + ", Email: " + userEmail);

            if (imageBlob != null) {
                Log.d("DEBUG", "User has profile image. Blob size: " + imageBlob.length);
            } else {
                Log.d("DEBUG", "User has no profile image.");
            }

            user = new UserModel(0,userName, password, userEmail, fullName, imageBlob);
        } else {
            Log.e("DEBUG", "No user found with ID: " + userId);
        }

        if (cursor != null) {
            cursor.close();
        }

        return user;
    }


    public int  updateEntry(int ID, String Username, String Useremail, String password, String fullName, byte[] imageBlob) {
        try {
            ContentValues updatedValues = new ContentValues();
            updatedValues.put("user_name", Username);
            updatedValues.put("user_email", Useremail);
            updatedValues.put("password", password);
            updatedValues.put("full_name", fullName);
            if (imageBlob != null) {
                updatedValues.put("image_blob", imageBlob);
            }

            String where = "ID = ?";
            db.update(TABLE_NAME, updatedValues, where, new String[]{String.valueOf(ID)});
            return 1;
        } catch (Exception ex) {
            Log.e(TAG, "Error updating entry", ex);
            return 0;
        }
    }
    public ArrayList<UserModel> searchUsers(String keyword) {
        ArrayList<UserModel> searchResults = new ArrayList<>();
        SQLiteDatabase readableDb = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                "user_name LIKE ? OR full_name LIKE ? OR user_email LIKE ?";

        String searchPattern = "%" + keyword + "%";
        Cursor cursor = readableDb.rawQuery(query, new String[]{searchPattern, searchPattern, searchPattern});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                String userName = cursor.getString(cursor.getColumnIndex("user_name"));
                String userEmail = cursor.getString(cursor.getColumnIndex("user_email"));
                String password = cursor.getString(cursor.getColumnIndex("password"));
                String fullName = cursor.getString(cursor.getColumnIndex("full_name"));

                int imageBlobIndex = cursor.getColumnIndex("image_blob");
                byte[] imageBlob = imageBlobIndex != -1 ? cursor.getBlob(imageBlobIndex) : null;

                UserModel user = new UserModel(id, userName, password, userEmail, fullName, imageBlob);
                searchResults.add(user);
            }
            cursor.close();
        }

        return searchResults;
    }


}