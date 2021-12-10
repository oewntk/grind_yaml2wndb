/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.grind.yaml2wndb;

import org.oewntk.model.*;
import org.oewntk.wndb.out.Data;
import org.oewntk.wndb.out.DataGrinder;
import org.oewntk.wndb.out.Flags;
import org.oewntk.wndb.out.MorphGrinder;
import org.oewntk.wndb.out.OffsetFactory;
import org.oewntk.wndb.out.SenseIndexer;
import org.oewntk.wndb.out.TagCountGrinder;
import org.oewntk.wndb.out.TemplateGrinder;
import org.oewntk.wndb.out.TemplateIndexer;
import org.oewntk.wndb.out.WordIndexer;
import org.oewntk.yaml.in.Factory;
import org.oewntk.yaml.in.Memory;
import org.oewntk.yaml.in.Memory.Unit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Main class that generates the WN database in the WNDB format as per wndb(5WN)
 *
 * @author Bernard Bou
 * @see "https://wordnet.princeton.edu/documentation/wndb5wn"
 */
public class Grinder
{
	/**
	 * Main entry point
	 *
	 * @param args command-line arguments [-compat:lexid] [-compat:pointer] yamlDir [outputDir]
	 * @throws IOException io
	 */
	public static void main(String[] args) throws IOException
	{
		// Timing
		final long startTime = System.currentTimeMillis();

		// Heap
		boolean traceHeap = false;
		String traceHeapEnv = System.getenv("TRACEHEAP");
		if (traceHeapEnv != null)
		{
			traceHeap = Boolean.parseBoolean(traceHeapEnv);
		}
		if (traceHeap)
		{
			System.err.println(Memory.heapInfo("before maps,", Unit.M));
		}

		// Argument switches processing
		int nArg = args.length; // left
		int iArg = 0; // current

		if (nArg > 0 && "-compat:pointer".equals(args[iArg])) // if left and is "-compat:pointer"
		{
			nArg--; // left: decrement
			iArg++; // current: move to next
			Flags.POINTER_COMPAT = true;
		}

		if (nArg > 0 && "-compat:lexid".equals(args[iArg])) // if left and is "-compat:lexid"
		{
			nArg--; // left: decrement
			iArg++; // current: move to next
			Flags.LEXID_COMPAT = true;
		}
		if (nArg > 0 && "-compat:verbframe".equals(args[iArg])) // if left and is "-compat:verbframe"
		{
			nArg--; // left: decrement
			iArg++; // current: move to next
			Flags.VERBFRAME_COMPAT = true;
		}

		// Input
		File inDir = new File(args[iArg]);
		nArg--; // left: decrement
		iArg++; // current: move to next

		// Input2
		File inDir2 = new File(args[iArg]);
		nArg--; // left: decrement
		iArg++; // current: move to next

		// Output
		File outDir;
		if (nArg > 0) // if left
		//noinspection CommentedOutCode
		{
			outDir = new File(args[iArg]);
			// nArg--; // left: decrement
			// iArg++; // current: move to next
			if (!outDir.exists())
			{
				//noinspection ResultOfMethodCallIgnored
				outDir.mkdirs();
			}
		}
		else
		{
			outDir = new File(".");
		}
		System.err.println("Output " + outDir.getAbsolutePath());

		// Model
		Model model = Factory.makeModel(inDir, inDir2);
		System.err.printf("[Model] %s\n%s%n", Arrays.toString(model.getSources()), model.info());

		// Compute synset offsets
		Map<String, Long> offsets = new OffsetFactory(model.lexesByLemma, model.synsetsById, model.sensesById).compute();

		// Heap
		if (traceHeap)
		{
			System.err.println(Memory.heapInfo("after maps,", Unit.M));
		}

		// Process
		data(outDir, model.lexesByLemma, model.synsetsById, model.sensesById, offsets);
		indexWords(outDir, model.lexesByLemma, model.synsetsById, offsets);
		indexSenses(outDir, model.sensesById, offsets);
		morphs(outDir, model.lexesByLemma);
		indexTemplates(outDir, model.sensesById);
		templates(outDir, model.verbTemplatesById);
		tagcounts(outDir, model.sensesById);

		// Timing
		final long endTime = System.currentTimeMillis();
		System.err.println("Total execution time: " + (endTime - startTime) / 1000 + "s");
	}

	/**
	 * Grind data.{noun|verb|adj|adv}
	 *
	 * @param dir          output directory
	 * @param lexesByLemma lexes mapped by lemma
	 * @param synsetsById  synsets mapped by synset id
	 * @param sensesById   senses mapped by sense id
	 * @param offsets      offsets mapped by synset id
	 * @throws IOException io
	 */
	public static void data(File dir, //
			Map<String, List<Lex>> lexesByLemma, //
			Map<String, Synset> synsetsById, //
			Map<String, Sense> sensesById, //
			Map<String, Long> offsets //
	) throws IOException
	{
		// Data
		DataGrinder grinder = new DataGrinder(lexesByLemma, synsetsById, sensesById, offsets);
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "data.noun")), true, Flags.charSet.name()))
		{
			grinder.makeData(ps, synsetsById, Data.NOUN_POS_FILTER);
			grinder.report();
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "data.verb")), true, Flags.charSet.name()))
		{
			grinder.makeData(ps, synsetsById, Data.VERB_POS_FILTER);
			grinder.report();
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "data.adj")), true, Flags.charSet.name()))
		{
			grinder.makeData(ps, synsetsById, Data.ADJ_POS_FILTER);
			grinder.report();
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "data.adv")), true, Flags.charSet.name()))
		{
			grinder.makeData(ps, synsetsById, Data.ADV_POS_FILTER);
			grinder.report();
		}
	}

	/**
	 * Make word index
	 *
	 * @param dir          output directory
	 * @param lexesByLemma lexes mapped by lemma
	 * @param synsetsById  synsets mapped by synset id
	 * @param offsets      offsets mapped by synset id
	 * @throws IOException io
	 */
	public static void indexWords(File dir, //
			Map<String, List<Lex>> lexesByLemma, //
			Map<String, Synset> synsetsById, //
			Map<String, Long> offsets //
	) throws IOException
	{
		// Index
		WordIndexer indexer = new WordIndexer(offsets);
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.noun")), true, Flags.charSet.name()))
		{
			indexer.makeIndex(ps, lexesByLemma, synsetsById, Data.NOUN_POS_FILTER);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.verb")), true, Flags.charSet.name()))
		{
			indexer.makeIndex(ps, lexesByLemma, synsetsById, Data.VERB_POS_FILTER);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.adj")), true, Flags.charSet.name()))
		{
			indexer.makeIndex(ps, lexesByLemma, synsetsById, Data.ADJ_POS_FILTER);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.adv")), true, Flags.charSet.name()))
		{
			indexer.makeIndex(ps, lexesByLemma, synsetsById, Data.ADV_POS_FILTER);
		}
	}

	/**
	 * Grind index.sense
	 *
	 * @param dir        output directory
	 * @param sensesById senses mapped by id
	 * @param offsets    offsets mapped by synsetId
	 * @throws IOException io
	 */
	public static void indexSenses(File dir, Map<String, Sense> sensesById, Map<String, Long> offsets) throws IOException
	{
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "index.sense")), true, Flags.charSet.name()))
		{
			new SenseIndexer(offsets).makeIndexLowerMultiValue(ps, sensesById);
		}
	}

	/**
	 * Grind {noun|verb|adj|adv}.exc
	 *
	 * @param lexesByLemma lexes mapped by lemma
	 * @param dir          output directory
	 * @throws IOException io
	 */
	public static void morphs(File dir, Map<String, List<Lex>> lexesByLemma) throws IOException
	{
		MorphGrinder grinder = new MorphGrinder();
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "noun.exc")), true, Flags.charSet.name()))
		{
			grinder.makeMorph(ps, lexesByLemma, Data.NOUN_POS_FILTER);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "verb.exc")), true, Flags.charSet.name()))
		{
			grinder.makeMorph(ps, lexesByLemma, Data.VERB_POS_FILTER);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "adj.exc")), true, Flags.charSet.name()))
		{
			grinder.makeMorph(ps, lexesByLemma, Data.ADJ_POS_FILTER);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "adv.exc")), true, Flags.charSet.name()))
		{
			grinder.makeMorph(ps, lexesByLemma, Data.ADV_POS_FILTER);
		}
	}

	/**
	 * Grind sentidx.vrb
	 *
	 * @param dir        output directory
	 * @param sensesById senses mapped by id
	 * @throws IOException io
	 */
	public static void indexTemplates(File dir, Map<String, Sense> sensesById) throws IOException
	{
		TemplateIndexer indexer = new TemplateIndexer();
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "sentidx.vrb")), true, Flags.charSet.name()))
		{
			indexer.makeIndex(ps, sensesById);
		}
	}

	/**
	 * Grind sent.vrb
	 *
	 * @param dir               output directory
	 * @param verbTemplatesById verb templates mapped by id
	 * @throws IOException io
	 */
	public static void templates(File dir, Map<Integer, VerbTemplate> verbTemplatesById) throws IOException
	{
		TemplateGrinder grinder = new TemplateGrinder();
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "sents.vrb")), true, Flags.charSet.name()))
		{
			grinder.makeTemplates(ps, verbTemplatesById);
		}
	}

	/**
	 * Grind cntlist cntlist.rev
	 *
	 * @param dir        output directory
	 * @param sensesById senses mapped by id
	 * @throws IOException io
	 */
	public static void tagcounts(File dir, Map<String, Sense> sensesById) throws IOException
	{
		TagCountGrinder grinder = new TagCountGrinder();
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "cntlist.rev")), true, Flags.charSet.name()))
		{
			grinder.makeTagCountRev(ps, sensesById);
		}
		try (PrintStream ps = new PrintStream(new FileOutputStream(new File(dir, "cntlist")), true, Flags.charSet.name()))
		{
			grinder.makeTagCount(ps, sensesById);
		}
	}
}
