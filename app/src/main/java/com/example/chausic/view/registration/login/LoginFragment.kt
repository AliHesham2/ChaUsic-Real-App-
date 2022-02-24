package com.example.chausic.view.registration.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chausic.R
import com.example.chausic.databinding.FragmentLoginBinding
import com.example.chausic.view.dashboard.DashBoardActivity
import com.example.chausic.view.util.PopUpMsg

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginFragmentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //initialization
        binding = FragmentLoginBinding.inflate(inflater)
        val application = requireNotNull(activity).application
        val viewModelFactory = LoginFragmentViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory)[LoginFragmentViewModel::class.java]
        binding.lifecycleOwner = this


        //observers
        viewModel.isSuccess.observe(this.viewLifecycleOwner) {
            if (it == true) {
                if (it == true) {
                    startActivity(Intent(this.activity, DashBoardActivity::class.java))
                    this.activity?.finish()
                }
            }
        }

        viewModel.loading.observe(this.viewLifecycleOwner) {
            if (it == true) {
                PopUpMsg.showDialogue(this.requireContext())
            } else {
                PopUpMsg.hideDialogue()
            }
        }

        //onCLickListeners
        binding.signIn.setOnClickListener {
            if(binding.email.editText?.text.toString().isEmpty() || binding.password.editText?.text.toString().isEmpty()){
                PopUpMsg.alertMsg(this.requireView(),application.resources.getString(R.string.NO_DATA))
            }else{
                viewModel.getData(binding.email.editText?.text.toString().trim(),binding.password.editText?.text.toString().trim(),this.requireActivity(),this.requireView())
            }
        }

        binding.social.setOnClickListener {
            this.findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }

        return binding.root
    }

}