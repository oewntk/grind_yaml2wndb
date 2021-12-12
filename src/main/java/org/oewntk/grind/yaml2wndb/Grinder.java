/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.grind.yaml2wndb;

import org.oewntk.model.Model;
import org.oewntk.wndb.out.Flags;
import org.oewntk.wndb.out.ModelConsumer;
import org.oewntk.yaml.in.Factory;
import org.oewntk.yaml.in.Memory;
import org.oewntk.yaml.in.Memory.Unit;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Main class that generates the WN database in the WNDB format as per wndb(5WN)
 *
 * @author Bernard Bou
 * @see "https://wordnet.princeton.edu/documentation/wndb5wn"
 */
public class Grinder
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
				result[0] |= Flags.traceTime;
			}
			else if ("-traceHeap".equals(args[i])) // if left and is "-traceHeap"
			{
				result[0] |= Flags.traceHeap;
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
		boolean traceHeap = (flags[0] & Flags.traceHeap) != 0;
		boolean traceTime = (flags[0] & Flags.traceTime) != 0;
		int iArg = flags[1];

		// Timing
		final long startTime = System.currentTimeMillis();

		// Heap
		if (traceHeap)
		{
			System.err.println(Memory.heapInfo("before maps,", Unit.M));
		}

		// Input
		File inDir = new File(args[iArg]);

		// Input2
		File inDir2 = new File(args[iArg + 1]);

		// Output
		File outDir = new File(args[iArg + 2]);
		if (!outDir.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			outDir.mkdirs();
		}
		System.err.println("Output " + outDir.getAbsolutePath());

		// Heap
		if (traceHeap)
		{
			System.err.println(Memory.heapInfo("before model,", Unit.M));
		}

		// Model
		Model model = new Factory(inDir, inDir2).get();
		System.err.printf("[Model] %s\n%s%n", Arrays.toString(model.getSources()), model.info());

		// Heap
		if (traceHeap)
		{
			System.err.println(Memory.heapInfo("after model,", Unit.M));
		}

		// Consume model
		new ModelConsumer(outDir, flags[0], System.out).grind(model);

		// Timing
		final long endTime = System.currentTimeMillis();
		if (traceTime)
		{
			System.err.println("Total execution time: " + (endTime - startTime) / 1000 + "s");
		}
	}
}
