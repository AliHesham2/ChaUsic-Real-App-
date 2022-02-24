package com.example.chausic.firebase


import android.app.Application
import android.view.View
import com.example.chausic.R
import com.example.chausic.model.data.ChatData
import com.example.chausic.view.util.PopUpMsg
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

object Message {

    private lateinit var   getMessageListening1 : ChildEventListener
    private lateinit var   updateMessageListening : ValueEventListener

    //Get Chat DataBase reference
    private fun chatsReference(): DatabaseReference {
        return Initialize.getDataBaseInstance()!!.getReference(Initialize.getResourceInstance().getString(
            R.string.CHATS))
    }

    //Send Message
    fun sendMessage(senderId: String, receiverID: String, Msg: String, isSeen: Boolean, requireView: View, mixId: String){
        val keyID = chatsReference().child(mixId).push().key
        val data = ChatData(keyID,Msg,isSeen,senderId,receiverID, playedMusic = false)
        chatsReference().child(mixId).child(keyID!!).setValue(data).addOnFailureListener {
            PopUpMsg.fireBaseError(requireView,it)
        }
        ChatListData.createChatList(senderId,receiverID,mixId,requireView,Msg)
    }


    //Update Messages If Seen
    private fun updateMessagesIFSeen(mixID: String?, currentUserID: String) {
        updateMessageListening = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    snapshot.children.map { msg ->
                        val chatData = msg.getValue<ChatData>()
                        if (chatData != null && chatData.receiverID == currentUserID && chatData.seen != true ) {
                            val map: HashMap<String, Any> = HashMap()
                            map[Initialize.getResourceInstance().getString(R.string.SEEN)] = true
                            msg.ref.updateChildren(map)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        }
        chatsReference().child(mixID!!).addValueEventListener(updateMessageListening)
    }

    //Update Messages If Music played .. paused .. released..
    private fun updateMusicStatus(mixID: String?,childID:String) {
        val map: HashMap<String, Any> = HashMap()
        map[Initialize.getResourceInstance().getString(R.string.IF_PLAYED)] = true
        chatsReference().child(mixID!!).child(childID).updateChildren(map)
    }

    //To Remove the chat
    fun removeTheChat(mixID: String){
        chatsReference().child(mixID).removeValue()
    }


    fun getMessagesNew(mixID:String,currentUserID:String,app:Application,chatUserData:(chatData:ChatData,type:String)->Unit){
        updateMessagesIFSeen(mixID,currentUserID)
        getMessageListening1 = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()){
                    val msg = snapshot.getValue<ChatData>()
                    chatUserData(msg!!,app.resources.getString(R.string.DATA_ADD))
                    checkMusic(msg,mixID,app)
                } }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val msg = snapshot.getValue<ChatData>()
                if (msg != null) { chatUserData(msg,app.resources.getString(R.string.DATA_CHANGED)) } }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }
        chatsReference().child(mixID).addChildEventListener(getMessageListening1)
    }

    private fun checkMusic(dataToCheck: ChatData, mixID: String, app: Application) {
            if(dataToCheck.msg!!.contains("/p") && dataToCheck.playedMusic != true){
                // play music
                val uri = dataToCheck.msg.trim().split("/p")
                updateMusicStatus(mixID,dataToCheck.id!!)
                Music.getMusicUrl(uri[1].trim(),app)
            }
            if(dataToCheck.msg.contains("/s") && dataToCheck.playedMusic != true){
                // stop music
                updateMusicStatus(mixID,dataToCheck.id!!)
                Music.releaseMusic(app)
            }
            if(dataToCheck.msg.contains("/r") && dataToCheck.playedMusic != true){
                // resume music
                updateMusicStatus(mixID,dataToCheck.id!!)
                Music.resumeMusic(app)
            }
            if(dataToCheck.msg.contains("/w") && dataToCheck.playedMusic != true){
                // pause music
                updateMusicStatus(mixID,dataToCheck.id!!)
                Music.pauseMusic(app)
            }
    }

    fun removeGetMessageListener(mixId: String) {
        chatsReference().child(mixId).removeEventListener(getMessageListening1)
    }

    fun removeUpdateMessageListener(mixId: String) {
        chatsReference().child(mixId).removeEventListener(updateMessageListening)
    }
}