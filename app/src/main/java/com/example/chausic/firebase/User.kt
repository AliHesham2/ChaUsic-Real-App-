package com.example.chausic.firebase

import android.content.Intent
import android.view.View
import androidx.core.net.toUri
import com.example.chausic.R
import com.example.chausic.model.data.UserData
import com.example.chausic.view.dashboard.config.DashBoardFragment
import com.example.chausic.view.splash.MainActivity
import com.example.chausic.view.util.PopUpMsg
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import java.io.File

object User {

    private lateinit var getCurrentUserDataEventListener: ValueEventListener
    private lateinit var getOtherUserDataEventListener: ValueEventListener
    private lateinit var getAllOtherUserDataChildListener:ChildEventListener
    private var currentUser:FirebaseUser? = null

    //Get The currentUser
    fun setCurrentUser(currentUser: FirebaseUser?) {
            this.currentUser = currentUser
    }

    //Return instance of currentUser
    fun getCurrentUser(): FirebaseUser? {
       return currentUser
    }

    //Get user`s DataBase reference By Id
    private fun currentUserReference(): DatabaseReference {
        return Initialize.getDataBaseInstance()!!.getReference(Initialize.getResourceInstance().getString(R.string.USERS)).child(currentUser!!.uid)
    }


    //Get user`s DataBase reference
    private fun userReference(): DatabaseReference {
        return Initialize.getDataBaseInstance()!!.getReference(Initialize.getResourceInstance().getString(R.string.USERS))
    }


    //Storage for pictures
    private fun userStorageReference(): StorageReference {
        return Initialize.getStorageInstance()!!.getReference(Initialize.getResourceInstance().getString(R.string.UPLOADS))
    }

    //Create DB with User Info
     fun setupUser(userData: UserData, requireView: View, isUserDataAdded:(isAdded:Boolean)->Unit) {
            currentUserReference().setValue(userData)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    //If  created DB with  user data then return true to SignUp Auth
                    isUserDataAdded(true)
                }
            }
            .addOnFailureListener {
                //If  Error in   create DB with  user data then return false to SignUp Auth
                isUserDataAdded(false)
                PopUpMsg.alertMsg(requireView, requireView.resources.getString(R.string.AUTH_FAIL))
            }
    }

    //Get CurrentUser Data
    fun getCurrentUserData(changeListener: (data: UserData?,flag:Boolean) -> Unit){
           getCurrentUserDataEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val  userProfileData = snapshot.getValue<UserData>()
                if (userProfileData != null) {
                    changeListener(userProfileData,true)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                changeListener(null,false)
            }
        }
        currentUserReference().addValueEventListener(getCurrentUserDataEventListener)
    }

    //Get Specific User Data
    fun getUserData(receiverID:String, changeListener: (data: UserData) -> Unit){
        getOtherUserDataEventListener =  object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val  userProfileData = snapshot.getValue<UserData>()
                if (userProfileData != null) {
                    changeListener(userProfileData)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        userReference().child(receiverID).addValueEventListener(getOtherUserDataEventListener)
    }

    //get changeable friend data
    fun getChangedFriendsData(dataOfUsers: (changedData:UserData?) -> Unit){
            getAllOtherUserDataChildListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val userData = snapshot.getValue<UserData>()
                    if(userData != null){ dataOfUsers(userData) } }
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            }
            userReference().addChildEventListener(getAllOtherUserDataChildListener)
        }

    //Get CurrentUser Data One Time Without update changes
    fun getCurrentUserDataOnce(currentUserData:(user:UserData) -> Unit){
         currentUserReference().get()
             .addOnSuccessListener { if(it.value != null ){ currentUserData(it.getValue<UserData>()!!) } }
             .addOnFailureListener {  }
    }

    //Get UserData One Time Without update changes
    fun getUserDataOnce(id:String,currentUserData:(user:UserData) -> Unit){
        userReference().child(id).get()
            .addOnSuccessListener { if(it.value != null ){ currentUserData(it.getValue<UserData>()!!) } }
            .addOnFailureListener {  }
    }

    //Update User Name
    fun updateUserName(name: String, requireView: View){
        val map: HashMap<String, Any> = HashMap()
        map[requireView.resources.getString(R.string.NAME)] = name
        currentUserReference().updateChildren(map).addOnFailureListener {
            PopUpMsg.fireBaseError(requireView,it)
        }
    }

    //Update User Bio
    fun updateUserBio(bio: String, requireView: View){
        val map: HashMap<String, Any> = HashMap()
        map[requireView.resources.getString(R.string.BIO)] = bio
        currentUserReference().updateChildren(map).addOnFailureListener {
            PopUpMsg.fireBaseError(requireView,it)
        }
    }

    //Update Profile Picture
        private fun updateUserImage(result: String, requireView: View , isImageSetToDB:(isUpdated:Boolean) -> Unit) {
        val map: HashMap<String, Any> = HashMap()
        map[requireView.resources.getString(R.string.IMG)] = result
        currentUserReference().updateChildren(map).addOnCompleteListener {
            isImageSetToDB(true)
        }.addOnFailureListener {
            isImageSetToDB(false)
            PopUpMsg.fireBaseError(requireView,it)
        }
    }

    //Update User status
    fun updateUserStatus(status: Boolean){
        val map: HashMap<String, Any> = HashMap()
        map[Initialize.getResourceInstance().getString(R.string.STATUS)] = status
        currentUserReference().updateChildren(map)
    }

    //Update User Theme
    fun updateUserTheme(id: String, requireView: View){
        val map: HashMap<String, Any> = HashMap()
        map[requireView.resources.getString(R.string.THEME_ID)] = id
        currentUserReference().updateChildren(map).addOnFailureListener {
            PopUpMsg.fireBaseError(requireView,it)
        }
    }

    //Upload Profile Picture
    fun uploadUserImage(filepath: String, requireView: View , isImageUploaded:(isUploaded:Boolean) -> Unit){
        val  file = File(filepath)
        val filename = System.currentTimeMillis().toString() + "-" + file.name
        val ref =  userStorageReference().child(filename)
        ref.putFile(file.toUri()).continueWithTask { task ->
            if (!task.isSuccessful) {
                isImageUploaded(false)
                task.exception?.let {
                    PopUpMsg.fireBaseError(requireView,it)
                }
            }
             ref.downloadUrl
        }.addOnCompleteListener { task ->
            if(task.isSuccessful){
                val downloadUri = task.result
                 updateUserImage(downloadUri.toString(),requireView) { isUpdated ->
                     isImageUploaded(isUpdated)
                 }
            }
        }.addOnFailureListener {
            isImageUploaded(false)
            PopUpMsg.fireBaseError(requireView,it)
        }
    }

    //UserToken
    fun updateUserToken(token: String) {
        val map: HashMap<String, Any> = HashMap()
        map[Initialize.getResourceInstance().getString(R.string.TOKEN)] = token
        if (currentUser != null){
            currentUserReference().updateChildren(map)
        }
    }

    //Check user Auth
    fun checkUserAuth(): Boolean {
        return Initialize.getAuthInstance()!!.currentUser != null
    }

    //SignOut the User
    fun signOut(currentFragment: DashBoardFragment) {
        updateUserStatus(false)
        removeAllListeners()
        Initialize.getAuthInstance()!!.signOut()
        Initialize.destroyFireBase()
        FirebaseMessaging.getInstance().deleteToken().addOnSuccessListener {
            val intent = Intent(currentFragment.requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            currentFragment.startActivity(intent)
        }
    }

    private fun removeCurrentUser(){ currentUser = null }

    fun removeGetOtherUserDataEventListener(id: String) {
        if (this::getOtherUserDataEventListener.isInitialized) {
            userReference().child(id).removeEventListener(getOtherUserDataEventListener)
        }
    }
    fun removeGetCurrentUserDataEventListener() {
        if (this::getCurrentUserDataEventListener.isInitialized) {
            currentUserReference().removeEventListener(getCurrentUserDataEventListener)
        }
    }
    private fun removeGetAllOtherUserDataEventListener() {
        if (this::getAllOtherUserDataChildListener.isInitialized) {
            userReference().removeEventListener(getAllOtherUserDataChildListener)
        }
    }
    private fun removeAllListeners(){
        removeGetAllOtherUserDataEventListener()
        removeGetCurrentUserDataEventListener()
        Requests.removeFriendsEventListener()
        ChatListData.removeListenerOfGetChatListIDsOfCurrentUser()
        removeCurrentUser()
    }
}