package ru.ifmo.nds.jfb.hybrid;

import ru.ifmo.nds.jfb.Deadline;
import ru.ifmo.nds.jfb.HybridAlgorithmWrapper;
import ru.ifmo.nds.jfb.JFBBase;
import ru.ifmo.nds.jfb.hybrid.ps.ParameterStrategy;
import ru.ifmo.nds.jfb.hybrid.ps.ParameterStrategyFactory;
import ru.ifmo.nds.ndt.Split;
import ru.ifmo.nds.ndt.SplitBuilder;
import ru.ifmo.nds.ndt.TreeRankNode;

public final class NDT extends HybridAlgorithmWrapper {
    private final int threshold3D;
    private final int thresholdAll;
    private final int treeThreshold;
    private final ParameterStrategyFactory threshold3DStrategyFactory;
    private final ParameterStrategyFactory thresholdAllStrategyFactory;


    public NDT(int threshold3D, int thresholdAll, int treeThreshold, ParameterStrategyFactory threshold3DStrategyFactory, ParameterStrategyFactory thresholdAllStrategyFactory) {
        this.threshold3D = threshold3D;
        this.thresholdAll = thresholdAll;
        this.treeThreshold = treeThreshold;
        this.threshold3DStrategyFactory = threshold3DStrategyFactory;
        this.thresholdAllStrategyFactory = thresholdAllStrategyFactory;
    }

    @Override
    public boolean supportsMultipleThreads() {
        return false;
    }

    @Override
    public String getName() {
        return "NDT (threshold 3D = " + threshold3D + ", threshold all = " + thresholdAll + ", tree threshold = " + treeThreshold + ")";
    }

    @Override
    public HybridAlgorithmWrapper.Instance create(int[] ranks, int[] indices, double[][] points, double[][] transposedPoints) {
        return new Instance(ranks, indices, points, transposedPoints, threshold3D, thresholdAll, treeThreshold, threshold3DStrategyFactory.createStrategy(), thresholdAllStrategyFactory.createStrategy());
    }

    private static final class Instance extends HybridAlgorithmWrapper.Instance {
        private SplitBuilder splitBuilder;
        private TreeRankNode tree;

        private double[][] points;
        private double[][] transposedPoints;
        private int[] indices;
        private int[] ranks;

        private double[][] localPoints;

        private int threshold3D;
        private int thresholdAll;
        private final int threshold;
        private final ParameterStrategy threshold3DStrategy;
        private final ParameterStrategy thresholdAllStrategy;

        private Instance(int[] ranks, int[] indices, double[][] points, double[][] transposedPoints, int threshold3D, int thresholdAll, int treeThreshold, ParameterStrategy threshold3DStrategy, ParameterStrategy thresholdAllStrategy) {
            this.ranks = ranks;
            this.indices = indices;
            this.points = points;
            this.transposedPoints = transposedPoints;

            this.threshold3D = threshold3D;
            this.thresholdAll = thresholdAll;
            this.threshold = treeThreshold;
            this.threshold3DStrategy = threshold3DStrategy;
            this.thresholdAllStrategy = thresholdAllStrategy;

            int maximumPoints = indices.length;
            int maximumDimension = transposedPoints.length;
            this.splitBuilder = new SplitBuilder(maximumPoints);
            this.tree = TreeRankNode.EMPTY;
            this.localPoints = new double[maximumPoints][maximumDimension];
        }

        @Override
        public boolean helperAHookCondition(int size, int obj) {
            switch (obj) {
                case 1: return false;
                case 2: return size < threshold3D;
                default: return size < thresholdAll;
            }
        }

        @Override
        public boolean helperBHookCondition(int goodFrom, int goodUntil, int weakFrom, int weakUntil, int obj) {
            return helperAHookCondition(goodUntil - goodFrom + weakUntil - weakFrom, obj);
        }

        @Override
        public int helperAHook(int from, int until, int obj, int maximalMeaningfulRank, Deadline deadline) {
            int M = obj + 1;
            Split split = splitBuilder.result(transposedPoints, from, until, indices, M, threshold);

            for (int i = from; i < until; ++i) {
                System.arraycopy(points[indices[i]], 0, localPoints[i], 0, M);
            }

            int minOverflow = until;
            tree = TreeRankNode.EMPTY;
            for (int i = from; i < until && !deadline.isExceeded(); ++i) {
                int idx = indices[i];
                ranks[idx] = tree.evaluateRank(localPoints[i], ranks[idx], split, M);

                if (ranks[idx] <= maximalMeaningfulRank) {
                    tree = tree.add(localPoints[i], ranks[idx], split, threshold);
                } else if (minOverflow > i) {
                    minOverflow = i;
                }
            }
            if (deadline.wasExceeded()) {
                tree = null;
                return -1;
            }
            tree = null;
            return JFBBase.kickOutOverflowedRanks(indices, ranks, maximalMeaningfulRank, minOverflow, until);
        }

        @Override
        public int helperBHook(int goodFrom, int goodUntil, int weakFrom, int weakUntil, int obj, int tempFrom, int maximalMeaningfulRank, Deadline deadline) {
            int M = obj + 1;
            Split split = splitBuilder.result(transposedPoints, goodFrom, goodUntil, indices, M, threshold);

            for (int good = goodFrom; good < goodUntil; ++good) {
                System.arraycopy(points[indices[good]], 0, localPoints[good], 0, M);
            }
            for (int weak = weakFrom; weak < weakUntil; ++weak) {
                System.arraycopy(points[indices[weak]], 0, localPoints[weak], 0, M);
            }

            int minOverflow = weakUntil;
            tree = TreeRankNode.EMPTY;
            for (int good = goodFrom, weak = weakFrom; weak < weakUntil && !deadline.isExceeded(); ++weak) {
                int wi = indices[weak];
                int gi;
                while (good < goodUntil && (gi = indices[good]) < wi) {
                    tree = tree.add(localPoints[good], ranks[gi], split, threshold);
                    ++good;
                }
                ranks[wi] = tree.evaluateRank(localPoints[weak], ranks[wi], split, M);
                if (minOverflow > weak && ranks[wi] > maximalMeaningfulRank) {
                    minOverflow = weak;
                }
            }
            if (deadline.wasExceeded()) {
                tree = null;
                return -1;
            }
            tree = null;
            return JFBBase.kickOutOverflowedRanks(indices, ranks, maximalMeaningfulRank, minOverflow, weakUntil);
        }

        @Override
        public void modify(int obj) {
            switch (obj) {
                case 1:
                    break;
                case 2:
                    threshold3D = threshold3DStrategy.next(threshold3D);
                    break;
                default:
                    thresholdAll = thresholdAllStrategy.next(thresholdAll);
            }
        }
    }
}
