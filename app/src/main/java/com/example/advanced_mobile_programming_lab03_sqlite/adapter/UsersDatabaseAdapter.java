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
    public static final String DATABASE_CREATE = "create table USERS (ID integer primary key autoincrement, user_name text, user_email text, password text, full_name text);";

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
    public long insertEntry(String userName, String userEmail, String password, String fullName) {
        try {
            ContentValues newValues = new ContentValues();
            // Gán dữ liệu cho mỗi cột.
            newValues.put("user_name", userName);
            newValues.put("user_email", userEmail);
            newValues.put("password", password); // Thêm password
            newValues.put("full_name", fullName); // Thêm fullName
            // Insert hàng dữ liệu vào table
            long result = db.insert(TABLE_NAME, null, newValues);
            Log.i("Row Insert Result ", String.valueOf(result));
            Toast.makeText(context.getApplicationContext(), "User Info Saved! Total Row Count is " + getRowCount(), Toast.LENGTH_SHORT).show();
            return result;
        } catch (Exception ex) {
            Log.e(TAG, "Error inserting entry", ex); // Ghi log lỗi chi tiết
            Toast.makeText(context.getApplicationContext(), "Error saving user info", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi cho người dùng
            return -1; // Trả về -1 để biểu thị lỗi
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
        SQLiteDatabase readableDb = dbHelper.getReadableDatabase(); // Lấy cơ sở dữ liệu ở chế độ đọc
        Cursor projCursor = readableDb.query(TABLE_NAME, null, null, null, null, null, null);
        while (projCursor.moveToNext()) {
            user = new UserModel();
            user.setID(projCursor.getInt(projCursor.getColumnIndex("ID"))); // Lấy ID dưới dạng int
            user.setUsername(projCursor.getString(projCursor.getColumnIndex("user_name")));
            user.setEmail(projCursor.getString(projCursor.getColumnIndex("user_email")));
            user.setPassword(projCursor.getString(projCursor.getColumnIndex("password"))); // Lấy password
            user.setFullName(projCursor.getString(projCursor.getColumnIndex("full_name"))); // Lấy fullName
            users.add(user);
        }
        projCursor.close();
        return users;
    }

    // Phương thức xoá bản ghi trong Table sử dụng khoá chính là ID
    public int deleteEntry(String ID) {
        String where = "ID=?";
        int numberOfEntriesDeleted = db.delete(TABLE_NAME, where, new String[]{ID});
        Toast.makeText(context.getApplicationContext(), "Number fo Entry Deleted Successfully: " + numberOfEntriesDeleted, Toast.LENGTH_SHORT).show();
        return numberOfEntriesDeleted;
    }

    // Phương thức xoá tất cả các bản ghi trong bảng Table
    public void truncateTable() {
        dbHelper.getReadableDatabase();
        db.delete(TABLE_NAME, "1", null);
        Toast.makeText(context.getApplicationContext(), "Table Data Truncated!", Toast.LENGTH_LONG).show();
    }

    public void updateEntry(String ID, String Username, String Useremail, String password, String fullName) { // Thêm password và fullName
        try {
            ContentValues updatedValues = new ContentValues();
            updatedValues.put("user_name", Username);
            updatedValues.put("user_email", Useremail);
            updatedValues.put("password", password); // Cập nhật password
            updatedValues.put("full_name", fullName); // Cập nhật fullName
            String where = "ID = ?";
            db = dbHelper.getWritableDatabase(); // Sử dụng getWritableDatabase() để cập nhật
            db.update(TABLE_NAME, updatedValues, where, new String[]{ID});
            Toast.makeText(context.getApplicationContext(), "Row Updated!", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Log.e(TAG, "Error updating entry", ex); // Ghi log lỗi chi tiết
            Toast.makeText(context.getApplicationContext(), "Error updating user info", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi cho người dùng
        }
    }
}