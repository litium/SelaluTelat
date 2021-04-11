package net.adiman.selalutelat

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.SharedPreferences
import android.location.Address
import android.location.Location
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.adiman.selalutelat.TelatConstants.akSHARED_PREF_MODE
import net.adiman.selalutelat.TelatConstants.akSHARED_PREF_NAME
import net.adiman.selalutelat.TelatConstants.akTAG

class ServiceAlarmJob: JobService() {
	var jobCancelled = false

	override fun onStartJob(params: JobParameters?): Boolean {
		val sharedPreferences: SharedPreferences = getSharedPreferences(akSHARED_PREF_NAME, akSHARED_PREF_MODE)

		Log.d(akTAG, "onStartJob")

        if (jobCancelled) {
			jobFinished(params, false)
			Log.d(akTAG, "Job finished")
			return true
		}

        CoroutineScope(Dispatchers.IO).launch {
            updateJob(params)
        }

		return true
	}

	override fun onStopJob(params: JobParameters?): Boolean {
		Log.d(akTAG, "onStopJob")
		jobCancelled = true
		return true
	}

	private fun updateJob(params: JobParameters?) {
		NotificationManager.scheduleNotifications(applicationContext)
        jobFinished(params, false)
	}
}