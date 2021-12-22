/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.grind.yaml2wndb;

import org.oewntk.model.CoreModel;
import org.oewntk.wndb.out.OffsetMapper;
import org.oewntk.wndb.out.OffsetSerializer;
import org.oewntk.yaml.in.CoreFactory;

import java.io.File;
import java.io.IOException;

/**
 * Main class that generates the WN database in the WNDB format as per wndb(5WN)
 *
 * @author Bernard Bou
 * @see "https://wordnet.princeton.edu/documentation/wndb5wn"
 */
public class GrindOffsets
{
	/**
	 * Main entry point
	 *
	 * @param args command-line arguments [-compat:lexid] [-compat:pointer] yamlDir [outputDir]
	 * @throws IOException io
	 */
	public static void main(String[] args) throws IOException
	{
		int[] flags = Grind.flags(args);
		int iArg = flags[1];

		// Tracing
		final long startTime = Tracing.start();

		// Input
		File inDir = new File(args[iArg]);
		Tracing.psInfo.println("[Input] " + inDir.getAbsolutePath());

		// Output
		File outDir = new File(args[iArg + 1]);
		if (!outDir.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			outDir.mkdirs();
		}
		Tracing.psInfo.println("[Output] " + outDir.getAbsolutePath());

		// Supply model
		Tracing.progress("before model is supplied,", startTime);
		CoreModel model = new CoreFactory(inDir).get();
		//Tracing.psInfo.printf("[CoreModel] %s%n%s%n%n", Arrays.toString(model.getSources()), model.info());
		Tracing.progress("after model is supplied,", startTime);

		// Consume model
		Tracing.progress("before model is consumed,", startTime);
		new OffsetMapper(outDir, flags[0], Tracing.psInfo).grind(model);
		new OffsetSerializer(outDir, flags[0], Tracing.psInfo).grind(model);
		Tracing.progress("after model is consumed,", startTime);

		// End
		Tracing.progress("total,", startTime);
	}
}
