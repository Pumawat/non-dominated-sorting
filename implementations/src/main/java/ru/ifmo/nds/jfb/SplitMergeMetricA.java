package ru.ifmo.nds.jfb;

public class SplitMergeMetricA {
    public final int size;
    public final int dimension;
    public final long time;

    public SplitMergeMetricA(int from, int until, long time, int dimension) {
        this.size = until - from;
        this.time = time;
        this.dimension = dimension;
    }

    @Override
    public String toString() {
        return size + " " + dimension + " " + time;
    }
}
