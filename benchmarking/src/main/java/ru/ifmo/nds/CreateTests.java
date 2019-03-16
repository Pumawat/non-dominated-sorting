package ru.ifmo.nds;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class CreateTests {
    public static void main(String[] args) throws IOException {
        int n = Integer.parseInt(args[1]);
        int dMin = Integer.parseInt(args[2]);
        int dMax = Integer.parseInt(args[3]);

        List<double[][]> tests = new ArrayList<>(dMax - dMin + 1);
        Random random = new Random(8234925);

        for (int i = dMin; i <= dMax; i++) {
            double[][] test = new double[n][i];
            switch (args[0]) {
                case "hypercube":
                    fillUniformHypercube(random, test, n, i);
                    break;
                case "correlated":
                    fillUniformCorrelated(random, test, n, i);
                    break;
                case "hyperplane":
                    fillUniformHyperplane(random, test, n, i, args[4]); //args[4] is f: ("1", "2", "n/2", "n")
                    break;
            }
            tests.add(test);
        }

        try (PrintWriter pw = new PrintWriter(new File(args[0] + "_" + n + "_" + dMax + ".in"))) {
            pw.println(tests.size());
            for (int i = 0; i < tests.size(); ++i) {
                int d = dMin + i;
                pw.println("Test " + i);
                pw.println(n + " " + d);
                for (int j = 0; j < n; ++j) {
                    for (int k = 0; k < d; ++k) {
                        pw.print(tests.get(i)[j][k] + " ");
                    }
                    pw.println();
                }
            }
        }
    }

    private static void fillUniformHypercube(Random random, double[][] test, int n, int d) {
        for (int j = 0; j < n; ++j) {
            for (int k = 0; k < d; ++k) {
                test[j][k] = random.nextDouble();
            }
        }
    }

    private static void fillUniformCorrelated(Random random, double[][] test, int n, int d) {
        int i = 0; //TODO: what to do if only one test for each d?
        int x = i % 2 == 0 ? 1 : d - 2;
        for (int j = 0; j < n; ++j) {
            double first = random.nextDouble();
            for (int k = 0; k < d; ++k) {
                test[j][k] = k == x ? -first : first;
            }
        Collections.shuffle(Arrays.asList(test), random);
        }
    }

    private static void fillUniformHyperplane(Random random, double[][] test, int n, int d, String f) {
        int realF;
        if (f.equals("n")) {
            realF = n;
        } else if (f.startsWith("n/")) {
            realF = n / Integer.parseInt(f.substring(2));
        } else {
            realF = Integer.parseInt(f);
        }

        int frontSize = n / realF;
        int firstFrontSize = n - (realF - 1) * frontSize;

        for (int i = 0; i < firstFrontSize; ++i) {
            double sum = 1.0;
            for (int j = d - 1; j > 0; --j) {
                test[i][j] = sum * (1 - Math.pow(1 - random.nextDouble(), 1.0 / j));
                sum -= test[i][j];
            }
            test[i][0] = sum;
        }
        for (int i = firstFrontSize; i < n; ++i) {
            test[i] = test[i - frontSize].clone();
            for (int j = 0; j < d; ++j) {
                test[i][j] += 1e-9;
            }
        }
        Collections.shuffle(Arrays.asList(test), random);
    }

}
