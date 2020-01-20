package io.zry.sms2telegram

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi


class MainService : Service() {

    companion object {
        private const val TAG = "MainService"
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind()")
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")
        val builder = Notification.Builder(
            this.applicationContext
        )
        val activityIntent = Intent(this, MainActivity::class.java)

        builder.setContentIntent(
            PendingIntent.getActivity(this, 0, activityIntent, 0)
        ).setLargeIcon(
            BitmapFactory.decodeResource(
                this.resources, R.mipmap.ic_launcher
            )
        ).setContentTitle(
            "SMS2Telegram"
        ).setSmallIcon(
            R.mipmap.ic_launcher
        ).setContentText(
            "Running"
        ).setWhen(
            System.currentTimeMillis()
        )

        val notification = builder.build()

        startForeground(326, notification)

        return super.onStartCommand(intent, flags, startId)
    }
}
