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
            if (args[0].equals("hypercube")) {
                fillUniformHypercube(random, test, n, i);
            } else if (args[0].equals("correlated")) {
                fillUniformCorrelated(random, test, n, i);
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
}
