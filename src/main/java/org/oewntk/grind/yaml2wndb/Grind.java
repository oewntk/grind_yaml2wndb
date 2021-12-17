/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.grind.yaml2wndb;

import org.oewntk.model.Model;
import org.oewntk.wndb.out.Flags;
import org.oewntk.wndb.out.ModelConsumer;
import org.oewntk.yaml.in.Factory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Main class that generates the WN database in the WNDB format as per wndb(5WN)
 *
 * @author Bernard Bou
 * @see "https://wordnet.princeton.edu/documentation/wndb5wn"
 */
public class Grind
{
	// Argument switches processing
	public static int[] flags(String[] args) throws IOException
	{
		int[] result = new int[2];

		int i = 0;
		for (; i < args.length; i++)
		{
			if ("-traceTime".equals(args[i])) // if left and is "-traceTime"
			{
				Tracing.traceTime = true;
			}
			else if ("-traceHeap".equals(args[i])) // if left and is "-traceHeap"
			{
				Tracing.traceHeap = true;
			}
			else if ("-compat:pointer".equals(args[i])) // if left and is "-compat:pointer"
			{
				result[0] |= Flags.pointerCompat;
			}
			else if ("-compat:lexid".equals(args[i])) // if left and is "-compat:lexid"
			{
				result[0] |= Flags.lexIdCompat;
			}
			else if ("-compat:verbframe".equals(args[i])) // if left and is "-compat:verbframe"
			{
				result[0] |= Flags.verbFrameCompat;
			}
			else
			{
				break;
			}
		}
		result[1] = i;
		return result;
	}

	/**
	 * Main entry point
	 *
	 * @param args command-line arguments [-compat:lexid] [-compat:pointer] yamlDir [outputDir]
	 * @throws IOException io
	 */
	public static void main(String[] args) throws IOException
	{
		int[] flags = flags(args);
		int iArg = flags[1];

		// Tracing
		final long startTime = Tracing.start();

		// Input
		File inDir = new File(args[iArg]);
		Tracing.psInfo.println("[Input] " + inDir.getAbsolutePath());

		// Input2
		File inDir2 = new File(args[iArg + 1]);
		Tracing.psInfo.println("[Input2] " + inDir2.getAbsolutePath());

		// Output
		File outDir = new File(args[iArg + 2]);
		if (!outDir.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			outDir.mkdirs();
		}
		Tracing.psInfo.println("[Output] " + outDir.getAbsolutePath());

		// Supply model
		Tracing.progress("before model is supplied,", startTime);
		Model model = new Factory(inDir, inDir2).get();
		//Tracing.psInfo.printf("[Model] %s\n%s%n", Arrays.toString(model.getSources()), model.info());
		Tracing.progress("after model is supplied,", startTime);

		// Consume model
		Tracing.progress("before model is consumed,", startTime);
		new ModelConsumer(outDir, flags[0], Tracing.psInfo).grind(model);
		Tracing.progress("after model is consumed,", startTime);

		// End
		Tracing.progress("total,", startTime);
	}
}
