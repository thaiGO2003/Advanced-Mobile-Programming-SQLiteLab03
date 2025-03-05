package com.example.advanced_mobile_programming_lab03_sqlite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.advanced_mobile_programming_lab03_sqlite.adapter.UsersDatabaseAdapter;
import com.example.advanced_mobile_programming_lab03_sqlite.model.UserModel;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private UsersDatabaseAdapter usersDatabaseAdapter;
    private RecyclerView recyclerViewUsers;
    private CustomListAdapterUsers adapterUsers;
    private List<UserModel> userList;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersDatabaseAdapter = UsersDatabaseAdapter.getInstance(getApplicationContext());
        usersDatabaseAdapter.open();

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        searchView = findViewById(R.id.searchView);

        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        loadUsers();

        // Xử lý tìm kiếm user theo tên
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return false;
            }
        });
    }

    private void loadUsers() {
        try {
            userList = usersDatabaseAdapter.getRows();
            adapterUsers = new CustomListAdapterUsers(this, userList);
            recyclerViewUsers.setAdapter(adapterUsers);
        } catch (JSONException e) {
            Toast.makeText(this, "Error loading users", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterUsers(String query) {
        List<UserModel> filteredList = usersDatabaseAdapter.searchUsers(query);
        adapterUsers.updateData(filteredList);
    }

    public void insertRowActivity(View view) {
        Intent insertRowIntent = new Intent(MainActivity.this, InsertRowActivity.class);
        startActivity(insertRowIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers(); // Load lại danh sách user khi quay lại activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        usersDatabaseAdapter.close();
    }
}
