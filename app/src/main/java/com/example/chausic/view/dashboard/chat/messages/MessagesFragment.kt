package com.example.chausic.view.dashboard.chat.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chausic.R
import com.example.chausic.databinding.FragmentMessagesBinding
import com.example.chausic.firebase.User
import android.app.Activity
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.chausic.firebase.Notification
import com.example.chausic.view.util.PopUpMsg
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.twitter.TwitterEmojiProvider


class MessagesFragment : Fragment() {
    private lateinit var binding: FragmentMessagesBinding
    private lateinit var viewModel: MessagesViewModel
    private var currentThemeID:String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        //initialization
        binding = FragmentMessagesBinding.inflate(inflater)
        val application = requireNotNull(activity).application
        val userData = MessagesFragmentArgs.fromBundle(requireArguments()).userData
        val viewModelFactory = MessagesViewModelFactory(application,userData)
        viewModel = ViewModelProvider(this,viewModelFactory)[MessagesViewModel::class.java]
        binding.data = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // emoji build
        EmojiManager.install(TwitterEmojiProvider())
        val popup = EmojiPopup.Builder.fromRootView(binding.root).build(binding.msgText)

        //Adapter
        binding.messages.adapter = MessagesAdapter(MessagesAdapter.OnClickListener{
            PopUpMsg.textCopy(it.msg!!,this.requireActivity(),this.requireContext(),this.resources)
        })


        //observers
        viewModel.theme.observe(this.viewLifecycleOwner) {
            currentThemeID = it.id
        }

        //OnCLickListeners
        binding.sendButton.setOnClickListener {
            viewModel.sendMessage(User.getCurrentUser()!!.uid, userData.user.id!!,  binding.msgText.text.toString(), this.requireView())
            binding.msgText.setText("")
            binding.msgText.hint = this.resources.getString(R.string.enter_text)
            //(requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?)?.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }

        binding.msgText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrBlank()){
                viewModel.updateIfTyping(false)
            }
            else{
                viewModel.updateIfTyping(true)
            }
        }

        binding.toPicture.setOnClickListener {
            this.findNavController().navigate(MessagesFragmentDirections.actionMessagesFragmentToUserProfileFragment(userData))
        }

        binding.sticker.setOnClickListener {
            popup.toggle()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.setting ->{
                    this.findNavController().navigate(MessagesFragmentDirections.actionMessagesFragmentToSettingFragment(userData,currentThemeID!!))
                    true
                }
                else -> false

            }}


     return binding.root
    }


    override fun onStart() {
        viewModel.updateCurrentUser(true)
        Notification.cancelNotification(this.requireContext())
        super.onStart()
    }

    override fun onPause() {
        viewModel.updateCurrentUser(false)
        viewModel.updateIfTyping(false)
        super.onPause()
    }

    override fun onDestroy() {
        viewModel.updateCurrentUser(false)
        viewModel.updateIfTyping(false)
        super.onDestroy()
    }
}