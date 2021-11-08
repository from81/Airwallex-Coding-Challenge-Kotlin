package com.airwallex.codechallenge.io

import com.airwallex.codechallenge.model.SpotChangeAlert
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.BufferedWriter
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.util.stream.Stream

class Writer {
    private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    private val writer: BufferedWriter by lazy {
        // create and open output file
        File("output/${Instant.now()}.jsonl").bufferedWriter()
    }

    fun write(alert: SpotChangeAlert) {
        val jsonStr = mapper.writeValueAsString(alert)
        writer.write(jsonStr)
        writer.newLine()
        writer.flush()
    }

    fun close() { writer.close() }
}