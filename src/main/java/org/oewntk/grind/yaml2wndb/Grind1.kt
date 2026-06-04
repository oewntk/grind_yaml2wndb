/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.grind.yaml2wndb

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.oewntk.grind.yaml2wndb.Grind.flags
import org.oewntk.model.CoreModel
import org.oewntk.parse.DataParser1
import org.oewntk.pojos.ParsePojoException
import org.oewntk.wndb.out.LineProducer
import org.oewntk.yaml.`in`.CoreFactory
import java.io.File

typealias Resolver = (CoreModel, String) -> String?

/**
 * Main class that generates one line of the WN database in the WNDB format as per wndb(5WN)
 *
 * @param flags    flags
 * @param resolver sensekey-to-synsetid resolver
 *
 * @author Bernard Bou
 * @see "https://wordnet.princeton.edu/documentation/wndb5wn"
 */
class Grind1(
    val flags: Int,
    private val resolver: Resolver?,
    private val verbose: Boolean = false,
) {

    /**
     * Grind
     *
     * @param source source
     * @param id     id, either synset id or sense id
     */
    fun grind(source: File, id: String) {

        // Model
        val model = CoreFactory(source, verbose = verbose).get()!!

        // SynsetId
        val synsetId = if (resolver != null) resolver.invoke(model, id) ?: id else id

        // Process
        val line: String = LineProducer(flags).invoke(model, synsetId)
        consumeLine(line)
    }

    /**
     * Consume produced line
     *
     * @param line line
     */
    private fun consumeLine(line: String) {
        println(line)

        // Parse line and pretty print
        try {
            val s = DataParser1.parseSynset(line, false)
            println(s.toPrettyString())
        } catch (e: ParsePojoException) {
            e.printStackTrace(Tracing.psErr)
        }
    }

    companion object {

        /**
         * Main entry point
         *
         * @param args command-line arguments
         * ```
         * [0] source,
         * [1] synsetid
         * [1] -offset [2] pos, [3] offset
         * [1] -sense [2] senseid
         * ```
         *
         * # POS (n|v|a|r|s)
         * # OFFSET (ie 1740)
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val parser = ArgParser("yaml2wndb")

            // Options (start with - or --)
            // @formatter:off
            val in1 by parser.argument(            ArgType.String,                                                    description = "Input dir or file")

            val synset by parser.option(           ArgType.String,   shortName = "y",  fullName = "synset",           description = "Synset id")
            val sense by parser.option(            ArgType.String,   shortName = "s",  fullName = "sense",            description = "Sense id")
            val offset by parser.option(           ArgType.String,   shortName = "o",  fullName = "offset",           description = "Offset")
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

            // Input
            val source = File(in1)

            // SynsetId, SenseId, w31 offset
            val (id: String, resolver: Resolver?) = if (sense != null) {
                val resolver = { model: CoreModel, senseId: String -> model.senseResolver(senseId).synsetId }
                sense!! to resolver
            } else if (offset != null) {
                val pos = offset!![0]
                val offset31 = offset!!.drop(1).toLong()
                val synsetId = String.format("%08d-%c", offset31, pos)
                synsetId to null
            } else if (synset != null) {
                synset!! to null
            } else {
                throw IllegalArgumentException()
            }

            // Flags
            val wndbFlags = flags(wndCompatPointer, wndCompatLexId, wndCompatVFrames)

            Grind1(wndbFlags, resolver, verbose = verbose).grind(source, id)
        }
    }
}
