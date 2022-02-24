package com.example.chausic.view.util


import com.example.chausic.view.dashboard.chat.chats.ChatsFragment
import com.example.chausic.view.dashboard.friends.FriendsFragment
import com.example.chausic.view.dashboard.profile.ProfileFragment

object FragmentsInstance {
      val profileFragment =  ProfileFragment()
      val chatsFragment = ChatsFragment()
      val friendsFragment = FriendsFragment()
      var foreGroundFlag = false
}