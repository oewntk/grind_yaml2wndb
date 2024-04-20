/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */
package org.oewntk.grind.yaml2wndb

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
		val flags = flags(args)
		val iArg = flags[1]

		// Tracing
		val startTime = start()

		// Input
		val inDir = File(args[iArg])
		Tracing.psInfo.println("[Input] " + inDir.absolutePath)

		// Output
		val outDir = File(args[iArg + 1])
		if (!outDir.exists()) {
			outDir.mkdirs()
		}
		Tracing.psInfo.println("[Output] " + outDir.absolutePath)

		// Supply model
		progress("before model is supplied,", startTime)
		val model = CoreFactory(inDir).get()
		//Tracing.psInfo.printf("[CoreModel] %s%n%s%n%n", Arrays.toString(model.getSources()), model.info());
		progress("after model is supplied,", startTime)

		// Consume model
		progress("before model is consumed,", startTime)
		OffsetMapper(outDir, flags[0], Tracing.psInfo).grind(model!!)
		OffsetSerializer(outDir, flags[0], Tracing.psInfo).grind(model)
		progress("after model is consumed,", startTime)

		// End
		progress("total,", startTime)
	}
}
