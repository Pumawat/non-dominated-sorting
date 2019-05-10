package ru.ifmo.nds;

import ru.ifmo.nds.jfb.HybridAlgorithmWrapper;
import ru.ifmo.nds.jfb.JFBDouble;
import ru.ifmo.nds.jfb.hybrid.NDT;
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
        String hybrid = args[5];

        int[] iterationSlot = new int[1];
        try (PrintWriter out = new PrintWriter(file)) {
            HybridAlgorithmWrapper wrapper;
            switch (hybrid) {
                case "ens":
                    wrapper = new ENS(100, 200, () -> new LoggingExtraCounter(out, iterationSlot));
                    break;
                case "ndt":
                    wrapper = new NDT(100, 20000, 4, () -> new LoggingExtraCounter(out, iterationSlot));
                    break;
                default:
                    throw new IllegalArgumentException("The hybrid '" + hybrid
                            + "' is unknown (shall be either 'ens' or 'ndt')");
            }
            NonDominatedSorting sorting = new JFBDouble(new RedBlackRankQueryStructure(n), d, 1, wrapper);
            int[][] ranks = new int[nTests][];
            double[][][] tests = new double[nTests][][];
            for (int i = 0; i < nTests; ++i) {
                int nn = ThreadLocalRandom.current().nextInt(n) + 1;
                double[][] test = new double[nn][ThreadLocalRandom.current().nextInt(4, d + 1)];
                tests[i] = test;
                ranks[i] = new int[nn];
                for (double[] aa : test) {
                    for (int j = 0; j < aa.length; ++j) {
                        aa[j] = ThreadLocalRandom.current().nextDouble();
                    }
                }
            }

            for (int iteration = 0; iteration < count; ++iteration) {
                iterationSlot[0] = iteration;
                long t0 = System.currentTimeMillis();
                int sum = 0;
                for (int t = 0; t < nTests; ++t) {
                    sorting.sort(tests[t], ranks[t]);
                    for (int i : ranks[t]) sum += i;
                }
                System.out.println("Iteration " + iteration + " done in " + (System.currentTimeMillis() - t0) + " ms, checksum " + sum);
            }
        }
    }

    private static class LoggingExtraCounter extends LoggingCounter {
        private int[] auxSlot;

        LoggingExtraCounter(PrintWriter writer, int[] auxSlot) {
            super(writer);
            this.auxSlot = auxSlot;
        }

        @Override
        protected String getHeader() {
            return super.getHeader() + ",iteration";
        }

        @Override
        protected void populateBuilder() {
            super.populateBuilder();
            builder.append(',').append(auxSlot[0]);
        }
    }
}
