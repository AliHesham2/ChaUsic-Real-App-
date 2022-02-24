package com.example.chausic.view.dashboard.requests

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chausic.R
import com.example.chausic.databinding.FragmentRequestsBinding
import com.example.chausic.view.util.FragmentsInstance


class RequestsFragment : Fragment() {
    private lateinit var binding: FragmentRequestsBinding
    private lateinit var viewModel: RequestViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //initialization
        binding = FragmentRequestsBinding.inflate(inflater)
        val application = requireNotNull(activity).application
        val viewModelFactory = RequestViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(RequestViewModel::class.java)
        binding.data = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //Adapter
        binding.requests.adapter = RequestAdapter(
            RequestAdapter.OnAccept{
                viewModel.onAccept(it.senderID)
            }
            ,RequestAdapter.OnReject{
                viewModel.onReject(it.senderID)
        })

        //OnClickListeners
        binding.topAppBar.setNavigationOnClickListener {
            if (this.activity?.intent?.extras?.get(resources.getString(R.string.NOTIFICATION_FOREGROUND)) != null){
                FragmentsInstance.foreGroundFlag = false
                this.requireActivity().finish()
            } else{ this.findNavController().popBackStack() }
        }



        return binding.root
    }
}