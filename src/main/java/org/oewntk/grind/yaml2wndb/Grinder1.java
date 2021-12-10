/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.grind.yaml2wndb;

import org.oewntk.model.Lex;
import org.oewntk.model.Model;
import org.oewntk.model.Sense;
import org.oewntk.model.Synset;
import org.oewntk.wndb.out.DataGrinder;
import org.oewntk.wndb.out.OffsetFactory;
import org.oewntk.yaml.in.Factory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.oewntk.parse.DataParser1;
import org.oewntk.pojos.ParsePojoException;

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
	 * @param args command-line arguments ([0] merged XML filename,[1] pos, [2] offset) # 1 input XML file # 2 SYNSETID | -sense | -offset # 3 SENSEID | POS
	 *             (n|v|a|r|s) # 4 OFFSET (ie 1740)
	 * @throws IOException io
	 */
	public static void main(String[] args) throws IOException
	{
		// Input
		File inDir = new File(args[0]);
		File inDir2 = new File(args[1]);
		String extraArg1 = args[2];
		boolean isOffset = extraArg1.equals("-offset");
		boolean isSense = extraArg1.equals("-sense");
		String extraArg2 = isOffset || isSense ? args[3] : null;
		String extraArg3 = isOffset ? args[4] : null;

		// Model
		Model model = Factory.makeModel(inDir, inDir2);
		System.err.printf("model %s\n%s%n", model, model.info());

		// Compute synset offsets
		Map<String, Long> offsets = new OffsetFactory(model.lexesByLemma, model.synsetsById, model.sensesById).compute();

		// SynsetId, SenseId, w31 offset
		String synsetId;
		if (isSense)
		{
			Sense senseElement = model.sensesById.get(extraArg2);
			synsetId = senseElement.getSynsetId();
		}
		else if (isOffset)
		{
			char pos = extraArg2.charAt(0);
			long offset31 = Long.parseLong(extraArg3);
			synsetId = String.format("%08d-%c", offset31, pos);
		}
		else
		{
			synsetId = extraArg1;
		}

		// Process
		Synset synset = model.synsetsById.get(synsetId);
		long offset = offsets.get(synsetId);
		if (!offsets.containsValue(offset))
		{
			System.err.printf("%d is not a valid offset", offset);
			System.exit(1);
		}

		// Print line
		String line = data(synset, offset, model.lexesByLemma, model.synsetsById, model.sensesById, offsets);
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

	/**
	 * Grind data for this synset
	 *
	 * @param synset       synset
	 * @param offset       offset
	 * @param lexesByLemma lexes mapped by lemma
	 * @param synsetsById  synset elements mapped by id
	 * @param sensesById   sense elements mapped by id
	 * @param offsets      offsets mapped by synsetId
	 * @return line
	 */
	public static String data(Synset synset, long offset, //
			Map<String, List<Lex>> lexesByLemma, //
			Map<String, Synset> synsetsById, //
			Map<String, Sense> sensesById, //
			Map<String, Long> offsets //
	)
	{
		// Data
		DataGrinder factory = new DataGrinder(lexesByLemma, synsetsById, sensesById, offsets);
		return factory.getData(synset, offset);
	}
}
