package ru.ifmo.jfb;

import java.util.Arrays;

public class RedBlackTreeSweepHybridLinearNDS extends RedBlackTreeSweep {
    private int[] howManyDominateMe;
    private int[] candidates;
    private int[] bestGuys;

    public RedBlackTreeSweepHybridLinearNDS(int maximumPoints, int maximumDimension) {
        super(maximumPoints, maximumDimension);
        howManyDominateMe = new int[maximumPoints];
        candidates = new int[maximumPoints];
        bestGuys = new int[maximumPoints];
    }

    @Override
    protected void closeImpl() throws Exception {
        super.closeImpl();
        howManyDominateMe = null;
        candidates = null;
        bestGuys = null;
    }

    @Override
    public String getName() {
        return "Jensen-Fortin-Buzdalov sorting (tree sweep, hybrid with fast NDS)";
    }

    @Override
    protected boolean helperAHookCondition(int size, int obj) {
        switch (obj) {
            case 2: return false;
            case 3: return size < 70;
            default: return size < 400;
        }
    }

    @Override
    protected void helperAHook(int from, int until, int obj) {
        for (int i = from; i < until; ++i) {
            int index = indices[i];
            howManyDominateMe[index] = 0;
            candidates[i - from] = index;
        }
        Arrays.fill(howManyDominateMe, from, until, 0);
        for (int left = from; left < until; ++left) {
            int leftIndex = indices[left];
            for (int right = left + 1; right < until; ++right) {
                int rightIndex = indices[right];
                if (ranks[rightIndex] <= maximalMeaningfulRank && strictlyDominatesAssumingNotSame(leftIndex, rightIndex, obj)) {
                    ++howManyDominateMe[rightIndex];
                }
            }
        }
        int remaining = until - from;
        while (remaining > 0) {
            int bestCount = 0;
            int newRemaining = 0;
            for (int i = 0; i < remaining; ++i) {
                if (howManyDominateMe[candidates[i]] == 0) {
                    bestGuys[bestCount++] = candidates[i];
                } else {
                    candidates[newRemaining++] = candidates[i];
                }
            }
            int nextIndex = 0;
            for (int bi = 0; bi < bestCount; ++bi) {
                int bestGuy = bestGuys[bi];
                int bestGuyRank = ranks[bestGuy];
                while (nextIndex < newRemaining && candidates[nextIndex] < bestGuy) {
                    ++nextIndex;
                }
                for (int i = nextIndex; i < newRemaining; ++i) {
                    int candidate = candidates[i];
                    if (strictlyDominatesAssumingNotSame(bestGuy, candidate, obj)) {
                        --howManyDominateMe[candidate];
                        if (bestGuyRank >= ranks[candidate]) {
                            ranks[candidate] = Math.min(bestGuyRank, maximalMeaningfulRank) + 1;
                        }
                    }
                }
            }
            remaining = newRemaining;
        }
    }

    @Override
    protected boolean helperBHookCondition(int goodSize, int weakSize, int obj) {
        return helperAHookCondition(goodSize + weakSize, obj);
    }

    @Override
    protected void helperBHook(int goodFrom, int goodUntil, int weakFrom, int weakUntil, int obj) {
        for (int good = goodFrom, weakMin = weakFrom; good < goodUntil; ++good) {
            int goodIndex = indices[good];
            int goodRank = ranks[goodIndex];
            while (weakMin < weakUntil && indices[weakMin] < goodIndex) {
                ++weakMin;
            }
            for (int weak = weakMin; weak < weakUntil; ++weak) {
                int weakIndex = indices[weak];
                if (goodRank >= ranks[weakIndex] && strictlyDominatesAssumingNotSame(goodIndex, weakIndex, obj)) {
                    ranks[weakIndex] = 1 + Math.min(goodRank, maximalMeaningfulRank);
                }
            }
        }
    }
}