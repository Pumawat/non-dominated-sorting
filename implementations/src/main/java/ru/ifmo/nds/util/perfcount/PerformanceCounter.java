package ru.ifmo.nds.util.perfcount;

public abstract class PerformanceCounter {
    public abstract void init(int size, int obj);
    public abstract void record(int length);
    public abstract void release();

    public static final PerformanceCounter DUMMY = new PerformanceCounter() {
        @Override public void init(int size, int obj) {}
        @Override public void record(int length) {}
        @Override public void release() {}
    };
}
