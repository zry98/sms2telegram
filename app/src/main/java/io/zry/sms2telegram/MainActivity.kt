package io.zry.sms2telegram

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var prefs: Preferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = Preferences(this)

        val pushUrl = prefs!!.pushUrl
        if (!pushUrl.isNullOrEmpty()) {
            val pushUrlEditText = findViewById<EditText>(R.id.pushUrlEditText)
            pushUrlEditText.setText(pushUrl)
        }

        val blacklistedNumbers = prefs!!.blacklistedNumbers
        if (!blacklistedNumbers.isNullOrEmpty()) {
            val blacklistedNumbersEditText = findViewById<EditText>(R.id.blacklistedNumbersEditText)
            val blacklistedNumbersString = blacklistedNumbers.joinToString(separator = "\n")
            blacklistedNumbersEditText.setText(blacklistedNumbersString)
        }

        val blacklistedKeywords = prefs!!.blacklistedKeywords
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
        if (pushUrl.isNotBlank()) {
            prefs!!.pushUrl = pushUrl
        }
    }

    fun setBlacklistedNumbers(view: View) {
        val blacklistedNumbersEditText = findViewById<EditText>(R.id.blacklistedNumbersEditText)
        val blacklistedNumbersString = blacklistedNumbersEditText.text.toString()
        if (blacklistedNumbersString.isNotBlank()) {
            val blacklistedNumbers = blacklistedNumbersString.split("\n").toMutableSet()
            prefs!!.blacklistedNumbers = blacklistedNumbers
        }
    }

    fun setBlacklistedKeywords(view: View) {
        val blacklistedKeywordsEditText = findViewById<EditText>(R.id.blacklistedKeywordsEditText)
        val blacklistedKeywordsString = blacklistedKeywordsEditText.text.toString()
        if (blacklistedKeywordsString.isNotBlank()) {
            val blacklistedKeywords = blacklistedKeywordsString.split("\n").toMutableSet()
            prefs!!.blacklistedKeywords = blacklistedKeywords
        }
    }
}
