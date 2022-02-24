package com.example.chausic.view.dashboard.chat.chats

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chausic.R
import com.example.chausic.databinding.FragmentChatsBinding
import com.example.chausic.firebase.User
import com.example.chausic.view.dashboard.config.DashBoardFragmentDirections
import com.example.chausic.view.util.PopUpMsg


class ChatsFragment : Fragment() {
    private lateinit var binding: FragmentChatsBinding
    private lateinit var viewModel: ChatsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //initialization
        binding = FragmentChatsBinding.inflate(inflater)
        val application = requireNotNull(activity).application
        val viewModelFactory = ChatsViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory)[ChatsViewModel::class.java]
        binding.data = viewModel
        binding.lifecycleOwner = this


        //Adapter
        binding.chats.adapter = ChatAdapter(ChatAdapter.OnClickListener{
            this.findNavController().navigate(DashBoardFragmentDirections.actionDashBoardFragmentToMessagesFragment(it))}){
            PopUpMsg.showAlertDialogue(this.requireContext(),this.resources.getString(R.string.ARE_YOU_SURE_CHAT)){ ok ->
                if(ok){
                    viewModel.removeChat(User.getCurrentUser()!!.uid,it.user.id!!,it.chat.mixId!!)
                }
            }
        }

            return binding.root
    }
}

