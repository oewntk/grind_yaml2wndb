/*
 * Copyright (c) 2024. Bernard Bou.
 */
package org.oewntk.grind.yaml2wndb

import java.io.PrintStream
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 * Heap Memory utilities
 *
 * @author Bernard Bou
 */
object Tracing {

    val psInfo: PrintStream = System.out

    val psErr: PrintStream = System.err

    private val psHeap: PrintStream = System.out

    private val psTime: PrintStream = System.out

    var traceHeap: Boolean = false

    var traceTime: Boolean = false

    fun start(): Long {
        val startTime = System.currentTimeMillis()

        // heap state
        if (traceHeap) {
            psHeap.println("[Memory]: " + Memory.memoryInfo("before,", Memory.Unit.M))
            psHeap.println("[Heap]: " + Memory.heapInfo("before,", Memory.Unit.M))
        }
        return startTime
    }

    fun progress(message: String?, startTime: Long) {
        if (traceTime) {
            val endTime = System.currentTimeMillis()
            psTime.println("[Time]: " + (endTime - startTime) / 1000 + "s")
        }

        // heap state
        if (traceHeap) {
            psHeap.println("[Heap]: " + Memory.heapInfo(message, Memory.Unit.M))
        }
    }

    object Memory {

        private fun formatter(): DecimalFormat {
            val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
            val symbols = formatter.decimalFormatSymbols
            symbols.groupingSeparator = ' '
            formatter.decimalFormatSymbols = symbols
            return formatter
        }

        fun heapInfo(tag: String?, u: Unit): String {
            val max = Runtime.getRuntime().maxMemory()
            val total = Runtime.getRuntime().totalMemory()
            val free = Runtime.getRuntime().freeMemory()
            var used = total - free
            val future = max - total
            var avail = free + future

            used /= u.div
            avail /= u.div

            val formatter = formatter()
            return String.format(
                "%s used: %s%s maxfree: %s%s",  
                tag,  
                formatter.format(used), u,  
                formatter.format(avail), u 
            )
        }

        fun memoryInfo(tag: String?, u: Unit): String {
            var max = Runtime.getRuntime().maxMemory()
            var total = Runtime.getRuntime().totalMemory()
            var free = Runtime.getRuntime().freeMemory()
            var used = total - free
            val future = max - total
            var avail = free + future

            max /= u.div // This will return Long.MAX_VALUE if there is no preset limit
            total /= u.div
            free /= u.div
            used /= u.div
            avail /= u.div

            val formatter = formatter()
            return String.format(
                "%s max=%15s%s total=%10s%s used=%15s%s free=%15s%s avail=%15s%s",  
                tag,  
                formatter.format(max), u,  
                formatter.format(total), u,  
                formatter.format(used), u,  
                formatter.format(free), u,  
                formatter.format(avail), u
            )
        }

        enum class Unit(val div: Long) {
            U(1), K(1024 * U.div), M(1024 * K.div), G(1024 * M.div)
        }
    }
}
