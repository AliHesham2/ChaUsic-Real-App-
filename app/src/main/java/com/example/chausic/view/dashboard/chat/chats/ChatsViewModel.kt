package com.example.chausic.view.dashboard.chat.chats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chausic.R
import com.example.chausic.firebase.ChatListData
import com.example.chausic.firebase.User
import com.example.chausic.model.data.*



class ChatsViewModel(private val app: Application): AndroidViewModel(app) {

    private val _data = MutableLiveData<MutableList<UserChatData>>()
    val data : LiveData<MutableList<UserChatData>>
        get() = _data

    private val _loading = MutableLiveData<Boolean?>()
    val loading: LiveData<Boolean?>
        get() = _loading

    init {
        _loading.value = true
        getChatList()
    }

    //Get User Friends
    private fun getChatList(){
        ChatListData.getChatListIDsOfCurrentUser { data, type ->
            if (data == null ){
               stopLoading()
            } else {
                when (type) {
                    app.resources.getString(R.string.DATA_ADD) -> { newChatListAdd(data) }
                    app.resources.getString(R.string.DATA_CHANGED) -> { chatListChange(data) }
                    app.resources.getString(R.string.DATA_REMOVED) -> { removeChatList(data) }
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

    // < ----------------------------------------------------------------- List OF Chats Section --------------------------------------------------- >
    private fun newChatListAdd(data: ChatList) {
        User.getUserDataOnce(data.id!!) {
            if (_data.value.isNullOrEmpty() && _data.value == null){
                _data.value = mutableListOf(UserChatData(it,ChatList(data.mixId,data.id,data.lastMsg,data.seen)))
                stopLoading()
            }
            else{
                _data.value!!.add(UserChatData(it,ChatList(data.mixId,data.id,data.lastMsg,data.seen)))
                _data.value = _data.value
            }
        }
    }

    private fun chatListChange(data: ChatList) {
        if (_data.value != null && !_data.value.isNullOrEmpty()) {
            val index = _data.value!!.indexOfFirst { user -> user.chat.mixId == data.mixId }
            if (index != -1) {
                _data.value!![index].chat.lastMsg = data.lastMsg
                _data.value = _data.value
            }
        }
    }

    private fun removeChatList(data: ChatList) {
        if (_data.value != null && !_data.value.isNullOrEmpty()) {
            val index = _data.value!!.indexOfFirst { user -> user.chat.mixId == data.mixId }
            if (index != -1) {
                _data.value!!.removeAt(index)
                _data.value = _data.value
            }
        }
    }

    fun removeChat(uid: String, id: String, mixId: String) {
        ChatListData.removeTheChatFromOneSide(uid,id,mixId)
    }
    // < ----------------------------------------------------------------- End  -------------------------------------------------------------------------- >

    // < ----------------------------------------------------------------- Friend Data Section ----------------------------------------------------------- >
    private fun updateFriendData(userChangedData: UserData) {
        if (_data.value != null && !_data.value.isNullOrEmpty()) {
            val index = _data.value!!.indexOfFirst { user -> user.user.id == userChangedData.id }
            if (index != -1) {
                _data.value!![index].user = userChangedData
                _data.value = _data.value
            }
        }
    }
    // < ----------------------------------------------------------------- End  -------------------------------------------------------------------------- >

}
