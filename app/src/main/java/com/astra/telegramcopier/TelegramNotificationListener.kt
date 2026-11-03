package com.astra.telegramcopier

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class TelegramNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != "org.telegram.messenger" && sbn.packageName != "org.telegram.messenger.web") return
        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString().orEmpty()
        val text = extras.getCharSequence("android.text")?.toString().orEmpty()
        val bigText = extras.getCharSequence("android.bigText")?.toString().orEmpty()
        val message = listOf(title, text, bigText).filter { it.isNotBlank() }.distinct().joinToString("\n")
        val signal = SignalParser.parse(message) ?: return
        getSharedPreferences("astra_signals", MODE_PRIVATE).edit()
            .putString("latest", "${signal.pair}|${signal.timeframe}|${signal.direction}|${signal.expiry.orEmpty()}|${signal.raw}")
            .putLong("latest_at", System.currentTimeMillis()).apply()
        sendBroadcast(android.content.Intent(MainActivity.ACTION_SIGNAL_RECEIVED).setPackage(packageName))
    }
}
