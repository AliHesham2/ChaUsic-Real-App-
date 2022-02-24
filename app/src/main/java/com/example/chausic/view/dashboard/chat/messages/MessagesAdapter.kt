package com.example.chausic.view.dashboard.chat.messages


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chausic.databinding.LeftChatBinding
import com.example.chausic.databinding.RightChatBinding
import com.example.chausic.firebase.User
import com.example.chausic.model.data.ChatData



class MessagesAdapter(private val onClickListener: OnClickListener):ListAdapter<ChatData, RecyclerView.ViewHolder>(DiffCallback) {
   inner class ViewHolderLeft( var binding: LeftChatBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bindLeft(chatData: ChatData) {
            binding.data = chatData
            binding.executePendingBindings()
        }
    }

    inner class ViewHolderRight( var binding: RightChatBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bindRight(chatData: ChatData) {
            binding.data = chatData
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ChatData>() {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1

        override fun areItemsTheSame(oldItem: ChatData, newItem: ChatData): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ChatData, newItem: ChatData): Boolean {
            return oldItem.msg == newItem.msg && oldItem.seen == newItem.seen && oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == MSG_TYPE_LEFT){
            ViewHolderLeft(LeftChatBinding.inflate(LayoutInflater.from(parent.context)))
        }else{
            ViewHolderRight(RightChatBinding.inflate(LayoutInflater.from(parent.context)))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val requestInfo = getItem(position)
        holder.itemView.setOnLongClickListener {
            onClickListener.onClick(requestInfo)
            return@setOnLongClickListener true
        }
        if(holder is ViewHolderLeft){
            holder.bindLeft(requestInfo)
        }
        if(holder is ViewHolderRight){
            holder.bindRight(requestInfo)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chatData = getItem(position)
        return if(chatData.senderID == User.getCurrentUser()!!.uid){
            MSG_TYPE_RIGHT
        }else{
            MSG_TYPE_LEFT
        }

    }

    class OnClickListener(val clickListener: (requestInfo: ChatData) -> Unit) {
        fun onClick(requestInfo: ChatData) = clickListener(requestInfo)
    }

}
