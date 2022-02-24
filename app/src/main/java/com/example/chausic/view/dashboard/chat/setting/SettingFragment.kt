package com.example.chausic.view.dashboard.chat.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chausic.R
import com.example.chausic.databinding.FragmentSettingBinding
import com.example.chausic.model.data.Theme
import com.example.chausic.view.util.PopUpMsg


class SettingFragment : Fragment() {
    private lateinit var binding : FragmentSettingBinding
    private lateinit var viewModel: SettingViewModel
    private var items:MutableList<String> = mutableListOf()
    private var currentText:Theme?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Initialization
        binding = FragmentSettingBinding.inflate(inflater)
        val application = requireNotNull(activity).application
        val userData = SettingFragmentArgs.fromBundle(requireArguments()).userData
        val themeID = SettingFragmentArgs.fromBundle(requireArguments()).themeID
        val viewModelFactory = SettingViewModelFactory(application,themeID)
        viewModel = ViewModelProvider(this,viewModelFactory).get(SettingViewModel::class.java)
        binding.data = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //observers
        viewModel.data.observe(this.viewLifecycleOwner) { data ->
            data.map { theme ->
                theme.name?.let { items.add(it) }
            }
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
            (binding.menu.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        }

        viewModel.currentTheme.observe(this.viewLifecycleOwner) {
            currentText = it
        }

        viewModel.updateCurrentTheme.observe(this.viewLifecycleOwner) {
            this.findNavController()
                .navigate(SettingFragmentDirections.actionSettingFragmentToMessagesFragment(userData))
        }


        //dropdownMenu
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, items)
        (binding.menu.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        
        //OnClickListeners
        binding.topAppBar.setNavigationOnClickListener {
            this.findNavController().popBackStack()
        }
        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.done -> {
                    val name = binding.menu.editText?.text.toString()
                    val position = items.indexOf(name)
                    if(currentText!!.name == name){
                        PopUpMsg.alertMsg(this.requireView(),resources.getString(R.string.SAME))
                    }else if(name.isEmpty()){PopUpMsg.alertMsg(this.requireView(),resources.getString(R.string.NO_tHEME))
                    }else{
                        viewModel.getThemeID(position,this.requireView())
                    }
                    true
                }
                else -> false
            }
        }
        return binding.root
    }




}