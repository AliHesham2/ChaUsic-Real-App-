package com.example.chausic.view.dashboard.friends


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chausic.databinding.CustomFriendsBinding
import com.example.chausic.model.data.UserChatData


class FriendsAdapter (private val onClickListener: OnClickListener,private val onLongClickListener:(data:UserChatData) ->Unit) :
    ListAdapter<UserChatData, FriendsAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder( var binding: CustomFriendsBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(requestInfo: UserChatData) {
            binding.data = requestInfo
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<UserChatData>() {
        override fun areItemsTheSame(oldItem: UserChatData, newItem: UserChatData): Boolean {
            return oldItem.user === newItem.user
        }

        override fun areContentsTheSame(oldItem: UserChatData, newItem: UserChatData): Boolean {
            return oldItem.user.name == newItem.user.name && oldItem.user.status == newItem.user.status && oldItem.user.img == newItem.user.img
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        return ViewHolder(CustomFriendsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val requestInfo = getItem(position)
        holder.itemView.layoutParams = Constraints.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(requestInfo)
        }
        holder.itemView.setOnLongClickListener {
            onLongClick(requestInfo)
        }
        holder.bind(requestInfo)
    }

    class OnClickListener(val clickListener: (requestInfo: UserChatData) -> Unit) {
        fun onClick(requestInfo: UserChatData) = clickListener(requestInfo)
    }
    private fun onLongClick(requestInfo: UserChatData):Boolean{
        onLongClickListener(requestInfo)
        return true
    }

}