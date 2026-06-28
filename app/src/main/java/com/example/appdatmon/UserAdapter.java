package com.example.appdatmon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdatmon.data.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onEdit(User user);
        void onDelete(User user);
    }

    public UserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvName.setText(user.getFullName());
        holder.tvEmail.setText(user.getEmail() != null && !user.getEmail().isEmpty() ? user.getEmail() : user.getPhone());
        holder.tvRole.setText(user.getRole());
        holder.tvStatus.setText(user.getStatus());

        // Role badge color
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            holder.tvRole.setBackgroundResource(R.drawable.bg_status_active); // Green for Admin
        } else {
            holder.tvRole.setBackgroundResource(R.drawable.bg_status_inactive); // Gray for others
        }

        // Status badge color
        if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {
            holder.tvStatus.setText("Hoạt động");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_active); // Green
        } else {
            holder.tvStatus.setText("Đã khóa");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_inactive); // Gray
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateData(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole, tvStatus;
        ImageView btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvRole = itemView.findViewById(R.id.tvUserRole);
            tvStatus = itemView.findViewById(R.id.tvUserStatus);
            btnEdit = itemView.findViewById(R.id.btnEditUser);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}
