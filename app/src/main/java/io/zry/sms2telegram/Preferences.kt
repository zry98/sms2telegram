package io.zry.sms2telegram

import android.content.Context
import android.content.SharedPreferences

class Preferences(context: Context) {

    private val prefs: SharedPreferences =
            context.getSharedPreferences("sms2telegram", Context.MODE_PRIVATE)

    var isRawTelegramBotApiRequestMode: Boolean
        get() = prefs.getBoolean("isRawTelegramBotApiRequestMode", true)
        set(value) = prefs.edit().putBoolean("isRawTelegramBotApiRequestMode", value).apply()

    var botToken: String?
        get() = prefs.getString("botToken", "")
        set(token) = prefs.edit().putString("botToken", token).apply()

    var chatId: String?
        get() = prefs.getString("chatId", "")
        set(id) = prefs.edit().putString("chatId", id).apply()

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
