package com.example.chausic.view.dashboard.config

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import com.example.chausic.firebase.Notification

import com.example.chausic.view.util.PopUpMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


class DashBoardViewModel(app:Application): AndroidViewModel(app) {

     fun sendNotification(requireView: View, token: String, currentName: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Notification.sendNewRequestNotification(token, currentName!!,requireView.resources,requireView)
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    PopUpMsg.fireBaseError(requireView,e)
                }
            }
        }
    }
}