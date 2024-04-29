/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */
package org.oewntk.grind.yaml2wndb

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
    private val flags: Int,
    private val resolver: Resolver?,
) {

    /**
     * Grind
     *
     * @param source source
     * @param id     id, either synset id or sense id
     */
    fun grind(source: File, id: String) {

        // Model
        val model = CoreFactory(source).get()!!

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
            val flags = flags(args)
            val iArg = flags[1]

            // Input
            val source = File(args[iArg])

            // SynsetId, SenseId, w31 offset
            val extraArg1 = args[iArg + 1]
            val isOffset = extraArg1 == "-offset"
            val isSense = extraArg1 == "-sense"

            val id: String
            var resolver: Resolver? = null
            if (isSense) {
                id = args[iArg + 2]
                resolver = { model: CoreModel, senseId: String -> model.sensesById!![senseId]!!.synsetId }
            } else if (isOffset) {
                val pos = args[iArg + 2][0]
                val offset31 = args[iArg + 3].toLong()
                id = String.format("%08d-%c", offset31, pos)
            } else {
                id = extraArg1
            }
            Grind1(flags[0], resolver).grind(source, id)
        }
    }
}
