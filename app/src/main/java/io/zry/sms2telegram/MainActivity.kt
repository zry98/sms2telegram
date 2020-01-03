package io.zry.sms2telegram

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var prefs: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = Preferences(this)

        val pushUrl = prefs.pushUrl
        val pushUrlEditText = findViewById<EditText>(R.id.pushUrlEditText)

        if (pushUrl.isNullOrBlank()) {
            pushUrlEditText.requestFocus()
            Toast.makeText(applicationContext, R.string.setPushUrlToastText, Toast.LENGTH_SHORT).show();
        } else {
            pushUrlEditText.setText(pushUrl)
        }

        val blacklistedNumbers = prefs.blacklistedNumbers

        if (!blacklistedNumbers.isNullOrEmpty()) {
            val blacklistedNumbersEditText = findViewById<EditText>(R.id.blacklistedNumbersEditText)
            val blacklistedNumbersString = blacklistedNumbers.joinToString(separator = "\n")

            blacklistedNumbersEditText.setText(blacklistedNumbersString)
        }

        val blacklistedKeywords = prefs.blacklistedKeywords

        if (!blacklistedKeywords.isNullOrEmpty()) {
            val blacklistedKeywordsEditText = findViewById<EditText>(R.id.blacklistedKeywordsEditText)
            val blacklistedKeywordsString = blacklistedKeywords.joinToString(separator = "\n")

            blacklistedKeywordsEditText.setText(blacklistedKeywordsString)
        }

        startService(Intent(baseContext, MainService::class.java))
    }

    fun setPushUrl(view: View) {
        val pushUrlEditText = findViewById<EditText>(R.id.pushUrlEditText)
        val pushUrl = pushUrlEditText.text.toString()

        prefs.pushUrl = pushUrl
        unFocus()
    }

    fun setBlacklistedNumbers(view: View) {
        val editText = findViewById<EditText>(R.id.blacklistedNumbersEditText)
        val str = editText.text.toString()
        var blacklistedNumbers = emptySet<String>()

        if (str.isNotBlank()) {
            blacklistedNumbers = str.split("\n").toSet()
        }
        prefs.blacklistedNumbers = blacklistedNumbers
        unFocus()
    }

    fun setBlacklistedKeywords(view: View) {
        val editText = findViewById<EditText>(R.id.blacklistedKeywordsEditText)
        val str = editText.text.toString()
        var blacklistedKeywords = emptySet<String>()

        if (str.isNotBlank()) {
            blacklistedKeywords = str.split("\n").toSet()
        }
        prefs.blacklistedKeywords = blacklistedKeywords
        unFocus()
    }

    private fun unFocus() {
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val linearLayout = findViewById<View>(R.id.linearLayout)

        inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        linearLayout.requestFocus()
    }
}
