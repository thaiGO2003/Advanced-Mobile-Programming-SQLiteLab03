package com.example.advanced_mobile_programming_lab03_sqlite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.advanced_mobile_programming_lab03_sqlite.adapter.UsersDatabaseAdapter;
import com.example.advanced_mobile_programming_lab03_sqlite.model.UserModel;

public class UserDetailActivity extends AppCompatActivity {
    private ImageView userImage;
    private TextView userFullName, userEmail, userUsername, userPassword;
    private Button btnEdit, btnDelete;
    private ImageView btnBack;
    private UsersDatabaseAdapter databaseAdapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        userImage = findViewById(R.id.userDetailImage);
        userFullName = findViewById(R.id.userDetailFullName);
        userEmail = findViewById(R.id.userDetailEmail);
        userUsername = findViewById(R.id.userDetailUsername);
        userPassword = findViewById(R.id.userDetailPassword);
        btnEdit = findViewById(R.id.btnEditUser);
        btnDelete = findViewById(R.id.btnDeleteUser);
        btnBack = findViewById(R.id.backButton);

        databaseAdapter = UsersDatabaseAdapter.getInstance(this);
        databaseAdapter.open();

        // Nhận userId từ Intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId != -1) {
            loadUserDetails(userId);
        } else {
            Toast.makeText(this, "User ID not found in intent!", Toast.LENGTH_SHORT).show();
            Log.e("UserDetailActivity", "User ID not found in intent!");
            finish();
        }

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdateRowActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            databaseAdapter.deleteEntry(userId);
            finish();
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadUserDetails(int userId) {
        Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_SHORT).show();
        UserModel user = databaseAdapter.getUserById(userId);
        Log.d("UserDetailActivity", "User: " + user);

        if (user != null) {
            userFullName.setText("Full Name: " + user.getFullName());
            userEmail.setText("Email: " + user.getEmail());
            userUsername.setText("Username: " + user.getUsername());
            userPassword.setText("Password: " + user.getPassword());


            if (user.getImageBlob() != null && user.getImageBlob().length > 0) {
                Bitmap decodedByte = BitmapFactory.decodeByteArray(user.getImageBlob(), 0, user.getImageBlob().length);
                userImage.setImageBitmap(decodedByte);
            } else {
                userImage.setImageResource(R.drawable.ic_default_avatar_24px);
            }
        } else {
            Log.e("UserDetailActivity", "User not found in database!");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseAdapter.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserDetails(userId);
    }
}
