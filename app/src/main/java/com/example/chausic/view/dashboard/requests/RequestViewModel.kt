package com.example.chausic.view.dashboard.requests

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chausic.R
import com.example.chausic.firebase.Requests
import com.example.chausic.model.data.SendRequest

class RequestViewModel(app:Application):AndroidViewModel(app) {

    private var _data = MutableLiveData<List<SendRequest>>()
    val data : LiveData<List<SendRequest>>
        get() = _data

    init {
      getRequests()
    }

    private fun getRequests(){
        Requests.getFriendRequests{ data ->
            if (_data.value.isNullOrEmpty()) {
                _data.value = listOf(data)
            }else {
                _data.value = _data.value!!.plus(data)
            }
        }
    }

     fun onAccept(senderID: String?) {
         Requests.acceptFriendRequest(senderID!!)
         removeTheRequest(senderID)
    }
     fun onReject(senderID: String?) {
         Requests.rejectFriendRequest(senderID!!)
         removeTheRequest(senderID)
    }
    private fun removeTheRequest(senderID: String){
        val data = _data.value!!.find { data -> data.senderID == senderID }
        _data.value = _data.value!!.minusElement(data!!)
    }

    override fun onCleared() {
        Requests.removeFriendRequestsEventListener()
    }
}