package com.example.advanced_mobile_programming_lab03_sqlite;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.advanced_mobile_programming_lab03_sqlite.R;
import com.example.advanced_mobile_programming_lab03_sqlite.adapter.UsersDatabaseAdapter;
import com.example.advanced_mobile_programming_lab03_sqlite.model.UserModel;

import java.util.List;

public class CustomListAdapterUsers extends RecyclerView.Adapter<CustomListAdapterUsers.UserViewHolder> {
    private Context context;
    private List<UserModel> userList;

    public CustomListAdapterUsers(Context context, List<UserModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);
        Log.d("DEBUG 1", "Userid : " + user.getID());
        holder.userID.setText(String.valueOf(user.getID()));
        holder.userName.setText(user.getUsername());
        holder.userFullName.setText(user.getFullName());
        holder.userEmail.setText(user.getEmail());
        holder.userPassword.setText(user.getPassword());

        if (user.getImageBlob() != null) {
            Log.d("DEBUG", "Image Blob Length: " + user.getImageBlob().length);
            if (user.getImageBlob().length > 0) {
                Bitmap decodedByte = BitmapFactory.decodeByteArray(user.getImageBlob(), 0, user.getImageBlob().length);
                if (decodedByte != null) {
                    Log.d("DEBUG", "Bitmap decoded successfully.");
                    holder.userImage.setImageBitmap(decodedByte);
                } else {
                    Log.e("DEBUG", "Bitmap decoding failed.");
                    holder.userImage.setImageResource(R.drawable.ic_default_avatar_24px);
                }
            } else {
                Log.e("DEBUG", "Image Blob is empty.");
                holder.userImage.setImageResource(R.drawable.ic_default_avatar_24px);
            }
        } else {
            Log.e("DEBUG", "Image Blob is null.");
            holder.userImage.setImageResource(R.drawable.ic_default_avatar_24px);
        }



        // Xử lý sự kiện click vào nút Edit
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateRowActivity.class);
            intent.putExtra("USER_ID", user.getID());
            context.startActivity(intent);
        });

        // Xử lý sự kiện click vào nút Delete
        holder.btnDelete.setOnClickListener(v -> {
            UsersDatabaseAdapter databaseAdapter = UsersDatabaseAdapter.getInstance(context);
            databaseAdapter.open();
            databaseAdapter.deleteEntry(user.getID());
            userList.remove(position);
            notifyItemRemoved(position);
            databaseAdapter.close();
        });


        holder.btnView.setOnClickListener(v -> {
            int userId = Integer.parseInt(holder.userID.getText().toString());
            Log.d("DEBUG", "User ID from hidden TextView: " + userId);

            Intent intent = new Intent(context, UserDetailActivity.class);
            intent.putExtra("USER_ID", userId);
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateData(List<UserModel> newList) {
        userList = newList;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userID, userFullName, userEmail,userPassword, userName;
        ImageView userImage;
        ImageButton btnEdit, btnDelete, btnView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userID = itemView.findViewById(R.id.userID);
            userImage = itemView.findViewById(R.id.userImage);
            userFullName = itemView.findViewById(R.id.userFullName);
            userEmail = itemView.findViewById(R.id.userEmail);
            userPassword = itemView.findViewById(R.id.userPassword);
            userName = itemView.findViewById(R.id.userName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }

}
