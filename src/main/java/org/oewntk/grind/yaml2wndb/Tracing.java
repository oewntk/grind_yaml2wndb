/*
 * Copyright (c) $originalComment.match("Copyright \(c\) (\d+)", 1, "-")2021. Bernard Bou.
 */

package org.oewntk.grind.yaml2wndb;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Heap Memory utilities
 *
 * @author Bernard Bou
 */
public class Tracing
{
	static final PrintStream psInfo = System.out;
	static final PrintStream psErr = System.err;
	private static final PrintStream psHeap = System.out;
	private static final PrintStream psTime = System.out;

	public static boolean traceHeap = false;

	public static boolean traceTime = false;

	public static long start()
	{
		final long startTime = System.currentTimeMillis();

		// heap state
		if (traceHeap)
		{
			psHeap.println("[Memory]: " + Memory.memoryInfo("before,", Memory.Unit.M));
			psHeap.println("[Heap]: " + Memory.heapInfo("before,", Memory.Unit.M));
		}
		return startTime;
	}

	public static void progress(String message, long startTime)
	{
		if (traceTime)
		{
			final long endTime = System.currentTimeMillis();
			psTime.println("[Time]: " + (endTime - startTime) / 1000 + "s");
		}

		// heap state
		if (traceHeap)
		{
			psHeap.println("[Heap]: " + Memory.heapInfo(message, Memory.Unit.M));
		}
	}

	static public class Memory
	{
		private Memory()
		{
		}

		public enum Unit
		{
			U(1), K(1024 * U.div), M(1024 * K.div), G(1024 * M.div);

			public final long div;

			Unit(final long div)
			{
				this.div = div;
			}
		}

		static DecimalFormat formatter()
		{
			final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
			final DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
			symbols.setGroupingSeparator(' ');
			formatter.setDecimalFormatSymbols(symbols);
			return formatter;
		}

		public static String heapInfo(final String tag, final Unit u)
		{
			final long max = Runtime.getRuntime().maxMemory();
			final long total = Runtime.getRuntime().totalMemory();
			final long free = Runtime.getRuntime().freeMemory();
			long used = total - free;
			final long future = max - total;
			long avail = free + future;

			used /= u.div;
			avail /= u.div;

			final DecimalFormat formatter = Memory.formatter();
			return String.format("%s used: %s%s maxfree: %s%s", //
					tag, //
					formatter.format(used), u, //
					formatter.format(avail), u //
			);
		}

		public static String memoryInfo(final String tag, final Unit u)
		{
			long max = Runtime.getRuntime().maxMemory();
			long total = Runtime.getRuntime().totalMemory();
			long free = Runtime.getRuntime().freeMemory();
			long used = total - free;
			final long future = max - total;
			long avail = free + future;

			max /= u.div; // This will return Long.MAX_VALUE if there is no preset limit
			total /= u.div;
			free /= u.div;
			used /= u.div;
			avail /= u.div;

			final DecimalFormat formatter = Memory.formatter();
			return String.format("%s max=%15s%s total=%10s%s used=%15s%s free=%15s%s avail=%15s%s", //
					tag, //
					formatter.format(max), u, //
					formatter.format(total), u, //
					formatter.format(used), u, //
					formatter.format(free), u, //
					formatter.format(avail), u);
		}
	}
}
