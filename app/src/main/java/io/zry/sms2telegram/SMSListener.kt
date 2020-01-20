package io.zry.sms2telegram

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import java.text.SimpleDateFormat
import java.util.*


class SMSListener : BroadcastReceiver() {

    companion object {
        private const val TAG = "SMSListener"
        private const val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SMS_RECEIVED) {
            val bundle = intent.extras

            if (bundle != null) {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                val messageBody = StringBuilder()
                var senderNumber = ""
                var receiveDateTime = ""

                for (message in messages) {
                    senderNumber = message.displayOriginatingAddress
                    receiveDateTime = getDateTime(message.timestampMillis);
                    messageBody.append(message.displayMessageBody)
                }

                startSmsService(context, senderNumber, receiveDateTime, messageBody.toString())
            }
        }
    }

    private fun getDateTime(timestamp: Long): String {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())

            formatter.format(Date(timestamp))
        } catch (e: Exception) {
            "[ERROR]"
        }
    }

    private fun startSmsService(context: Context, senderNumber: String, receiveDateTime: String, messageBody: String) {
        val serviceIntent = Intent(context, FilterService::class.java)

        serviceIntent.putExtra("senderNumber", senderNumber)
        serviceIntent.putExtra("receiveDateTime", receiveDateTime)
        serviceIntent.putExtra("messageBody", messageBody)

        context.startService(serviceIntent)
    }
}
