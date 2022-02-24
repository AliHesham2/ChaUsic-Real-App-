package com.example.chausic.firebase


import android.util.Log
import android.view.View
import com.example.chausic.R
import com.example.chausic.model.data.ChatList
import com.example.chausic.view.util.PopUpMsg
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue


object ChatListData {
    private lateinit var getChatListChildListener: ChildEventListener

    //Get ChatList DataBase reference
    private fun chatListReference(): DatabaseReference {
        return Initialize.getDataBaseInstance()!!.getReference(Initialize.getResourceInstance().getString(
            R.string.CHATS_List))
    }

    //Create ChatList With two Users IDs in DATABASE
    fun createChatList(senderId: String, receiverID: String, mixID: String, requireView: View, Msg: String) {
            chatListReference().child(senderId).child(receiverID).setValue(ChatList(mixId = mixID, id = receiverID , lastMsg = Msg)).addOnFailureListener {
            PopUpMsg.fireBaseError(requireView,it)
        }
            chatListReference().child(receiverID).child(senderId).setValue(ChatList(mixId = mixID,id =senderId,lastMsg = Msg)).addOnFailureListener {
            PopUpMsg.fireBaseError(requireView,it)
        }
    }

    // To Display Users Data in ChatList section in Tabs View
    fun getChatListIDsOfCurrentUser(dataOfUsers: (data: ChatList?,type:String) -> Unit){
        chatListReference().child(User.getCurrentUser()!!.uid).get().addOnSuccessListener {
           if(!it.hasChildren()){ dataOfUsers(null,Initialize.getResourceInstance().getString(R.string.DATA_ADD))}
               getChatListChildListener = object :ChildEventListener{
                   override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                       if(snapshot.exists()){
                           val data = snapshot.getValue<ChatList>()
                           if(data != null){
                               dataOfUsers(data,Initialize.getResourceInstance().getString(R.string.DATA_ADD))
                           }
                       }
                   }
                   override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                       if(snapshot.exists()){
                           val data = snapshot.getValue<ChatList>()
                           if(data != null){
                               dataOfUsers(data,Initialize.getResourceInstance().getString(R.string.DATA_CHANGED))
                           }
                       }
                   }

                   override fun onChildRemoved(snapshot: DataSnapshot) {
                       if(snapshot.exists()){
                           val data = snapshot.getValue<ChatList>()
                           if(data != null){
                               dataOfUsers(data,Initialize.getResourceInstance().getString(R.string.DATA_REMOVED))
                           }
                       }
                   }

                   override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                   }

                   override fun onCancelled(error: DatabaseError) {
                   }

               }
            chatListReference().child(User.getCurrentUser()!!.uid).addChildEventListener(getChatListChildListener)
        }
    }




    // helpful in removing to detect if other user delete the chat or not ..
    private fun ifChatExistInOtherSide(currentUser:String,otherUser:String,ifExist:(isExist:Boolean) ->Unit){
        chatListReference().child(otherUser).child(currentUser).get().addOnSuccessListener {
            if (it.exists()){
                ifExist(true)
            }else{
                ifExist(false)
            }
        }

    }
    //remove the chat from chat list and remove all messages if the chat is removed in other side ...
    fun removeTheChatFromOneSide(currentUser:String,otherUser:String,mixID: String){
        chatListReference().child(currentUser).child(otherUser).removeValue()
        IfOnline.removeUserStatusFromDB(mixID,currentUser)
        ifChatExistInOtherSide(currentUser,otherUser){
            if(!it){
                Message.removeTheChat(mixID)
            }
        }
    }

    fun removeTheChatFromTwoSide(currentUser:String,otherUser:String){
        chatListReference().child(currentUser).child(otherUser).removeValue()
        chatListReference().child(otherUser).child(currentUser).removeValue()
    }

    fun  removeListenerOfGetChatListIDsOfCurrentUser(){
        if (this::getChatListChildListener.isInitialized) {
            chatListReference().child(User.getCurrentUser()!!.uid).removeEventListener(getChatListChildListener)
        }
    }
}
