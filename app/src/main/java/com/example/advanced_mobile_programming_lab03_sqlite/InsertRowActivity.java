package com.example.advanced_mobile_programming_lab03_sqlite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.advanced_mobile_programming_lab03_sqlite.adapter.UsersDatabaseAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InsertRowActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 100;
    private EditText txtUserName, txtPassword, txtEmail, txtFullname;
    private ImageView userImage;
    private String encodedImage = "";
    private UsersDatabaseAdapter databaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_row);

        databaseAdapter = UsersDatabaseAdapter.getInstance(this);
        databaseAdapter.open();

        userImage = findViewById(R.id.userImage);
        TextView selectImageBtn = findViewById(R.id.selectImageBtn);
        txtUserName = findViewById(R.id.txtUserName);
        txtPassword = findViewById(R.id.txtPassword);
        txtEmail = findViewById(R.id.txtEmail);
        txtFullname = findViewById(R.id.txtFullname);
        Button insertRowBtn = findViewById(R.id.insertRowFrom);
        ImageView btnBack = findViewById(R.id.backButton);

        selectImageBtn.setOnClickListener(v -> openGallery());
        userImage.setOnClickListener(v -> openGallery());
        insertRowBtn.setOnClickListener(v -> insertUser());
        btnBack.setOnClickListener(v -> finish());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            userImage.setImageURI(imageUri);
            encodeImageToBase64(imageUri);
        }
    }

    private void encodeImageToBase64(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Image selection failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertUser() {
        String username = txtUserName.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String fullname = txtFullname.getText().toString().trim();

        // Kiểm tra Username (ít nhất 4 ký tự, chỉ chứa chữ cái, số, _ và .)
        if (!username.matches("^[a-zA-Z0-9_.]{4,}$")) {
            txtUserName.setError("Username must be at least 4 characters and can only contain letters, numbers, _ and .");
            return;
        }

        // Kiểm tra Email (định dạng hợp lệ)
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError("Invalid email format!");
            return;
        }

        // Kiểm tra Password (ít nhất 6 ký tự, có cả chữ và số)
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")) {
            txtPassword.setError("Password must be at least 6 characters and include both letters and numbers.");
            return;
        }

        // Kiểm tra Fullname (chỉ chứa chữ và khoảng trắng)
        if (!fullname.matches("^[\\p{L}\\s]+$")) {
            txtFullname.setError("Full name can only contain letters and spaces.");
            return;
        }

        // Chuyển Base64 thành byte[] để lưu vào SQLite
        byte[] imageBlob = null;
        if (!encodedImage.isEmpty()) {
            try {
                imageBlob = Base64.decode(encodedImage, Base64.DEFAULT);
                Log.d("InsertUser", "Image encoded size: " + imageBlob.length + " bytes");
            } catch (Exception e) {
                Log.e("InsertUser", "Error decoding image", e);
            }
        } else {
            Log.d("InsertUser", "No image selected, imageBlob is null.");
        }

        // Gọi insertEntry với đầy đủ dữ liệu
        long result = databaseAdapter.insertEntry(username, email, password, fullname, imageBlob);

        if (result != -1) {
            Log.d("InsertUser", "User inserted successfully with ID: " + result);
            Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.e("InsertUser", "Failed to insert user!");
            Toast.makeText(this, "Failed to add user!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseAdapter.close();
    }
}
