package io.zry.sms2telegram

import android.content.Context
import android.content.SharedPreferences

class Preferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sms2telegram", Context.MODE_PRIVATE)

    var pushUrl: String?
        get() = prefs.getString("pushUrl", "")
        set(url) = prefs.edit().putString("pushUrl", url).apply()

    var blacklistedNumbers: MutableSet<String>
        get() = prefs.getStringSet("blacklistedNumbers", mutableSetOf<String>()) as MutableSet<String>
        set(numbers) = prefs.edit().putStringSet("blacklistedNumbers", numbers).apply()

    var blacklistedKeywords: MutableSet<String>
        get() = prefs.getStringSet("blacklistedKeywords", mutableSetOf<String>()) as MutableSet<String>
        set(keywords) = prefs.edit().putStringSet("blacklistedKeywords", keywords).apply()
}
