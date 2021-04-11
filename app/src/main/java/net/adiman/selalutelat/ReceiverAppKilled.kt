package net.adiman.selalutelat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import net.adiman.selalutelat.TelatConstants.akAPP_KILLED_BROADCAST_ACTION
import net.adiman.selalutelat.TelatConstants.akTAG

class ReceiverAppKilled: BroadcastReceiver() {

	override fun onReceive(context: Context, intent: Intent) {
		if (intent.action == akAPP_KILLED_BROADCAST_ACTION) {
			Log.d(akTAG, "APP_KILLED broadcast received. Executing scheduleJobAlarm service.")
			Util.scheduleBackgroundJobAlarm(context)
		}
	}
}