package com.example.chausic.firebase

import com.example.chausic.R
import com.example.chausic.model.data.IfUserOnline
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

object IfOnline {

    private lateinit var detectEventListener: ValueEventListener
    //Get Chat DataBase reference
    private fun ifOnlineReference(): DatabaseReference {
        return Initialize.getDataBaseInstance()!!.getReference(Initialize.getResourceInstance().getString(R.string.IF_ONLINE))
    }

    //Create New Reference with 2 users and check if they are inside the chat or not
    private fun createIfOnline(mixID: String, senderID: String, receiverID: String){
        ifOnlineReference().child(mixID).child(senderID).setValue(IfUserOnline(userID = senderID,online = false))
        ifOnlineReference().child(mixID).child(receiverID).setValue(IfUserOnline(userID = receiverID,online = false))
    }

    //Check if this reference is already created or not between 2 specific users
    fun checkIfCreated(mixID: String, senderID: String, receiverID: String){
        ifOnlineReference().child(mixID).get().addOnSuccessListener {
            if(!it.exists()) {
                createIfOnline(mixID,senderID,receiverID)
            }
        }
    }
    //Remove user status from DB
    fun removeUserStatusFromDB(mixID: String, ID: String){
        ifOnlineReference().child(mixID).child(ID).removeValue()
    }
    fun removeUserStatusFromDBTwoSide(mixID: String, senderID: String,receiverID: String){
        ifOnlineReference().child(mixID).child(senderID).removeValue()
        ifOnlineReference().child(mixID).child(receiverID).removeValue()
    }

    //Update  if user in chat or not
    fun updateStatus(mixID: String,userID:String,online: Boolean){
        val map: HashMap<String, Any> = HashMap()
        map[Initialize.getResourceInstance().getString(R.string.ONLINE)] = online
        ifOnlineReference().child(mixID).child(userID).updateChildren(map)
    }
    //Update  if user typing or not
    fun updateTyping(mixID: String,userID:String,typing: Boolean){
        val map: HashMap<String, Any> = HashMap()
        map[Initialize.getResourceInstance().getString(R.string.TYPING)] = typing
        ifOnlineReference().child(mixID).child(userID).updateChildren(map)
    }

    //SnapShot if user inside chat or not
    fun detectUserCurrentState(mixID:String,otherUserID:String,otherUserStatus:(online:Boolean) ->Unit){
        detectEventListener = object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               val otherUser = snapshot.getValue<IfUserOnline>()
                if(otherUser != null){
                    if(otherUser.online == true){
                        otherUserStatus(true)
                    }else{
                        otherUserStatus(false)
                    }
                }else{
                    otherUserStatus(false)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        }
        ifOnlineReference().child(mixID).child(otherUserID).addValueEventListener(detectEventListener)
     }

    //SnapShot if user inside  chat typing or not
    fun detectUserIfTyping(mixID:String,otherUserID:String,ifTyping:(typing:Boolean) ->Unit){
        ifOnlineReference().child(mixID).child(otherUserID).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val otherUser = snapshot.getValue<IfUserOnline>()
                if(otherUser != null){
                    if(otherUser.typing == true){
                        ifTyping(true)
                    }else{
                        ifTyping(false)
                    }
                }else{
                    ifTyping(false)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

      fun removeListenerOfDetectUserCurrentState(mixID:String,otherUserID:String){
        ifOnlineReference().child(mixID).child(otherUserID).removeEventListener(detectEventListener)
    }

}