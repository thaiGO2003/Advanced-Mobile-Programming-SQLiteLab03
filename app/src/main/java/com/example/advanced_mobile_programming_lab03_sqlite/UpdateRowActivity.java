package com.example.advanced_mobile_programming_lab03_sqlite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.advanced_mobile_programming_lab03_sqlite.model.UserModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UpdateRowActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 100;
    private EditText txtUserName, txtPassword, txtEmail, txtFullname;
    private ImageView userImage;
    private String encodedImage = "";
    private UsersDatabaseAdapter databaseAdapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_row);

        databaseAdapter = UsersDatabaseAdapter.getInstance(this);
        databaseAdapter.open();

        userImage = findViewById(R.id.userImage);
        TextView selectImageBtn = findViewById(R.id.selectImageBtn);
        txtUserName = findViewById(R.id.txtUserName);
        txtPassword = findViewById(R.id.txtPassword);
        txtEmail = findViewById(R.id.txtEmail);
        txtFullname = findViewById(R.id.txtFullname);
        Button updateRowBtn = findViewById(R.id.updateRowBtn);
        ImageView btnBack = findViewById(R.id.backButton);

        // Nhận userId từ Intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId != -1) {
            loadUserData(userId);
        } else {
            Toast.makeText(this, "Invalid User ID!", Toast.LENGTH_SHORT).show();
            finish();
        }

        selectImageBtn.setOnClickListener(v -> openGallery());
        userImage.setOnClickListener(v -> openGallery());
        updateRowBtn.setOnClickListener(v -> updateUser());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserData(int userId) {
        UserModel user = databaseAdapter.getUserById(userId);
        if (user != null) {
            txtUserName.setText(user.getUsername());
            txtPassword.setText(user.getPassword());
            txtEmail.setText(user.getEmail());
            txtFullname.setText(user.getFullName());

            if (user.getImageBlob() != null && user.getImageBlob().length > 0) {
                Bitmap decodedByte = BitmapFactory.decodeByteArray(user.getImageBlob(), 0, user.getImageBlob().length);
                userImage.setImageBitmap(decodedByte);
            } else {
                userImage.setImageResource(R.drawable.ic_default_avatar_24px);
            }
        } else {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            finish();
        }
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

    private void updateUser() {
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

        // Chuyển Base64 thành byte[] để lưu vào SQLite nếu có hình mới
        byte[] imageBlob = null;
        if (!encodedImage.isEmpty()) {
            try {
                imageBlob = Base64.decode(encodedImage, Base64.DEFAULT);
                Log.d("UpdateUser", "Image encoded size: " + imageBlob.length + " bytes");
            } catch (Exception e) {
                Log.e("UpdateUser", "Error decoding image", e);
            }
        }

        int result = databaseAdapter.updateEntry(userId, username, email, password, fullname, imageBlob);
        if (result > 0) {
            Toast.makeText(this, "User updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update user!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseAdapter.close();
    }
}
