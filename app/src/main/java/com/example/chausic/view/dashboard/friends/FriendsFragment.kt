package com.example.chausic.view.dashboard.friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chausic.R
import com.example.chausic.databinding.FragmentFriendsBinding
import com.example.chausic.firebase.User
import com.example.chausic.view.dashboard.config.DashBoardFragmentDirections
import com.example.chausic.view.util.PopUpMsg


class FriendsFragment : Fragment() {
    private lateinit var binding: FragmentFriendsBinding
    private lateinit var viewModel: FriendsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //initialization
        binding = FragmentFriendsBinding.inflate(inflater)
        val application = requireNotNull(activity).application
        val viewModelFactory = FriendsViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory)[FriendsViewModel::class.java]
        binding.data = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        //Adapter
        binding.friends.adapter = FriendsAdapter(FriendsAdapter.OnClickListener{
            //to the chatFragment
            this.findNavController().navigate(DashBoardFragmentDirections.actionDashBoardFragmentToMessagesFragment(it))}){
            PopUpMsg.showAlertDialogue(this.requireContext(),this.resources.getString(R.string.ARE_YOU_SURE)+it.user.name){ ok ->
                if(ok){
                    viewModel.removeUser(User.getCurrentUser()!!.uid,it.user.id!!,it.chat.mixId!!)
                }
            }
        }

        return binding.root
    }
}