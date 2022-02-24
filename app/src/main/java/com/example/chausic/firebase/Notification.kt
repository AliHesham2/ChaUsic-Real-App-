package com.example.chausic.firebase

import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.core.content.ContextCompat
import com.example.chausic.R
import com.example.chausic.model.data.FcmData
import com.example.chausic.model.data.FcmDetail
import com.example.chausic.model.data.FcmModel
import com.example.chausic.model.service.FcmApi
import com.example.chausic.notification.cancelNotifications
import com.example.chausic.view.util.PopUpMsg

object Notification {
    //Send Notification when user send message
    suspend fun sendMessageNotification(token: String, userName: String, resources: Resources, msg: String, requireView: View, userId: String, mixId: String) {
        val response = FcmApi.fcm.sendNotification(FcmModel(token, FcmDetail("$userName : $msg", resources.getString(R.string.NEW_MESSAGE)),
                FcmData(type = resources.getString(R.string.DATA_CHAT), userID = userId, mixID = mixId)))
        if (!response.isSuccessful) {
            PopUpMsg.alertMsg(requireView,resources.getString(R.string.WRONG))
        }
    }
    //Send Notification when user add friend
    suspend fun sendNewRequestNotification(token: String, userName: String, resources: Resources, requireView: View) {
        val response = FcmApi.fcm.sendNotification(FcmModel(token, FcmDetail(userName+" "+resources.getString(R.string.REQUEST_BODY), resources.getString(R.string.NEW_REQUEST)) ,
                FcmData(type = resources.getString(R.string.DATA_REQUEST)) ))
        if (!response.isSuccessful) {
            PopUpMsg.alertMsg(requireView,resources.getString(R.string.WRONG))
        }
    }
    //Cancel the Notifications
     fun cancelNotification(context: Context) {
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
        notificationManager.cancelNotifications()
    }



}