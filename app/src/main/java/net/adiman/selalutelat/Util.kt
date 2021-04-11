package net.adiman.selalutelat

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object Util {
    fun scheduleBackgroundJobAlarm(context: Context) {
		val serviceName = ComponentName(context, ServiceAlarmJob::class.java)
		val job = JobInfo.Builder(2000, serviceName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING)
            .setPeriodic(15*60000) //15 minutes
			.build()

		var jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
		jobScheduler.schedule(job)

        Log.d("telat", "Background job scheduler set")
	}
}

fun SharedPreferences.Editor.putIntArray(key: String, value: IntArray): SharedPreferences.Editor {
    return putString(key, value.joinToString(
            separator = ",",
            transform = { it.toString() }))
}

fun SharedPreferences.getIntArray(key: String): IntArray {
    with(getString(key, "")) {
        with(if(this?.isNotEmpty() == true) this.split(',') else return intArrayOf()) {
            return IntArray(this.count()) { this.get(it).toInt() }
        }
    }
}