package io.zry.sms2telegram

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = "MainActivity"
        private lateinit var prefs: Preferences
        private lateinit var context: Context
        private val toastHandler: Handler = ToastHandler()

        private lateinit var webhookUrlEditText: EditText
        private lateinit var blacklistedNumbersEditText: EditText
        private lateinit var blacklistedKeywordsEditText: EditText

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

        webhookUrlEditText = findViewById(R.id.webhookUrlEditText)
        blacklistedNumbersEditText = findViewById(R.id.blacklistedNumbersEditText)
        blacklistedKeywordsEditText = findViewById(R.id.blacklistedKeywordsEditText)

        val webhookUrl = prefs.webhookUrl

        if (webhookUrl.isNullOrBlank()) {
            webhookUrlEditText.requestFocus()
            showToast(resources.getString(R.string.set_webhook_url_hint_toast_text))
        } else {
            webhookUrlEditText.setText(webhookUrl)
        }

        val blacklistedNumbers = prefs.blacklistedNumbers

        if (blacklistedNumbers.isNotEmpty()) {
            val str = blacklistedNumbers.joinToString(separator = "\n")
            blacklistedNumbersEditText.setText(str)
        }

        val blacklistedKeywords = prefs.blacklistedKeywords

        if (blacklistedKeywords.isNotEmpty()) {
            val str = blacklistedKeywords.joinToString(separator = "\n")
            blacklistedKeywordsEditText.setText(str)
        }

        startService(Intent(baseContext, MainService::class.java))
    }

    fun setWebhookUrl(view: View) {
        val str = webhookUrlEditText.text.toString()

        if (str.isNotBlank()) {
            prefs.webhookUrl = str
            unFocus()
            showToast(resources.getString(R.string.toast_text_successful))
        }
    }

    fun setBlacklistedNumbers(view: View) {
        val str = blacklistedNumbersEditText.text.toString()
        val numbers = if (str.isNotBlank()) {
            str.split("\n").toSet()
        } else {
            emptySet()
        }

        prefs.blacklistedNumbers = numbers
        unFocus()
        showToast(resources.getString(R.string.toast_text_successful))
    }

    fun setBlacklistedKeywords(view: View) {
        val str = blacklistedKeywordsEditText.text.toString()
        val keywords = if (str.isNotBlank()) {
            str.split("\n").toSet()
        } else {
            emptySet()
        }

        prefs.blacklistedKeywords = keywords
        unFocus()
        showToast(resources.getString(R.string.toast_text_successful))
    }

    private fun unFocus() {
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val linearLayout = findViewById<View>(R.id.linearLayout)

        inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        linearLayout.requestFocus()
    }

    class ToastHandler : Handler() {
        override fun handleMessage(msg: Message) {
            makeToast(msg.data.getString("text")!!)
            super.handleMessage(msg)
        }
    }
}
