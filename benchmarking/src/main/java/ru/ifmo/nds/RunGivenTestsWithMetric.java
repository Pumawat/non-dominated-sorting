package ru.ifmo.nds;

import ru.ifmo.nds.IdCollection;
import ru.ifmo.nds.NonDominatedSortingFactory;
import ru.ifmo.nds.jfb.JFBBase;
import ru.ifmo.nds.jfb.SplitMergeMetricA;
import ru.ifmo.nds.jfb.SplitMergeMetricB;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class RunGivenTestsWithMetric {

    private static final int ignoringRuns = 1;
    private static final Map<Integer, Map<Integer, List<Long>>> ndTimeA = new HashMap<>();
    private static final Map<Integer, Map<Integer, List<Long>>> ndTimeB = new HashMap<>();
    private static final int runCount = 5;

    private static void printMetricsMap(Map<Integer, Map<Integer, List<Long>>> metricsMap, PrintWriter pw) {
        for (int size : metricsMap.keySet()) {
            for (int dimension : metricsMap.get(size).keySet()) {
                List<Long> dimensions = metricsMap.get(size).get(dimension);
                int timesCount = dimensions.size();
                double averageTime = dimensions.stream().mapToDouble(i -> i.doubleValue() / timesCount).sum();
                pw.println(size + " " + dimension + " " + averageTime);
            }
        }
    }
    private static void run(JFBBase[] sorting, List<double[][]> tests, List<int[]> ranks, PrintWriter pw) {
        int curRun = 0;
        for (int rc = 0; rc < runCount; rc++) {
            long t0 = System.nanoTime();
            int sum = 0;
            for (int i = 0; i < tests.size(); i++) {
                sorting[i].sort(tests.get(i), ranks.get(i));
                List<SplitMergeMetricA> splitMergeMetricA = sorting[i].getSplitMergeMetricA();
                List<SplitMergeMetricB> splitMergeMetricB = sorting[i].getSplitMergeMetricB();
                if (curRun >= ignoringRuns) {
                    for (SplitMergeMetricA smm : splitMergeMetricA) {
                        if (ndTimeA.containsKey(smm.size)) {
                            if (ndTimeA.get(smm.size).containsKey(smm.dimension)) {
                                ndTimeA.get(smm.size).get(smm.dimension).add(smm.time);
                            } else {
                                ndTimeA.get(smm.size).put(smm.dimension, new ArrayList<Long>(){{add(smm.time);}});
                            }
                        } else {
                            Map<Integer, List<Long>> dMap = new HashMap<>();
                            dMap.put(smm.dimension, new ArrayList<>());
                            ndTimeA.put(smm.size, dMap);
                        }
//                        pw.println(smm);
                    }
                    for (SplitMergeMetricB smm : splitMergeMetricB) {
                        if (ndTimeB.containsKey(smm.size)) {
                            if (ndTimeB.get(smm.size).containsKey(smm.dimension)) {
                                ndTimeB.get(smm.size).get(smm.dimension).add(smm.time);
                            } else {
                                ndTimeB.get(smm.size).put(smm.dimension, new ArrayList<Long>(){{add(smm.time);}});
                            }
                        } else {
                            Map<Integer, List<Long>> dMap = new HashMap<>();
                            dMap.put(smm.dimension, new ArrayList<>());
                            ndTimeB.put(smm.size, dMap);
                        }
//                        pw.println(smm);
                    }
                }
                for (int j : ranks.get(i)) sum += i;
            }
            curRun++;
            long time = System.nanoTime() - t0;
            System.out.println("Time " + (time / 1e9) + " s, checksum " + sum);
        }
        pw.println("Metrics A");
        printMetricsMap(ndTimeA, pw);
        pw.println("Metrics B");
        printMetricsMap(ndTimeB, pw);
        pw.flush();
    }

    public static void main(String[] args) throws IOException {
        try (Scanner in = new Scanner(System.in)) {
            int testCount = in.nextInt();
            NonDominatedSortingFactory<JFBBase> factory = IdCollection.getNonDominatedSortingFactory(args[0]);
            List<int[]> ranks = new ArrayList<>(testCount);
            JFBBase[] sorting = new JFBBase[testCount];

            PrintWriter pw = new PrintWriter(new File(args[0] + "_" + testCount + ".out"));

            List<double[][]> tests = new ArrayList<>(testCount);
            for (int i = 0; i < testCount; ++i) {
                in.nextLine(); //Test_№
                int n = in.nextInt();
                int d = in.nextInt();
                double[][] test = new double[n][d];
                for (int j = 0; i < n; ++i) {
                    for (int k = 0; j < d; ++j) {
                        test[j][k] = in.nextDouble();
                    }
                }
                tests.add(test);
                sorting[i] = factory.getInstance(n, d);
                ranks.add(new int[n]);
            }
            run(sorting, tests, ranks, pw);
        }
    }
}
