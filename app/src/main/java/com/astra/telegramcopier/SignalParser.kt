package com.astra.telegramcopier

data class TradeSignal(val pair: String, val timeframe: String, val direction: String, val expiry: String?, val raw: String)

object SignalParser {
    private val pairRegex = Regex("\\b(?:USD|EUR|GBP|JPY|CHF|AUD|NZD|CAD|BTC|ETH|XAU|XAG)(?:USD|EUR|GBP|JPY|CHF|AUD|NZD|CAD|BTC|ETH)\\b")
    private val tfRegex = Regex("\\bM([1-9]|[1-5][0-9])\\b", RegexOption.IGNORE_CASE)
    fun parse(message: String): TradeSignal? {
        val upper = message.uppercase()
        val direction = when {
            Regex("\\b(PUT|SELL|DOWN)\\b|🔽").containsMatchIn(upper) -> "PUT"
            Regex("\\b(CALL|BUY|UP)\\b|🔼").containsMatchIn(upper) -> "CALL"
            else -> return null
        }
        val pair = pairRegex.find(upper)?.value ?: return null
        val timeframe = tfRegex.find(upper)?.value?.uppercase() ?: "غير محدد"
        val expiry = Regex("\\b([01]?\\d|2[0-3]):[0-5]\\d(:[0-5]\\d)?\\b").find(message)?.value
        return TradeSignal(pair, timeframe, direction, expiry, message)
    }
}
