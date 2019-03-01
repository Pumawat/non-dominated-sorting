package ru.ifmo.nds;

import ru.ifmo.nds.jfb.JFBBase;
import ru.ifmo.nds.jfb.SplitMergeMetricA;
import ru.ifmo.nds.jfb.SplitMergeMetricB;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class RunGivenTestsWithMetric {
    private static final int ignoringRuns = 1;

    private static final Map<SizeDim, AverageCount> ndTimeAMed = new HashMap<>();
    private static final Map<SizeDim, AverageCount> ndTimeBMed = new HashMap<>();

    private static final int runCount = 5;

    private static void printMetricsMap(Map<SizeDim, AverageCount> metricsMap, PrintWriter pw) {
        for (SizeDim sizeDim : metricsMap.keySet()) {
            pw.println(sizeDim.size + " " + sizeDim.dim + " " + metricsMap.get(sizeDim).average);
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
                    calcA(splitMergeMetricA);
                    calcB(splitMergeMetricB);
                }
                for (int j : ranks.get(i)) sum += j;
            }
            curRun++;
            long time = System.nanoTime() - t0;
            System.out.println("Time " + (time / 1e9) + " s, checksum " + sum);
        }
        pw.println("Metrics A");
        printMetricsMap(ndTimeAMed, pw);
        pw.println("Metrics B");
        printMetricsMap(ndTimeBMed, pw);
        pw.flush();
    }

    private static void calcA(List<SplitMergeMetricA> splitMergeMetricA) {
        Map<Integer, Map<Integer, List<Long>>> ndTimeA = new HashMap<>();
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
        }
        for (int size : ndTimeA.keySet()) {
            for (int dimension : ndTimeA.get(size).keySet()) {
                List<Long> dimensions = ndTimeA.get(size).get(dimension);
                int timesCount = dimensions.size();
                int count = ndTimeAMed.getOrDefault(new SizeDim(size, dimension), new AverageCount(0, 0)).count;
                double average = ndTimeAMed.getOrDefault(new SizeDim(size, dimension), new AverageCount(0, 0)).average;
                double averageTime = dimensions.stream().mapToDouble(i -> i.doubleValue() / timesCount).sum();
                ndTimeAMed.put(new SizeDim(size, dimension),
                               new AverageCount(average / (count + timesCount) * count + averageTime / (count + timesCount) * timesCount, timesCount + count));
            }
        }

    }


    private static void calcB(List<SplitMergeMetricB> splitMergeMetricB) {
        Map<Integer, Map<Integer, List<Long>>> ndTimeB = new HashMap<>();
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
        }
        for (int size : ndTimeB.keySet()) {
            for (int dimension : ndTimeB.get(size).keySet()) {
                List<Long> dimensions = ndTimeB.get(size).get(dimension);
                int timesCount = dimensions.size();
                int count = ndTimeBMed.getOrDefault(new SizeDim(size, dimension), new AverageCount(0, 0)).count;
                double average = ndTimeBMed.getOrDefault(new SizeDim(size, dimension), new AverageCount(0, 0)).average;
                double averageTime = dimensions.stream().mapToDouble(i -> i.doubleValue() / timesCount).sum();
                ndTimeBMed.put(new SizeDim(size, dimension),
                        new AverageCount(average / (count + timesCount) * count + averageTime / (count + timesCount) * timesCount, timesCount + count));
            }
        }

    }

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);
        try (Scanner in = new Scanner(new File(args[0] + ".in"));
             PrintWriter pw = new PrintWriter(new File(args[0] + ".out"))
        ) {
            int testCount = in.nextInt();
            NonDominatedSortingFactory<JFBBase> factory = IdCollection.getNonDominatedSortingFactory(args[1]);
            List<int[]> ranks = new ArrayList<>(testCount);
            JFBBase[] sorting = new JFBBase[testCount];

            List<double[][]> tests = new ArrayList<>(testCount);
            for (int i = 0; i < testCount; ++i) {
                in.next(); //Test_№
                in.next(); //Test_№
                int n = in.nextInt();
                int d = in.nextInt();
                double[][] test = new double[n][d];
                for (int j = 0; j < n; ++j) {
                    for (int k = 0; k < d; ++k) {
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

class SizeDim {
    public final int size;
    public final int dim;

    SizeDim(int size, int dim) {
        this.size = size;
        this.dim = dim;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SizeDim sizeDim = (SizeDim) o;
        return size == sizeDim.size &&
                dim == sizeDim.dim;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, dim);
    }
}

class AverageCount {
    public final double average;
    public final int count;


    public AverageCount(double average, int count) {
        this.average = average;
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AverageCount that = (AverageCount) o;
        return Double.compare(that.average, average) == 0 &&
                count == that.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(average, count);
    }
}