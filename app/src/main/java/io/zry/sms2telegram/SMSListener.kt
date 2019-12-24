package io.zry.sms2telegram

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony


class SMSListener : BroadcastReceiver() {

    companion object {
        private const val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SMS_RECEIVED) {
            val bundle = intent.extras

            if (bundle != null) {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                val messageBody = StringBuilder()
                var senderNumber = ""

                for (message in messages) {
                    senderNumber = message.displayOriginatingAddress
                    messageBody.append(message.displayMessageBody)
                }

                startSmsService(context, senderNumber, messageBody.toString())
            }
        }
    }

    private fun startSmsService(context: Context, senderNumber: String?, message: String?) {
        val serviceIntent = Intent(context, FilterService::class.java)
        serviceIntent.putExtra("senderNumber", senderNumber)
        serviceIntent.putExtra("message", message)
        context.startService(serviceIntent)
    }
}
