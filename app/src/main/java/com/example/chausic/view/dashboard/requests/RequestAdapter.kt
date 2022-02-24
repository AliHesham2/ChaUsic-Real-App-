package com.example.chausic.view.dashboard.requests

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chausic.databinding.CustomRequestsBinding
import com.example.chausic.model.data.SendRequest

class RequestAdapter(private val onAccept: OnAccept,private val onReject:OnReject ) :
    ListAdapter<SendRequest, RequestAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(var binding: CustomRequestsBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(requestInfo: SendRequest) {
            binding.data = requestInfo
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<SendRequest>() {
        override fun areItemsTheSame(oldItem: SendRequest, newItem: SendRequest): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: SendRequest, newItem: SendRequest): Boolean {
            return oldItem.senderID == newItem.senderID
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        return ViewHolder(CustomRequestsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val requestInfo = getItem(position)
        holder.binding.accept.setOnClickListener { onAccept.onClick(requestInfo) }
        holder.binding.refuse.setOnClickListener {  onReject.onClick(requestInfo)}
        holder.bind(requestInfo)
    }

    class OnAccept(val clickListener: (requestInfo: SendRequest) -> Unit) {
        fun onClick(requestInfo: SendRequest) = clickListener(requestInfo)
    }
    class OnReject(val clickListener: (requestInfo: SendRequest) -> Unit) {
        fun onClick(requestInfo: SendRequest) = clickListener(requestInfo)
    }


}