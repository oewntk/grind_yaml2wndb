/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.grind.yaml2wndb

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.oewntk.grind.yaml2wndb.Grind.flags
import org.oewntk.grind.yaml2wndb.Tracing.progress
import org.oewntk.grind.yaml2wndb.Tracing.start
import org.oewntk.wndb.out.OffsetMapper
import org.oewntk.wndb.out.OffsetSerializer
import org.oewntk.yaml.`in`.CoreFactory
import java.io.File
import java.io.IOException

/**
 * Main class that generates the WN database offset map
 *
 * @author Bernard Bou
 */
object GrindOffsets {

    /**
     * Main entry point
     *
     * @param args command-line arguments
     * ```
     * [-compat:lexid] [-compat:pointer] yamlDir [outputDir]
     * ```
     *
     * @throws IOException io
     */
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgParser("yaml2wndb")
        // Options (start with - or --)
        // @formatter:off
        val in1 by parser.argument(            ArgType.String,                                                    description = "Input dir or file")
        val out by parser.argument(            ArgType.String,                                                    description = "Output dir or file")
        val verbose by parser.option(          ArgType.Boolean,  shortName = "v",  fullName = "verbose",          description = "Verbose output")           .default(false)

        val wndCompatPointer by parser.option( ArgType.Boolean,  shortName = "wp", fullName = "compat:pointer",   description = "WNDB pointer compat")      .default(false)
        val wndCompatLexId by parser.option(   ArgType.Boolean,  shortName = "wl", fullName = "compat:lexid",     description = "WNDB lexid compat")        .default(false)
        val wndCompatVFrames by parser.option( ArgType.Boolean,  shortName = "wv", fullName = "compat:verbframe", description = "WNDB vframe compat")       .default(false)

        val traceTime by parser.option(        ArgType.Boolean,  shortName = "tt", fullName = "trace:time",       description = "trace time")               .default(false)
        val traceHeap by parser.option(        ArgType.Boolean,  shortName = "th", fullName = "trace:heap",       description = "trace heap")               .default(false)
        // @formatter:on
        parser.parse(args)

        // Tracing
        Tracing.traceTime = traceTime
        Tracing.traceHeap = traceHeap

        // Tracing
        val startTime = start()

        // Input
        val inDir = File(in1)
        Tracing.psInfo.println("[Input] " + inDir.absolutePath)

        // Output
        val outDir = File(out)
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        Tracing.psInfo.println("[Output] " + outDir.absolutePath)

        // Flags
        val wndbFlags = flags(wndCompatPointer, wndCompatLexId, wndCompatVFrames)

        // Supply model
        progress("before model is supplied,", startTime)
        val model = CoreFactory(inDir, verbose = verbose).get()
        progress("after model is supplied,", startTime)

        // Consume model
        progress("before model is consumed,", startTime)
        OffsetMapper(outDir, wndbFlags, Tracing.psInfo).grind(model!!)
        OffsetSerializer(outDir, wndbFlags, Tracing.psInfo).grind(model)
        progress("after model is consumed,", startTime)

        // End
        progress("total,", startTime)
    }
}
