package com.example.chausic.view.dashboard.chat.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chausic.databinding.CustomChatListBinding
import com.example.chausic.model.data.UserChatData


class ChatAdapter (private val onClickListener: OnClickListener,private val onLongClickListener:(data:UserChatData) ->Unit):
    ListAdapter<UserChatData, ChatAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder( var binding: CustomChatListBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(requestInfo: UserChatData) {
            binding.data = requestInfo
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<UserChatData>() {
        override fun areItemsTheSame(oldItem: UserChatData, newItem: UserChatData): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: UserChatData, newItem: UserChatData): Boolean {
            return  oldItem.user.id == newItem.user.id && oldItem.user.name == newItem.user.name && oldItem.user.status == newItem.user.status && oldItem.user.img == newItem.user.img && oldItem.chat.lastMsg == newItem.chat.lastMsg
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        return ViewHolder(CustomChatListBinding.inflate(LayoutInflater.from(parent.context)))
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