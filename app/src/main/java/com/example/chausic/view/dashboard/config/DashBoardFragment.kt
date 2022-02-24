package com.example.chausic.view.dashboard.config

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chausic.R
import com.example.chausic.databinding.FragmentDashBoardBinding
import com.example.chausic.firebase.Requests
import com.example.chausic.firebase.User
import com.example.chausic.model.data.ChatList
import com.example.chausic.model.data.UserChatData
import com.example.chausic.model.data.UserData
import com.example.chausic.view.util.FragmentsInstance
import com.example.chausic.view.util.PopUpMsg
import com.example.chausic.view.util.PopUpDialogue
import com.google.android.material.tabs.TabLayoutMediator


class DashBoardFragment : Fragment() {
    private lateinit var binding : FragmentDashBoardBinding
    private lateinit var viewModel:DashBoardViewModel
    private lateinit var adapter: ViewPagerAdapter
    private  var data : Bundle? = null
    private  var flag = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View {
        //handle data from notification just once
        handleNotificationPendingIntent()
        //Initialization
        binding = FragmentDashBoardBinding.inflate(inflater)
        viewModel = ViewModelProvider(this)[DashBoardViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner
        adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        binding.pager2.adapter = adapter
        binding.tabLayout.clearOnTabSelectedListeners()

        //TabLayout Handler
        TabLayoutMediator(binding.tabLayout, binding.pager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = resources.getString(R.string.Chats)
                }
                1 -> {
                    tab.text = resources.getString(R.string.Friends)
                }
                2 -> {
                    tab.text = resources.getString(R.string.Profile)
                }
            }
        }.attach()

        //TopBar Menu Handle
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add -> {
                    // send Request to add Friend
                    PopUpDialogue.showAddDialogue(this) { id ->
                        Requests.findIFUserExist(id,this.requireView()){ isExist , otherToken ,currentName ->
                            when(isExist){
                                true  ->{
                                    viewModel.sendNotification(this.requireView(),otherToken!!,currentName)
                                    PopUpDialogue.hideAddDialogue()
                                    PopUpMsg.toastMsg(this.requireContext(),this.resources.getString(R.string.REQUEST_SENT))
                                }
                                false ->{
                                    PopUpDialogue.hideAddDialogue()
                                    PopUpMsg.toastMsg(this.requireContext(),this.resources.getString(R.string.USER_NOT_FOUND))
                                }
                            }
                        }

                    }
                    true
                }
                R.id.request -> {
                    this.findNavController().navigate(DashBoardFragmentDirections.actionDashBoardFragmentToRequestsFragment())
                    true
                }
                R.id.logout -> {
                    User.signOut(this)
                    true
                }
                else -> false
            }
        }

        return  binding.root
    }
    //Pushing the pending intent on top of the stack and then when the user press back, I destroy this pending intent
    private fun handleNotificationPendingIntent(){
        data = this.activity?.intent?.extras
        if (data != null && !flag){
            flag = true
            if (data!!.get(resources.getString(R.string.NOTIFICATION_FOREGROUND))  != null){
                FragmentsInstance.foreGroundFlag = true }
            when(data!![resources.getString(R.string.NOTIFICATION_TYPE)]){
                resources.getString(R.string.DATA_REQUEST) ->{
                    this.findNavController().navigate(DashBoardFragmentDirections.actionDashBoardFragmentToRequestsFragment())
                }
                resources.getString(R.string.DATA_CHAT) ->{
                    val userID =  data!!.get(resources.getString(R.string.DATA_USERID))  as String
                    val mixID =  data!!.get(resources.getString(R.string.DATA_MIXID)) as String
                    this.findNavController().navigate(DashBoardFragmentDirections.actionDashBoardFragmentToMessagesFragment(UserChatData(UserData(id = userID), ChatList(mixId = mixID))))
                }
            }
        }
    }

}