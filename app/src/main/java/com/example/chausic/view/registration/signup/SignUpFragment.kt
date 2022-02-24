package com.example.chausic.view.registration.signup

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chausic.R
import com.example.chausic.databinding.FragmentSignUpBinding
import com.example.chausic.view.dashboard.DashBoardActivity
import com.example.chausic.view.util.PopUpMsg


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: SignUpFragmentViewModel
    private var email:Boolean = false
    private var name:Boolean = false
    private var password:Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //initialization
        binding = FragmentSignUpBinding.inflate(inflater)
        val application = requireNotNull(activity).application
        val viewModelFactory = SignUpFragmentViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory)[SignUpFragmentViewModel::class.java]
        binding.lifecycleOwner = this

        //onChangeListeners
        binding.name.editText?.doOnTextChanged { text, _, _, _ ->
            if(text.isNullOrEmpty()){
                binding.name.editText?.error = application.resources.getString(R.string.NO_NAME)
                binding.name.endIconDrawable = null
                name = false
            }else if(text.length < 3){
                binding.name.editText?.error = application.resources.getString(R.string.INVALID_NAME)
                binding.name.endIconDrawable = null
                name = false
            }else{
                name = true
                binding.name.setEndIconDrawable(R.drawable.outline_check_24)
            }
        }

        binding.email.editText?.doOnTextChanged { text, _, _, _ ->
            if(text.isNullOrEmpty()){
                binding.email.editText?.error = application.resources.getString(R.string.NO_EMAIL)
                binding.email.endIconDrawable = null
                email = false
            }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                binding.email.editText?.error =  application.resources.getString(R.string.INVALID_EMAIL_FORM)
                email = false
                binding.email.endIconDrawable = null
            }else{
                email = true
                binding.email.setEndIconDrawable(R.drawable.outline_check_24)
            }
        }

        binding.password.editText?.doOnTextChanged { text, _, _, _ ->
            if(text.isNullOrEmpty()){
                binding.password.editText?.error = application.resources.getString(R.string.NO_PASSWORD)
                password = false
            }else if(text.trim().length < 8  ){
                binding.password.editText?.error =  application.resources.getString(R.string.INVALID_PASSWORD)
                password = false
            }else{
                password = true
            }
        }


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

        //Handle Clicking on SignUp
        binding.signUp.setOnClickListener {
            if(!email || !password || !name){
                PopUpMsg.alertMsg(this.requireView(),application.resources.getString(R.string.NO_DATA))
            }else{
                viewModel.getData(binding.email.editText?.text.toString().trim(),binding.password.editText?.text.toString().trim(),this.requireActivity(),binding.name.editText?.text.toString(),this.requireView())
            }
        }
        //Handle Clicking on login
        binding.social.setOnClickListener {
            this.findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
        }

        return binding.root
    }

}