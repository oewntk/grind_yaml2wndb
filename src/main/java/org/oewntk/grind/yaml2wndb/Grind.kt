/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.grind.yaml2wndb

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
     * Argument switches processing
     *
     * @param args command-line arguments
     * @return int[0]=flags, int[1]=next arg to process
     */
    fun flags(args: Array<String>): IntArray {
        val result = IntArray(2)

        var i = 0
        while (i < args.size) {
            if ("-traceTime" == args[i]) // if left and is "-traceTime"
            {
                Tracing.traceTime = true
            } else if ("-traceHeap" == args[i]) // if left and is "-traceHeap"
            {
                Tracing.traceHeap = true
            } else if ("-compat:pointer" == args[i]) // if left and is "-compat:pointer"
            {
                result[0] = result[0] or Flags.POINTER_COMPAT
            } else if ("-compat:lexid" == args[i]) // if left and is "-compat:lexid"
            {
                result[0] = result[0] or Flags.LEXID_COMPAT
            } else if ("-compat:verbframe" == args[i]) // if left and is "-compat:verbframe"
            {
                result[0] = result[0] or Flags.VERBFRAME_COMPAT
            } else {
                break
            }
            i++
        }
        result[1] = i
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
        val flags = flags(args)
        val iArg = flags[1]

        // Tracing
        val startTime = Tracing.start()

        // Input
        val inDir = File(args[iArg])
        Tracing.psInfo.println("[Input] " + inDir.absolutePath)

        // Input2
        val inDir2 = File(args[iArg + 1])
        Tracing.psInfo.println("[Input2] " + inDir2.absolutePath)

        // Output
        val outDir = File(args[iArg + 2])
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        Tracing.psInfo.println("[Output] " + outDir.absolutePath)

        // Supply model
        Tracing.progress("before model is supplied,", startTime)
        val model = Factory(inDir, inDir2).get()
        //Tracing.psInfo.printf("[Model] %s%n%s%n%n", Arrays.toString(model.getSources()), model.info())
        Tracing.progress("after model is supplied,", startTime)

        // Consume model
        Tracing.progress("before model is consumed,", startTime)
        ModelConsumer(outDir, flags[0]).grind(model!!)
        Tracing.progress("after model is consumed,", startTime)

        // End
        Tracing.progress("total,", startTime)
    }
}
