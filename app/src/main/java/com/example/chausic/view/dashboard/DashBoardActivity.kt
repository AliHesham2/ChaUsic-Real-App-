package com.example.chausic.view.dashboard

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chausic.R
import com.example.chausic.firebase.Initialize
import com.example.chausic.firebase.Music
import com.example.chausic.firebase.User
import com.example.chausic.view.util.FragmentsInstance
import com.google.firebase.messaging.FirebaseMessaging

class DashBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        Initialize.setupFireBase(this.resources)
        User.setCurrentUser(Initialize.getAuthInstance()?.currentUser)
        Music.musicInstance()
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            User.updateUserToken(it.result!!)
        }
        createChannel(getString(R.string.CHANNEL_ID), getString(R.string.CHANNEL_NAME))
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply { setShowBadge(true) }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            val notificationManager = this.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
    private fun updateUserStatOnline(){
        if(Initialize.getAuthInstance() != null){
            User.updateUserStatus(true)
        }
    }
    private fun updateUserStatOffline(){
        if(Initialize.getAuthInstance() != null){
            User.updateUserStatus(false)
        }
    }
    override fun onStart() {
        updateUserStatOnline()
        super.onStart()
    }

    override fun onPause() {
        updateUserStatOffline()
        super.onPause()
    }
    override fun onDestroy() {
        updateUserStatOffline()
        Music.clearInstance()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (FragmentsInstance.foreGroundFlag){
            FragmentsInstance.foreGroundFlag = false
            this.finish()
        }else{ super.onBackPressed() }
    }
}