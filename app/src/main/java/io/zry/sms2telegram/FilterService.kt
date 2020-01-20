package io.zry.sms2telegram

import android.app.IntentService
import android.content.Intent

class FilterService : IntentService {

    companion object {
        private const val TAG = "FilterService"
        private lateinit var prefs: Preferences
    }

    constructor() : super("FilterService")

    constructor(name: String) : super(name)

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            return
        }
        val senderNumber = intent.getStringExtra("senderNumber")!!
        val receiveDateTime = intent.getStringExtra("receiveDateTime")!!
        val messageBody = intent.getStringExtra("messageBody")!!

        if (!isBlacklisted(senderNumber, messageBody)) {
            val serviceIntent = Intent(this.applicationContext, PushService::class.java)

            serviceIntent.putExtra("senderNumber", senderNumber)
            serviceIntent.putExtra("receiveDateTime", receiveDateTime)
            serviceIntent.putExtra("messageBody", messageBody)

            this.startService(serviceIntent)
        }
    }

    private fun isBlacklisted(senderNumber: String, message: String): Boolean {
        if (senderNumber.isBlank() || message.isBlank()) {
            return true
        }
        prefs = Preferences(this)

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
