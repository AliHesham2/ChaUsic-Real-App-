package com.example.chausic.firebase


import android.view.View
import com.example.chausic.R
import com.example.chausic.model.data.AcceptFriend
import com.example.chausic.model.data.SendRequest
import com.example.chausic.model.data.UserData
import com.example.chausic.view.util.PopUpMsg
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue


object Requests {

    private  lateinit var  friendRequestValueListener:ChildEventListener
    private  lateinit var  friendValueListener:ChildEventListener
    //Get user`s Friends from DataBase reference
    private fun friendsReference(): DatabaseReference {
        return Initialize.getDataBaseInstance()!!.getReference(Initialize.getResourceInstance().getString(R.string.FRIENDS))
    }
    //Get user`s FriendRequest  from DataBase reference
    private fun friendsRequestReference(): DatabaseReference {
        return Initialize.getDataBaseInstance()!!.getReference(Initialize.getResourceInstance().getString(R.string.FRIENDS_REQUESTS))
    }
    //Get user`s DataBase reference
    private fun userReference(): DatabaseReference {
        return Initialize.getDataBaseInstance()!!.getReference(Initialize.getResourceInstance().getString(R.string.USERS))
    }

    //Check if User Exist id ->{other user}
    fun findIFUserExist(id:String, requireView: View, isUserExist:(isFound:Boolean,token:String?,currentName:String?) ->Unit) {
        userReference().child(id).get().addOnSuccessListener {
            if(it.value != null && id != User.getCurrentUser()!!.uid){
                val otherUser = it.getValue<UserData>()
                User.getCurrentUserDataOnce { currentUser ->
                    sendAddFriendRequest(id,currentUser.id!!, requireView, currentUser.img, currentUser.name) { isSent ->
                        isUserExist(isSent,otherUser!!.token,currentUser.name!!)
                    }
                }
            }else{
                isUserExist(false,null,null)
            }
        }.addOnFailureListener{
            isUserExist(false,null,null)
            PopUpMsg.fireBaseError(requireView,it)
        }
    }

    // When user send friend request ->  Create Child of Users who is get the request ,name it with their id and enter list of Children  (users who send requests) with (id/name/pic/status)
    private fun sendAddFriendRequest(id:String,currentUserID: String, requireView: View, img: String?, name: String?, isRequestSent:(isSent:Boolean) -> Unit) {
        val requestData = SendRequest(currentUserID,img,name)
        friendsRequestReference().child(id).child(currentUserID).setValue(requestData)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    isRequestSent(true)
                }
            }
            .addOnFailureListener {
                isRequestSent(false)
                PopUpMsg.fireBaseError(requireView,it)
            }
    }

    //Get list of Friends
    fun getFriends(userRequests:(changedData:AcceptFriend?,type:String) -> Unit){
        friendsReference().child(User.getCurrentUser()!!.uid).get().addOnSuccessListener {
            if(!it.hasChildren()){ userRequests(null,Initialize.getResourceInstance().getString(R.string.DATA_ADD))}
            friendValueListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val data = snapshot.getValue<AcceptFriend>()
                    if(data != null){
                        userRequests(data,Initialize.getResourceInstance().getString(R.string.DATA_ADD))
                    }
                }
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val data = snapshot.getValue<AcceptFriend>()
                    if(data != null){ userRequests(data,Initialize.getResourceInstance().getString(R.string.DATA_REMOVED)) }
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            }
            friendsReference().child(User.getCurrentUser()!!.uid).addChildEventListener(friendValueListener)
        }
    }

    //Get list of Users who sent Request to Current User
    fun getFriendRequests(userRequests:(requests : SendRequest) -> Unit){
        friendRequestValueListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()){
                    val data = snapshot.getValue<SendRequest>()
                    if(data != null){
                        userRequests(data)
                    }
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }
        friendsRequestReference().child(User.getCurrentUser()!!.uid).addChildEventListener(friendRequestValueListener)
    }



    //Update friend status to "accepted" in (Child of -> Current user ) and create Child with Sender ID and set status to "accepted" in (Child of -> Sender ID )
    fun acceptFriendRequest(senderID: String) {
        val mixID = senderID + "-" + User.getCurrentUser()!!.uid
        friendsRequestReference().child(User.getCurrentUser()!!.uid).child(senderID).removeValue().addOnFailureListener { }
        friendsReference().child(User.getCurrentUser()!!.uid).child(senderID).setValue(AcceptFriend(friendID = senderID, mixID = mixID)).addOnFailureListener { }
        friendsReference().child(senderID).child(User.getCurrentUser()!!.uid).setValue(AcceptFriend(friendID = User.getCurrentUser()!!.uid, mixID = mixID)).addOnFailureListener { }
            }

        //Remove Child of Sender ID
        fun rejectFriendRequest(senderID: String?) {
            friendsRequestReference().child(User.getCurrentUser()!!.uid).child(senderID!!).removeValue().addOnFailureListener { }
        }

    fun removeFriendsEventListener(){
        if (this::friendValueListener.isInitialized) {
            friendsReference().child(User.getCurrentUser()!!.uid).removeEventListener(friendValueListener)
        }
    }

    fun removeFriendRequestsEventListener(){
        friendsRequestReference().child(User.getCurrentUser()!!.uid).removeEventListener(friendRequestValueListener)
    }

}