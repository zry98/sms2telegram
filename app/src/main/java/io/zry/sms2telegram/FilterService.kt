package io.zry.sms2telegram

import android.app.IntentService
import android.content.Intent

class FilterService : IntentService {

    constructor() : super("FilterService")

    constructor(name: String) : super(name)

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            return
        }

        val senderNumber = intent.getStringExtra("senderNumber")
        val message = intent.getStringExtra("message")
        if (!isBlacklisted(senderNumber, message)) {
            val serviceIntent = Intent(this.applicationContext, PushService::class.java)
            serviceIntent.putExtra("senderNumber", senderNumber)
            serviceIntent.putExtra("message", message)
            this.startService(serviceIntent)
        }
    }

    private fun isBlacklisted(senderNumber: String?, message: String?): Boolean {
        if (senderNumber.isNullOrBlank() || message.isNullOrBlank()) {
            return false
        }
        val prefs = Preferences(this)

        val blacklistedNumbers = prefs.blacklistedNumbers
        if (blacklistedNumbers.isNotEmpty()) {
            if (senderNumber in blacklistedNumbers) {
                return true
            }
        }
        val blacklistedKeywords = prefs.blacklistedKeywords
        if (blacklistedKeywords.isNotEmpty()) {
            for (keyword in blacklistedKeywords) {
                if (keyword in message) {
                    return true
                }
            }
        }

        return false
    }
}
