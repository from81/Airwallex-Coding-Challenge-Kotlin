package com.airwallex.codechallenge

import com.airwallex.codechallenge.model.CurrencyConversionRate
import com.airwallex.codechallenge.model.SpotChangeAlert
import java.time.Instant
import java.util.*
import kotlin.Comparator


class SpotChangeMonitor(private val moving_average_window: Int, private val pct_change_threshold: Float) {
    private val queues: Hashtable<String, PriorityQueue<Pair<Instant, CurrencyConversionRate>>> = Hashtable()
    private val queueInfo: Hashtable<String, Pair<Double, Int>> = Hashtable()
    private val myCustomComparator =  Comparator<Pair<Instant, CurrencyConversionRate>> { a, b ->
        when {
            (a.first == b.first) -> 0
            (a.first > b.first) -> 1
            else -> -1
        }
    }

    fun processData(conversionRate: CurrencyConversionRate): Optional<SpotChangeAlert> {
        // get or create priority queue for the currency pair
        val queue: PriorityQueue<Pair<Instant, CurrencyConversionRate>> = queues.getOrDefault(
            conversionRate.currencyPair,
            PriorityQueue(moving_average_window, myCustomComparator)
        )

        // get cumulative sum of the last n entries, and current queue size
        val currencyInfo = queueInfo.getOrDefault(conversionRate.currencyPair, Pair(0.0, 0))
        var cumsum: Double = currencyInfo.first
        var currentQueueSize: Int = currencyInfo.second

        // if queue is full, remove one item and subtract its value from cumsum
        // if not full, update current queue size
        if (currentQueueSize == moving_average_window) {
            val expiredData = queue.remove()
            cumsum -= expiredData.second.rate
        } else {
            currentQueueSize += 1
        }

        cumsum += conversionRate.rate

        // update queue and queueInfo
        queue.add(Pair(conversionRate.timestamp, conversionRate))
        queues[conversionRate.currencyPair] = queue
        queueInfo[conversionRate.currencyPair] = Pair(cumsum, currentQueueSize)

        // check pct diff
        if (currentQueueSize > 1) {
            val currAverage: Double = cumsum / currentQueueSize
            val pctChange: Double = (conversionRate.rate - currAverage) / currAverage

            return if (pctChange < pct_change_threshold) Optional.empty()
            else Optional.of(SpotChangeAlert(conversionRate.timestamp, conversionRate.currencyPair));
        }
        return Optional.empty()
    }
}
