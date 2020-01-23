package io.zry.sms2telegram

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = "MainActivity"
        private lateinit var prefs: Preferences
        private lateinit var context: Context
        private val toastHandler: Handler = ToastHandler()

        fun makeToast(text: String) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            Log.d(TAG, text)
        }

        fun showToast(vararg messageFragments: Any) {
            val message = Message.obtain()
            val toastText = StringBuilder()
            for (fragment in messageFragments) {
                toastText.append(fragment.toString())
            }
            message.data = bundleOf("text" to toastText.toString())
            toastHandler.sendMessage(message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        setContentView(R.layout.activity_main)
        prefs = Preferences(this)
        context = applicationContext

        val isRawTelegramBotApiRequestMode = prefs.isRawTelegramBotApiRequestMode
        switch_raw_telegram_bot_api_request_mode.isChecked = isRawTelegramBotApiRequestMode
        rawTelegramBotApiRequestMode(isRawTelegramBotApiRequestMode)

        if (isRawTelegramBotApiRequestMode) {
            val botToken = prefs.botToken
            if (botToken.isNullOrBlank()) {
                edittext_bot_token_or_webhook_url.requestFocus()
                showToast(resources.getString(R.string.toast_text_set_bot_token))
            } else {
                edittext_bot_token_or_webhook_url.setText(botToken)
            }

            val chatId = prefs.chatId
            if (chatId.isNullOrBlank()) {
                edittext_chat_id.requestFocus()
                showToast(resources.getString(R.string.toast_text_set_chat_id))
            } else {
                edittext_chat_id.setText(chatId)
            }
        } else {
            val webhookUrl = prefs.webhookUrl
            if (webhookUrl.isNullOrBlank()) {
                edittext_bot_token_or_webhook_url.requestFocus()
                showToast(resources.getString(R.string.toast_text_set_webhook_url))
            } else {
                edittext_bot_token_or_webhook_url.setText(webhookUrl)
            }
        }

        val blacklistedNumbers = prefs.blacklistedNumbers
        if (blacklistedNumbers.isNotEmpty()) {
            val str = blacklistedNumbers.joinToString(separator = "\n")
            edittext_blacklisted_numbers.setText(str)
        }

        val blacklistedKeywords = prefs.blacklistedKeywords
        if (blacklistedKeywords.isNotEmpty()) {
            val str = blacklistedKeywords.joinToString(separator = "\n")
            edittext_blacklisted_keywords.setText(str)
        }

        switch_raw_telegram_bot_api_request_mode.setOnCheckedChangeListener { _, isChecked ->
            prefs.isRawTelegramBotApiRequestMode = isChecked
            rawTelegramBotApiRequestMode(isChecked)
        }

        startService(Intent(baseContext, MainService::class.java))
    }

    private fun rawTelegramBotApiRequestMode(on: Boolean) {
        if (on) {
            edittext_bot_token_or_webhook_url.hint = "Bot Token"
            edittext_bot_token_or_webhook_url.setText(prefs.botToken)
            layout_chat_id.visibility = View.VISIBLE
            edittext_chat_id.setText(prefs.chatId)
        } else {
            edittext_bot_token_or_webhook_url.hint = "Webhook URL"
            edittext_bot_token_or_webhook_url.setText(prefs.webhookUrl)
            layout_chat_id.visibility = View.GONE
        }
    }

    fun saveSettings(view: View) {
        var str = edittext_bot_token_or_webhook_url.text.toString()
        if (prefs.isRawTelegramBotApiRequestMode) {
            prefs.botToken = str
        } else {
            prefs.webhookUrl = str
        }

        str = edittext_chat_id.text.toString()
        prefs.chatId = str

        str = edittext_blacklisted_numbers.text.toString()
        val numbers = if (str.isNotBlank()) {
            str.split("\n").toSet()
        } else {
            emptySet()
        }
        prefs.blacklistedNumbers = numbers

        str = edittext_blacklisted_keywords.text.toString()
        val keywords = if (str.isNotBlank()) {
            str.split("\n").toSet()
        } else {
            emptySet()
        }
        prefs.blacklistedKeywords = keywords

        showToast(resources.getString(R.string.toast_text_successful_message))

        val imeManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imeManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        main_layout.requestFocus()
    }

    class ToastHandler : Handler() {
        override fun handleMessage(msg: Message) {
            makeToast(msg.data.getString("text")!!)
            super.handleMessage(msg)
        }
    }
}
