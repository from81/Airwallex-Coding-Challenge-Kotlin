package com.airwallex.codechallenge.model

import java.time.Instant

data class SpotChangeAlert(
    val timestamp: Instant,
    val currencyPair: String,
    val alert: String = "spotChange"
)