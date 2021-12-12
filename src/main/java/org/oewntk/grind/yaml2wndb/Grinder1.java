/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.grind.yaml2wndb;

import org.oewntk.model.CoreModel;
import org.oewntk.model.Model;
import org.oewntk.parse.DataParser1;
import org.oewntk.pojos.ParsePojoException;
import org.oewntk.wndb.out.LineProducer;
import org.oewntk.yaml.in.Factory;

import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;

/**
 * Main class that generates one line of the WN database in the WNDB format as per wndb(5WN)
 *
 * @author Bernard Bou
 * @see "https://wordnet.princeton.edu/documentation/wndb5wn"
 */
public class Grinder1
{
	/**
	 * Main entry point
	 *
	 * @param args command-line arguments (
	 *             [0] source,
	 *             [1] synsetid
	 *             [1] -offset [2] pos, [3] offset)
	 *             [1] -sense [2] senseid
	 *
	 *             # POS (n|v|a|r|s)
	 *             # OFFSET (ie 1740)
	 * @throws IOException io
	 */
	public static void main(String[] args) throws IOException
	{
		int[] flags = Grinder.flags(args);
		int iArg = flags[1];

		// Input
		File source = new File(args[iArg]);

		// SynsetId, SenseId, w31 offset
		String extraArg1 = args[iArg + 1];
		boolean isOffset = extraArg1.equals("-offset");
		boolean isSense = extraArg1.equals("-sense");

		String id;
		BiFunction<CoreModel, String, String> resolver = null;
		if (isSense)
		{
			id = args[iArg + 2];
			resolver = (model, senseId) -> model.sensesById.get(senseId).getSynsetId();
		}
		else if (isOffset)
		{
			char pos = args[iArg + 2].charAt(0);
			long offset31 = Long.parseLong(args[iArg + 3]);
			id = String.format("%08d-%c", offset31, pos);
		}
		else
		{
			id = extraArg1;
		}

		new Grinder1(flags[0], resolver).grind(source, id);
	}

	private final int flags;

	private final BiFunction<CoreModel, String, String> resolver;

	public Grinder1(final int flags, BiFunction<CoreModel, String, String> resolver)
	{
		this.flags = flags;
		this.resolver = resolver;
	}

	void grind(File source, String id) throws IOException
	{
		// Model
		CoreModel model = Factory.makeCoreModel(source);

		// SynsetId
		String synsetId = resolver == null ? id : resolver.apply(model, id);

		// Process
		String line = new LineProducer(flags).apply(model, synsetId);
		consumeLine(line);
	}

	private void consumeLine(String line)
	{
		System.out.println(line);

		// Parse line and pretty print
		try
		{
			org.oewntk.pojos.Synset s = DataParser1.parseSynset(line, false);
			System.out.println(s.toPrettyString());
		}
		catch (ParsePojoException e)
		{
			e.printStackTrace();
		}
	}
}
