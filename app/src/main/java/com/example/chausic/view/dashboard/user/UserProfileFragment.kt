package com.example.chausic.view.dashboard.user


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chausic.databinding.FragmentUserProfileBinding
import com.example.chausic.view.util.FullScreenImage


class UserProfileFragment : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var viewModel: UserViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //initialization
        binding = FragmentUserProfileBinding.inflate(inflater)
        val application = requireNotNull(activity).application
        val userData = UserProfileFragmentArgs.fromBundle(requireArguments()).userData
        val viewModelFactory = UserViewModelFactory(application,userData)
        viewModel = ViewModelProvider(this,viewModelFactory).get(UserViewModel::class.java)
        binding.data = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //OnClickListeners
        binding.topAppBar.setNavigationOnClickListener {
            this.findNavController().popBackStack()
        }

        binding.profile.setOnClickListener {
            val fullScreenIntent = Intent(this.context, FullScreenImage::class.java)
            fullScreenIntent.data = userData.user.img!!.toUri()
            this.startActivity(fullScreenIntent)
        }

        return  binding.root
    }
}