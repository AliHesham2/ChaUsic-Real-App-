package com.example.chausic.view.registration.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.chausic.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //initialization
        binding = FragmentHomeBinding.inflate(inflater)

        //Handle Clicking on signIn
        binding.signIn.setOnClickListener {
            this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
        }

        //Handle Clicking on signUp
        binding.signUp.setOnClickListener {
            this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSignUpFragment())
        }


        return binding.root
    }

}