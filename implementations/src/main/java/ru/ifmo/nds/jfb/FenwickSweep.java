package ru.ifmo.nds.jfb;

import ru.ifmo.nds.util.FenwickRankQueryStructure;
import ru.ifmo.nds.util.RankQueryStructure;

public class FenwickSweep extends AbstractJFBSorting {
    public FenwickSweep(int maximumPoints, int maximumDimension, int allowedThreads) {
        super(maximumPoints, maximumDimension, allowedThreads);
    }

    @Override
    protected RankQueryStructure createStructure(int maximumPoints) {
        return new FenwickRankQueryStructure(maximumPoints);
    }

    @Override
    public String getName() {
        return "Jensen-Fortin-Buzdalov sorting, " + getThreadDescription() + " (Fenwick sweep)";
    }

}
