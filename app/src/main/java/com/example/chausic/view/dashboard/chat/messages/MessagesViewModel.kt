package com.example.chausic.view.dashboard.chat.messages

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chausic.R
import com.example.chausic.firebase.*
import com.example.chausic.model.data.ChatData
import com.example.chausic.model.data.Theme
import com.example.chausic.model.data.UserChatData
import com.example.chausic.model.data.UserData
import com.example.chausic.view.util.PopUpMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MessagesViewModel(
    private val app: Application,
    private val userData: UserChatData,
) :AndroidViewModel(app){

    private lateinit var currentUser:UserData

    private var _data = MutableLiveData<UserData>()
    val data : LiveData<UserData>
        get() = _data

    private var _otherUserStatus = MutableLiveData<Boolean>()
    val otherUserStatus : LiveData<Boolean>
        get() = _otherUserStatus

    private var _ifTyping = MutableLiveData<Boolean>()
    val ifTyping : LiveData<Boolean>
        get() = _ifTyping

    private var _theme = MutableLiveData<Theme>()
    val theme : LiveData<Theme>
        get() = _theme

    private var _chatData = MutableLiveData<MutableList<ChatData>>()
    val chatData : LiveData<MutableList<ChatData>>
        get() = _chatData

    init {
        getUserTheme()
        loadUserData()
        detectOtherUser()
        detectIfTyping()
        getMessages(userData.chat.mixId!!)
    }

    private fun getUserTheme(){
        User.getCurrentUserData { it,_ ->
            if(it != null) {
                currentUser = it
                Themes.getTheme(it.themeID!!) { themeURL ->
                    _theme.value = themeURL
                }
            }
        }
    }

    private fun loadUserData() {
        User.getUserData(userData.user.id!!) {
            _data.value = it
        }
    }


    // < ----------------------------------------------------------------- Msg Section ----------------------------------------------------------- >
    private fun getMessages(mixID:String){
        Message.getMessagesNew(mixID,User.getCurrentUser()!!.uid,app){ chatData, type ->
            when(type){
                app.resources.getString(R.string.DATA_ADD)->{ addNewMessages(chatData) }
               app.resources.getString(R.string.DATA_CHANGED)->{ updateOldMessage(chatData) }
            }
        }
    }

    private fun addNewMessages(chatData: ChatData) {
        if (_chatData.value.isNullOrEmpty()){
            _chatData.value = mutableListOf(chatData)
        }else{
            _chatData.value!!.add(chatData)
            _chatData.value = _chatData.value
        }
    }

    private fun updateOldMessage(chatData: ChatData) {
        if(_chatData.value != null || _chatData.value!!.isNotEmpty()){
           val index =  _chatData.value!!.indexOfFirst { data -> data.id == chatData.id }
            if (index != -1){
                _chatData.value!![index] = chatData
                _chatData.value = _chatData.value
            }
        }
    }

    fun sendMessage(senderID: String, receiverID: String, msg: String, requireView: View){
        val isSeen = _otherUserStatus.value!!
        Message.sendMessage(senderID,receiverID,msg,isSeen,requireView,userData.chat.mixId!!)
        if(_chatData.value.isNullOrEmpty()){
            IfOnline.checkIfCreated(userData.chat.mixId!!,senderID,receiverID)
        }
        if(_otherUserStatus.value == false){
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    Notification.sendMessageNotification(userData.user.token!!,currentUser.name!!,app.resources,msg,requireView,userData.user.id!!, userData.chat.mixId!!)
                }catch (e:Exception){
                    PopUpMsg.fireBaseError(requireView,e)
                }
            }
        }
    }
    // < ----------------------------------------------------------------- End ----------------------------------------------------------------------------------------- >

    // < ----------------------------------------------------------------- Detect User online/typing Section ----------------------------------------------------------- >
    private fun detectOtherUser(){
        // if online or not
        IfOnline.detectUserCurrentState(userData.chat.mixId!!,userData.user.id!!){
            _otherUserStatus.value = it
        }
    }


    private fun detectIfTyping(){
        IfOnline.detectUserIfTyping(userData.chat.mixId!!,userData.user.id!!){
            _ifTyping.value = it
        }
    }
    // < ----------------------------------------------------------------- End ----------------------------------------------------------------------------------------- >

    // < ----------------------------------------------------------------- Update User online/typing Section ----------------------------------------------------------- >
    fun updateCurrentUser(online:Boolean){
        IfOnline.updateStatus(userData.chat.mixId!!,User.getCurrentUser()!!.uid,online)
    }

    fun updateIfTyping(typing:Boolean){
        IfOnline.updateTyping(userData.chat.mixId!!,User.getCurrentUser()!!.uid,typing)
    }
    // < ----------------------------------------------------------------- End ----------------------------------------------------------------------------------------- >

    override fun onCleared() {
        Message.removeGetMessageListener(userData.chat.mixId!!)
        Message.removeUpdateMessageListener(userData.chat.mixId!!)
        User.removeGetOtherUserDataEventListener(userData.user.id!!)
        User.removeGetCurrentUserDataEventListener()
        IfOnline.removeListenerOfDetectUserCurrentState(userData.chat.mixId!!,userData.user.id!!)
        super.onCleared()
    }
}