package com.example.jasminassociates.ui.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.models.User
import com.example.jasminassociates.viewmodels.admin.UserDisplay

class RecentUsersAdapter(private var users: List<User>) : RecyclerView.Adapter<RecentUsersAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        val userRoleTextView: TextView = itemView.findViewById(R.id.userRoleTextView)
        val userEmailTextView: TextView = itemView.findViewById(R.id.userEmailTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.userNameTextView.text = "${user.firstName} ${user.lastName}"
        holder.userRoleTextView.text = user.role
        holder.userEmailTextView.text = user.email
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<UserDisplay>) {
        newUsers.also { users = users }
        notifyDataSetChanged()
    }
}