package com.airwallex.codechallenge

import com.airwallex.codechallenge.io.Reader
import com.airwallex.codechallenge.io.Writer
import com.airwallex.codechallenge.model.CurrencyConversionRate
import com.airwallex.codechallenge.model.SpotChangeAlert
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    // create output directory if it does not exist
    val directory = File("output")
    if (!directory.exists()) directory.mkdir()

    // load config
    val config = Properties()
    config.load(FileInputStream("app.properties"))
    val movingAverageWindow: Int = config.getProperty("moving_average_window", "300").toInt()
    val pctChangeThreshold: Float = config.getProperty("pct_change_threshold", "0.1").toFloat()
    println("Params: moving average window=$movingAverageWindow, percent change threshold=$pctChangeThreshold")

    val timeInMs = measureTimeMillis {
        runBlocking{
            args.filter { it.endsWith(".jsonl") }
                .forEach { path -> launch{ processFile(path, movingAverageWindow, pctChangeThreshold) } }
        }
    }
    println("Tasks finished in ${timeInMs}ms")
}

fun processFile(path: String, movingAverageWindow: Int, pctChangeThreshold: Float) {
    val monitor = SpotChangeMonitor(movingAverageWindow, pctChangeThreshold)
    val reader = Reader()
    val writer = Writer()
    reader.read(path).forEach { currencyConversionRate: CurrencyConversionRate ->
        val alertMaybe: Optional<SpotChangeAlert> = monitor.processData(currencyConversionRate)
        if (alertMaybe.isPresent) writer.write(alertMaybe.get())
    }
    writer.close()
}