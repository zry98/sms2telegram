package io.zry.sms2telegram

import android.content.Context
import android.content.SharedPreferences

class Preferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sms2telegram", Context.MODE_PRIVATE)

    var webhookUrl: String?
        get() = prefs.getString("webhookUrl", "")
        set(url) = prefs.edit().putString("webhookUrl", url).apply()

    var blacklistedNumbers: Set<String>
        get() = prefs.getStringSet("blacklistedNumbers", setOf<String>()) as Set<String>
        set(numbers) = prefs.edit().putStringSet("blacklistedNumbers", numbers).apply()

    var blacklistedKeywords: Set<String>
        get() = prefs.getStringSet("blacklistedKeywords", setOf<String>()) as Set<String>
        set(keywords) = prefs.edit().putStringSet("blacklistedKeywords", keywords).apply()
}
