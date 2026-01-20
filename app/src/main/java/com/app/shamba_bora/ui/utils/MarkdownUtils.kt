package com.app.shamba_bora.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

/**
 * Markdown-like renderer supporting:
 * - **bold** and ***bold italic***
 * - *italic*
 * - ### Headers (h3), ## Headers (h2), # Headers (h1)
 * - Strips markdown markers and applies formatting
 */
fun renderMarkdown(input: String?): AnnotatedString {
    val text = input ?: ""
    
    return buildAnnotatedString {
        var pos = 0
        val lines = text.split("\n")
        
        for ((lineIndex, line) in lines.withIndex()) {
            // Check for headers: ###, ##, #
            val headerMatch = Regex("^(#{1,3})\\s+(.+)").find(line)
            if (headerMatch != null) {
                val hashes = headerMatch.groupValues[1]
                val headerText = headerMatch.groupValues[2]
                
                val fontSize = when (hashes.length) {
                    1 -> 24.sp  // # Header 1
                    2 -> 20.sp  // ## Header 2
                    3 -> 16.sp  // ### Header 3
                    else -> 14.sp
                }
                
                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize
                )) {
                    append(renderLineMarkdown(headerText))
                }
            } else {
                // Regular line with inline formatting
                append(renderLineMarkdown(line))
            }
            
            // Add newline if not the last line
            if (lineIndex < lines.size - 1) {
                append("\n")
            }
        }
    }
}

/**
 * Render a single line with inline formatting: **bold**, ***bold italic***, *italic*
 */
private fun renderLineMarkdown(line: String): AnnotatedString {
    return buildAnnotatedString {
        val boldItalicRegex = Regex("\\*\\*\\*(.*?)\\*\\*\\*")
        val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
        val italicRegex = Regex("\\*(.*?)\\*")
        
        var lastIndex = 0
        
        // Process bold-italic first (highest priority)
        val boldItalicMatches = boldItalicRegex.findAll(line).toList()
        val boldMatches = boldRegex.findAll(line).toList()
        val italicMatches = italicRegex.findAll(line).toList()
        
        // Combine and sort all matches by position
        val allMatches = (boldItalicMatches.map { Triple(it.range, "bolditalic", it) } +
                          boldMatches.map { Triple(it.range, "bold", it) } +
                          italicMatches.map { Triple(it.range, "italic", it) })
            .sortedBy { it.first.first }
        
        val processedRanges = mutableSetOf<IntRange>()
        
        for ((range, type, match) in allMatches) {
            // Skip overlapping matches
            if (processedRanges.any { it.intersect(range).isNotEmpty() }) continue
            
            if (range.first > lastIndex) {
                append(line.substring(lastIndex, range.first))
            }
            
            val content = match.groupValues[1]
            when (type) {
                "bolditalic" -> {
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )) {
                        append(content)
                    }
                }
                "bold" -> {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(content)
                    }
                }
                "italic" -> {
                    withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(content)
                    }
                }
            }
            
            processedRanges.add(range)
            lastIndex = range.last + 1
        }
        
        if (lastIndex < line.length) {
            append(line.substring(lastIndex))
        }
    }
}
