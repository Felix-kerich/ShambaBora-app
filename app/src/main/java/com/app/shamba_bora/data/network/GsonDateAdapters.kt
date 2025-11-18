package com.app.shamba_bora.data.network

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Custom Gson TypeAdapter for LocalDate serialization/deserialization
 * Formats dates as "yyyy-MM-dd" (e.g., "2025-03-10") as required by the backend
 */
class LocalDateAdapter : TypeAdapter<LocalDate>() {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE // "yyyy-MM-dd"
    
    override fun write(out: JsonWriter, value: LocalDate?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(formatter.format(value))
        }
    }
    
    override fun read(`in`: JsonReader): LocalDate? {
        return when (`in`.peek()) {
            JsonToken.NULL -> {
                `in`.nextNull()
                null
            }
            else -> {
                val dateString = `in`.nextString()
                if (dateString.isNullOrBlank()) null else LocalDate.parse(dateString, formatter)
            }
        }
    }
}

/**
 * Custom Gson TypeAdapter for LocalDateTime serialization/deserialization
 * Formats date-time as "yyyy-MM-dd'T'HH:mm:ss" (e.g., "2025-03-10T14:30:00") as required by the backend
 * Handles deserialization of various formats including microseconds
 */
class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    
    override fun write(out: JsonWriter, value: LocalDateTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(formatter.format(value))
        }
    }
    
    override fun read(`in`: JsonReader): LocalDateTime? {
        // Handle explicit JSON null values safely before attempting to read a string
        when (`in`.peek()) {
            JsonToken.NULL -> {
                `in`.nextNull()
                return null
            }
            else -> { /* continue to read string */ }
        }

        val dateTimeString = `in`.nextString()
        if (dateTimeString.isNullOrBlank()) {
            return null
        } else {
            // Return parsed LocalDateTime or null; ensure every branch returns a value
            return try {
                // Use DateTimeFormatterBuilder to handle variable fractional seconds
                // This handles formats like "2025-11-17T22:52:52.82102" (5 digits) or "2025-11-17T22:52:52" (no fractional)
                val builder = java.time.format.DateTimeFormatterBuilder()
                    .append(DateTimeFormatter.ISO_LOCAL_DATE)
                    .appendLiteral('T')
                    .append(DateTimeFormatter.ISO_LOCAL_TIME)

                val flexibleFormatter = builder.toFormatter()
                LocalDateTime.parse(dateTimeString, flexibleFormatter)
            } catch (e: Exception) {
                // Fallback: try removing fractional seconds and parse
                try {
                    val cleaned = dateTimeString.replace(Regex("\\.\\d+"), "")
                    LocalDateTime.parse(cleaned, formatter)
                } catch (e2: Exception) {
                    // Last resort: log error and return null
                    android.util.Log.e("LocalDateTimeAdapter", "Failed to parse date: $dateTimeString", e2)
                    null
                }
            }
        }
    }
}


