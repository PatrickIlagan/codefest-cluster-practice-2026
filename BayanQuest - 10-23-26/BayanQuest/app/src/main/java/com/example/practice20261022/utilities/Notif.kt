package com.example.practice20261022.utilities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.practice20261022.MainActivity

// 2. In MainActivity.kt onCreate() (Requests permission on Android 13+ / API 33+):
// if (Build.VERSION.SDK_INT >= 33) {
//     requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"), 1)
// }

// 3. Universal Extension Function:
fun Context.sendNotif(title: String, message: String) {
    val nm = getSystemService(NotificationManager::class.java)
    if (Build.VERSION.SDK_INT >= 26) {
        nm.createNotificationChannel(
            NotificationChannel("c", "Plant Alerts", NotificationManager.IMPORTANCE_HIGH)
        )
    }

    val intent = Intent(this, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        this,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE // Mandatory for targetSdk 31+
    )

    nm.notify(
        1,
        NotificationCompat.Builder(this, "c")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Built-in icon (Zero missing asset crashes!)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent) // Reopens MainActivity when tapped
            .setAutoCancel(true)             // Clears notification on tap
            .build()
    )
}
