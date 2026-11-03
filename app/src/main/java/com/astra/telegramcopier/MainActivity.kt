package com.astra.telegramcopier

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.EditText

class MainActivity : Activity() {
    companion object { const val ACTION_SIGNAL_RECEIVED = "com.astra.telegramcopier.SIGNAL_RECEIVED" }
    private lateinit var status: TextView
    private lateinit var signalInput: EditText
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) { refreshLatest() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 24)
            layoutParams = ViewGroup.LayoutParams(-1, -1)
            setBackgroundColor(0xFFF5F7FA.toInt())
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        fun heading(text: String, size: Float = 18f) {
            content.addView(TextView(this).apply { this.text = text; textSize = size; setTextColor(0xFF142033.toInt()); setPadding(0, 12, 0, 10) })
        }
        fun button(label: String, action: () -> Unit) {
            content.addView(Button(this).apply { text = label; setOnClickListener { action() } })
        }
        heading("ASTRA", 30f)
        heading("ناسخ إشارات التداول من تيليجرام")
        status = TextView(this).apply { textSize = 15f; setTextColor(0xFF263238.toInt()); setPadding(0, 12, 0, 12) }
        content.addView(status)
        button("تفعيل الوصول لإشعارات تيليجرام") {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
        heading("اختبار / إدخال إشارة يدويًا")
        signalInput = EditText(this).apply { hint = "USDCHF\nM1\nPUT"; minLines = 3; gravity = Gravity.TOP }
        content.addView(signalInput, LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT))
        button("تحليل الإشارة") { parseManual() }
        heading("آخر إشارة تم التقاطها")
        button("تحديث") { refreshLatest() }
        heading("حدود الإصدار الحالي", 16f)
        content.addView(TextView(this).apply {
            text = "• يقرأ نص إشعارات تيليجرام الظاهرة فقط.\n• يجب تفعيل إشعارات القناة ومعاينتها في الهاتف.\n• لا يسجل الدخول إلى حساب Telegram ولا يقرأ القنوات الصامتة.\n• لا يفتح صفقات حقيقية ولا يتصل بـ Pocket Option.\n• استخدمه للمراقبة والاختبار الورقي فقط."
            textSize = 14f; setTextColor(0xFF455A64.toInt()); setPadding(0, 8, 0, 20)
        })
        setContentView(root)
        refreshLatest()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(ACTION_SIGNAL_RECEIVED), Context.RECEIVER_NOT_EXPORTED)
        refreshLatest()
    }
    override fun onPause() {
        try { unregisterReceiver(receiver) } catch (_: Exception) {}
        super.onPause()
    }
    private fun parseManual() {
        val signal = SignalParser.parse(signalInput.text.toString())
        status.text = if (signal == null) "لم أتعرف على الإشارة. مثال: USDCHF / M1 / PUT"
        else "تم تحليل إشارة تجريبية:\nالزوج: ${signal.pair}\nالفريم: ${signal.timeframe}\nالاتجاه: ${signal.direction}\nالانتهاء: ${signal.expiry ?: "غير محدد"}\n\nلم يتم تنفيذ أي صفقة."
    }
    private fun refreshLatest() {
        val raw = getSharedPreferences("astra_signals", MODE_PRIVATE).getString("latest", null)
        val enabled = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")?.contains(packageName) == true
        status.text = buildString {
            append(if (enabled) "حالة قارئ الإشعارات: مفعّل" else "حالة قارئ الإشعارات: غير مفعّل")
            append("\n")
            append(if (raw == null) "لم تصل إشارة بعد." else "آخر إشارة:\n" + raw.replace("|", "\n"))
        }
    }
}
