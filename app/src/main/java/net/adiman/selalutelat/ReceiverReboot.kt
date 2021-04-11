package net.adiman.selalutelat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import net.adiman.selalutelat.TelatConstants.akTAG

class ReceiverReboot: BroadcastReceiver() {

	override fun onReceive(context: Context, intent: Intent) {
		if (intent.action == "android.intent.action.BOOT_COMPLETED") {
			Log.d(akTAG, "BOOT_COMPLETED broadcast received. Executing starter service")
			Util.scheduleBackgroundJobAlarm(context)
		}
	}
}