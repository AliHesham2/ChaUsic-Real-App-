package com.example.chausic.firebase


import com.example.chausic.R
import com.google.firebase.database.*

object FriendList {
    //Get user`s Friends DataBase reference
    private fun friendsReference(): DatabaseReference {
        return Initialize.getDataBaseInstance()!!.getReference(Initialize.getResourceInstance().getString(R.string.FRIENDS))
    }

    //To Remove A friend with chat
    fun removeFriend(currentUser:String,otherUser:String,mixID:String){
        friendsReference().child(currentUser).child(otherUser).removeValue()
        friendsReference().child(otherUser).child(currentUser).removeValue()
        ChatListData.removeTheChatFromTwoSide(currentUser,otherUser)
        IfOnline.removeUserStatusFromDBTwoSide(mixID,otherUser,currentUser)
        Message.removeTheChat(mixID)
    }

}


