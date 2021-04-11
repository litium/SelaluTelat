package net.adiman.selalutelat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import net.adiman.selalutelat.TelatConstants.akAPP_KILLED_BROADCAST_ACTION
import net.adiman.selalutelat.TelatConstants.akTAG
import net.adiman.selalutelat.Util.scheduleBackgroundJobAlarm

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationManager.scheduleNotifications(this)
        Log.d(akTAG, "notifications set")
    }

    override fun onPause() {
        super.onPause()

        scheduleBackgroundJobAlarm(this)
    }

    override fun onDestroy() {
        val broadcastIntent = Intent(akAPP_KILLED_BROADCAST_ACTION)
        sendBroadcast(broadcastIntent)
        Log.d(akTAG,"onDestroy. Sending $akAPP_KILLED_BROADCAST_ACTION broadcast")
        super.onDestroy()
    }
}