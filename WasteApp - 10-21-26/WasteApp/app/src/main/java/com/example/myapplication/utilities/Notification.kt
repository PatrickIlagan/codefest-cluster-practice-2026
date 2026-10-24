package com.example.myapplication.utilities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivity

fun Context.sendNotif(title: String, message: String){
    val nm = getSystemService(NotificationManager::class.java)
    if(Build.VERSION.SDK_INT >= 26){
        nm.createNotificationChannel(
            NotificationChannel("c", "Plant Notification", NotificationManager.IMPORTANCE_HIGH)
        )
    }


    val intent = Intent(this, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        this,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    nm.notify(
        1,
        NotificationCompat.Builder(this, "c")
            .setSmallIcon(android.R.drawable.ic_menu_send)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    )
}