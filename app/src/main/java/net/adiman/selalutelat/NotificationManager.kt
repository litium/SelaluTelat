package net.adiman.selalutelat

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager

import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import net.adiman.selalutelat.TelatConstants.akNOTIFICATION_CHANNEL_DEFAULT_DESC
import net.adiman.selalutelat.TelatConstants.akNOTIFICATION_CHANNEL_DEFAULT_ID
import net.adiman.selalutelat.TelatConstants.akNOTIFICATION_CHANNEL_DEFAULT_NAME
import net.adiman.selalutelat.TelatConstants.akSHARED_PREF_MODE
import net.adiman.selalutelat.TelatConstants.akSHARED_PREF_NAME
import net.adiman.selalutelat.TelatConstants.akSHARED_PREF_REQUEST_CODES_KEY
import net.adiman.selalutelat.TelatConstants.akTAG
import java.time.Instant
import java.util.*

object NotificationManager {
	private const val PENDING_INTENT_FLAG = PendingIntent.FLAG_ONE_SHOT

	fun scheduleNotifications(context: Context) {
		val sharedPreferences = context.getSharedPreferences(akSHARED_PREF_NAME, akSHARED_PREF_MODE)
        val settingsPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val intCodes = arrayListOf<Int>()

		cancelScheduledAlarms(context)
		createNotificationChannel(context, akNOTIFICATION_CHANNEL_DEFAULT_ID, akNOTIFICATION_CHANNEL_DEFAULT_NAME, akNOTIFICATION_CHANNEL_DEFAULT_DESC)

        intCodes.add(scheduleAlarmManager(Date().toInstant().plusSeconds(60), "test1 - 1 min", context))
        intCodes.add(scheduleAlarmManager(Date().toInstant().plusSeconds(360), "test2 - 6 min", context))
        intCodes.add(scheduleAlarmManager(Date().toInstant().plusSeconds(600), "test3 - 10 min", context))

		sharedPreferences.edit().putIntArray(akSHARED_PREF_REQUEST_CODES_KEY, intCodes.toIntArray()).apply()
        Log.d(akTAG,"scheduled ${intCodes.size} alarms")
    }

	private fun removeCurrentChannel(context: Context, channelId: String) {
		val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		//debugLog("cancelling ${notificationManager.activeNotifications.size} notifications")
		notificationManager.deleteNotificationChannel(channelId)
    }

	private fun cancelScheduledAlarms(context: Context) {
        val sharedPreferences = context.getSharedPreferences(akSHARED_PREF_NAME, akSHARED_PREF_MODE)
        val intent = Intent(context, ReceiverNotification::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val requestCodes = sharedPreferences.getIntArray(akSHARED_PREF_REQUEST_CODES_KEY)

        for (requestCode in requestCodes) {
            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PENDING_INTENT_FLAG)
            //debugLog("cancelling pending alarm intent code: $requestCode")
            pendingIntent.cancel()
            alarmManager.cancel(pendingIntent)
        }
        Log.d(akTAG,"removed ${requestCodes.size} alarms")
    }

	private fun createNotificationChannel(context: Context, channelId: String, name: String, desc: String) {
		// The user-visible name of the channel.
		val channelName: CharSequence = name
		// The user-visible description of the channel.
		val channelDescription: String = desc
		val channelImportance: Int = NotificationManager.IMPORTANCE_HIGH
		val channelEnableVibrate = true
		val channelLockscreenVisibility: Int = NotificationCompat.VISIBILITY_PUBLIC
		val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
            .build()

		// Initializes NotificationChannel.
		val notificationChannel = NotificationChannel(channelId, channelName, channelImportance)
		notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes)
		notificationChannel.description = channelDescription
		notificationChannel.enableVibration(channelEnableVibrate)
		notificationChannel.lockscreenVisibility = channelLockscreenVisibility
		notificationChannel.setShowBadge(false)

		// Adds NotificationChannel to system. Attempting to create an existing notification
		// channel with its original values performs no operation, so it's safe to perform the
		// below sequence.
		val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.createNotificationChannel(notificationChannel)
	}

	private fun scheduleAlarmManager(alarmTime: Instant, locationName: String?, context: Context): Int {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val uniqueRequestCode = UUID.randomUUID().hashCode() and Int.MAX_VALUE

		if (alarmTime.isBefore(Date().toInstant())) {
            return 0
        }

        val intent = Intent(context, ReceiverNotification::class.java)
        intent.putExtra(TelatConstants.akKEY_NOTIFICATION_TIME_EPOCH, alarmTime.toEpochMilli())

        val pendingIntent = PendingIntent.getBroadcast(context, uniqueRequestCode, intent, PENDING_INTENT_FLAG)

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime.toEpochMilli(), pendingIntent)
        Log.d(akTAG, "in scheduleAlarmManager. Queued: $uniqueRequestCode At: ${Date.from(alarmTime)}")

        return uniqueRequestCode
    }
}