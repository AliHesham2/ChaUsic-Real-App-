package com.example.chausic.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.example.chausic.R
import com.example.chausic.view.dashboard.DashBoardActivity
import com.example.chausic.view.util.PopUpMsg

fun NotificationManager.sendNotification(body: String, title: String?, type: String?, userID: String?, mixID: String?, applicationContext: Context) {
    val contentIntent = Intent(applicationContext, DashBoardActivity::class.java)
    contentIntent.putExtra( applicationContext.resources.getString(R.string.NOTIFICATION_TYPE) ,type )
    contentIntent.putExtra( applicationContext.resources.getString(R.string.NOTIFICATION_FOREGROUND) ,1 )
    userID?.let {  contentIntent.putExtra( applicationContext.resources.getString(R.string.DATA_USERID) , it ) }
    mixID?.let {   contentIntent.putExtra( applicationContext.resources.getString(R.string.DATA_MIXID) , it  ) }
    val contentPendingIntent = PendingIntent.getActivity(applicationContext, PopUpMsg.NOTIFICATION_ID,contentIntent,PendingIntent.FLAG_UPDATE_CURRENT)

    val iconImage = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.icon)
    val bigPicStyle = NotificationCompat.BigPictureStyle().bigPicture(iconImage).bigLargeIcon(null)

    // Build the notification
    val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.CHANNEL_ID))
        //small icon behind the name of app
        .setSmallIcon(R.drawable.icon)
        .setContentTitle(title)
        .setContentText(body)
        .setContentIntent(contentPendingIntent)
        .setStyle(bigPicStyle)
        .setLargeIcon(iconImage)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
         notify(PopUpMsg.NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
