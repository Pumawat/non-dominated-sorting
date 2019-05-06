package ru.ifmo.nds;

import ru.ifmo.nds.jfb.JFBDouble;
import ru.ifmo.nds.jfb.hybrid.ENS;
import ru.ifmo.nds.util.RedBlackRankQueryStructure;
import ru.ifmo.nds.util.perfcount.LoggingCounter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.ThreadLocalRandom;

public class RunStatisticsCollection {
    public static void main(String[] args) throws FileNotFoundException {
        int n = Integer.parseInt(args[0]);
        int d = Integer.parseInt(args[1]);
        int nTests = Integer.parseInt(args[2]);
        int count = Integer.parseInt(args[3]);
        String file = args[4];
        try (PrintWriter out = new PrintWriter(file)) {
            NonDominatedSorting sorting = new JFBDouble(new RedBlackRankQueryStructure(n), d, 1,
                    new ENS(100, 200, () -> new LoggingCounter(out)));

            int[] ranks = new int[n];
            //noinspection MismatchedReadAndWriteOfArray not true, as we write to the elements of elements of the big array.
            double[][][] tests = new double[nTests][n][d];
            for (double[][] a : tests) {
                for (double[] aa : a) {
                    for (int i = 0; i < aa.length; ++i) {
                        aa[i] = ThreadLocalRandom.current().nextDouble();
                    }
                }
            }

            for (int iteration = 0; iteration < count; ++iteration) {
                long t0 = System.currentTimeMillis();
                int sum = 0;
                for (double[][] test : tests) {
                    sorting.sort(test, ranks);
                    for (int i : ranks) sum += i;
                }
                System.out.println("Iteration " + iteration + " done in " + (System.currentTimeMillis() - t0) + " ms, checksum " + sum);
            }
        }
    }
}
