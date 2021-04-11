package net.adiman.selalutelat

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import net.adiman.selalutelat.TelatConstants.akKEY_NOTIFICATION_TIME_EPOCH
import net.adiman.selalutelat.TelatConstants.akNOTIFICATION_CHANNEL_DEFAULT_ID
import net.adiman.selalutelat.TelatConstants.akTAG
import java.time.Instant
import java.util.*

class ReceiverNotification : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
        triggerNotification(context, intent)
	}

	private fun triggerNotification(context: Context, intent: Intent) {
        val timeFormat = android.text.format.DateFormat.getTimeFormat(context)
        val notificationTimeMilis = intent.getLongExtra(akKEY_NOTIFICATION_TIME_EPOCH, 0)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val azanTime = timeFormat.format(Date.from(Instant.ofEpochMilli(notificationTimeMilis)))
        var notificationText = "On time Notification($azanTime)"

        //just compare up to seconds to see if the azan time has passed
        if (notificationTimeMilis/1000 < Date().toInstant().epochSecond) {
            //delayed passed notifications
            notificationText = "Delayed notification($azanTime)"
        }

        Log.d(akTAG,"Received notification")

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(context, akNOTIFICATION_CHANNEL_DEFAULT_ID)
            .setContentTitle("telat")
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), AudioManager.STREAM_ALARM)
            .setWhen(notificationTimeMilis)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(0, notification)
    }
}

