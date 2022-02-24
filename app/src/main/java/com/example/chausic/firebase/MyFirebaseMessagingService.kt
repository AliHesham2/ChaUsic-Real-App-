package com.example.chausic.firebase

import android.app.NotificationManager
import androidx.core.content.ContextCompat
import com.example.chausic.R
import com.example.chausic.notification.sendNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    //Get the data from notification
    override fun onMessageReceived(data: RemoteMessage) {
        data.notification?.let { it ->
            sendNotification(it.body!!,it.title,data.data[resources.getString(R.string.NOTIFICATION_TYPE)],data.data[resources.getString(R.string.DATA_USERID)],data.data[resources.getString(R.string.DATA_MIXID)])
        }
    }
    //If the user signOut and Sign In again the new token generated !!
    override fun onNewToken(token: String) {
        User.updateUserToken(token)
    }
    //Send notification in foreground
    private fun sendNotification(body: String, title: String?, type: String?, userID: String?,mixID: String?) {
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.sendNotification(body,title,type,userID,mixID,applicationContext)
    }
}