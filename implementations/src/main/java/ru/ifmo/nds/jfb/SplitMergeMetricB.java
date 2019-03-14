package ru.ifmo.nds.jfb;

public class SplitMergeMetricB {
    public final int size;
    public final int dimension;
    public final long time;

    public SplitMergeMetricB(int goodFrom, int goodUntil, int weakFrom, int weakUntil, int dimension, long time) {
        this.size = goodUntil - goodFrom + weakUntil - weakFrom;
        this.dimension = dimension;
        this.time = time;
    }

    @Override
    public String toString() {
        return size + " " + dimension + " " + time;
    }
}
