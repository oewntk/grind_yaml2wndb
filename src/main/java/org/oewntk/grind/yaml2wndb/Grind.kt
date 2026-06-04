/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.grind.yaml2wndb

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.oewntk.wndb.out.Flags
import org.oewntk.wndb.out.ModelConsumer
import org.oewntk.yaml.`in`.Factory
import java.io.File
import java.io.IOException

/**
 * Main class that generates the WN database in the WNDB format as per wndb(5WN)
 *
 * @author Bernard Bou
 * @see "https://wordnet.princeton.edu/documentation/wndb5wn"
 */
object Grind {

    /**
     * Argument switches merging
     *
     * @return flags
     */
    fun flags(compatPointer: Boolean, compatLexId: Boolean, compatVFrame: Boolean): Int {
        var result = 0
        if (compatPointer)
            result = result or Flags.POINTER_COMPAT
        if (compatLexId)
            result = result or Flags.LEXID_COMPAT
        if (compatVFrame)
            result = result or Flags.VERBFRAME_COMPAT
        return result
    }

    /**
     * Main entry point
     *
     * @param args command-line arguments
     * ```
     * [-compat:lexid] [-compat:pointer] yamlDir [outputDir]
     * ```
     * @throws IOException io
     */
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgParser("yaml2wndb")

        // Options (start with - or --)
        // @formatter:off
        val in1 by parser.argument(            ArgType.String,                                                    description = "Input dir or file")
        val in2 by parser.argument(            ArgType.String,                                                    description = "Extra input dir or file")
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

        val startTime = Tracing.start()

        // Input
        val inDir = File(in1)
        Tracing.psInfo.println("[Input] " + inDir.absolutePath)

        // Input2
        val inDir2 = File(in2)
        Tracing.psInfo.println("[Input2] " + inDir2.absolutePath)

        // Output
        val outDir = File(out)
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        Tracing.psInfo.println("[Output] " + outDir.absolutePath)

        // Flags
        val wndbFlags = flags(wndCompatPointer, wndCompatLexId, wndCompatVFrames)

        // Supply model
        Tracing.progress("before model is supplied,", startTime)
        val model = Factory(inDir, inDir2, verbose = verbose).get()
        Tracing.progress("after model is supplied,", startTime)

        // Consume model
        Tracing.progress("before model is consumed,", startTime)
        ModelConsumer(outDir, flags = wndbFlags, verbose = verbose).grind(model!!)
        Tracing.progress("after model is consumed,", startTime)

        // End
        Tracing.progress("total,", startTime)
    }
}
