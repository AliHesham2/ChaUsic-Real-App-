package com.example.chausic.view.dashboard.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chausic.model.data.UserChatData
import com.example.chausic.model.data.UserData

class UserViewModel(app: Application,private val userData: UserChatData):AndroidViewModel(app) {

    private var _data = MutableLiveData<UserData>()
    val data : LiveData<UserData>
        get() = _data

    init {
       loadUserData()
    }

    private fun loadUserData(){
        _data.value = userData.user
    }
}