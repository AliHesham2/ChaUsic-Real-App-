package com.example.chausic.view.dashboard.friends

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chausic.R
import com.example.chausic.firebase.*
import com.example.chausic.model.data.*



class FriendsViewModel(private val app:Application):AndroidViewModel(app) {

    private var _data = MutableLiveData<MutableList<UserChatData>>()
    val data : LiveData<MutableList<UserChatData>>
        get() = _data

    private val _loading = MutableLiveData<Boolean?>()
    val loading: LiveData<Boolean?>
        get() = _loading

    init {
        _loading.value = true
        getFriends()
    }

    //Get User Friends
    private fun getFriends(){
        Requests.getFriends { requests, type ->
            if (requests == null){
                stopLoading()
            }else {
                stopLoading()
                when (type) {
                    app.resources.getString(R.string.DATA_ADD) -> {
                        newFriendAdded(requests)
                    }
                    app.resources.getString(R.string.DATA_REMOVED) -> {
                        removeOldFriend(requests)
                    }
                }
            }
        }
        User.getChangedFriendsData{ userChangedData ->
            updateFriendData(userChangedData!!)
        }
    }

    private fun stopLoading(){
        if (_loading.value == true ){ _loading.value = false }
    }

    // < ----------------------------------------------------------------- List OF Friends Section --------------------------------------------------- >
    private fun newFriendAdded(requests: AcceptFriend) {
        User.getUserDataOnce(requests.friendID!!) {
            if (_data.value.isNullOrEmpty() && _data.value == null){
                _data.value = mutableListOf(UserChatData(it, ChatList(requests.mixID,requests.friendID,null,null)))
            }
            else{
                _data.value!!.add(UserChatData(it, ChatList(requests.mixID,requests.friendID,null,null)))
                _data.value = _data.value
            }
        }
    }
    private fun removeOldFriend(requests: AcceptFriend) {
        val index = _data.value!!.indexOfFirst { user -> user.chat.mixId == requests.mixID }
        if (index != -1){
            _data.value!!.removeAt(index)
            _data.value = _data.value
        }
    }

    fun removeUser(uid: String, id: String, mixId: String) {
        FriendList.removeFriend(uid,id,mixId)
    }

    // < ----------------------------------------------------------------- End  -------------------------------------------------------------------------- >


    // < ----------------------------------------------------------------- Friend Data Section ----------------------------------------------------------- >
    private fun updateFriendData(userChangedData: UserData) {
        val index = _data.value!!.indexOfFirst { user -> user.user.id == userChangedData.id }
        if (index != -1){
            _data.value!![index].user = userChangedData
            _data.value = _data.value
        }
    }
    // < ----------------------------------------------------------------- End  -------------------------------------------------------------------------- >

}