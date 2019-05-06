package ru.ifmo.nds.util.perfcount;

import java.io.PrintWriter;

public final class LoggingCounter extends PerformanceCounter {
    private int size, obj;
    private int totalLength;
    private long time;
    private final StringBuilder builder;
    private final PrintWriter writer;

    public LoggingCounter(PrintWriter writer) {
        this.writer = writer;
        this.builder = new StringBuilder();
    }

    @Override
    public void init(int size, int obj) {
        this.size = size;
        this.obj = obj;
        this.time = System.nanoTime();
    }

    @Override
    public void record(int length) {
        totalLength += length;
    }

    @Override
    public void release() {
        builder.append(size).append(' ').append(obj).append(' ').append(totalLength).append(' ').append(System.nanoTime() - time);
        writer.println(builder);
        builder.setLength(0);
        totalLength = 0;
    }
}
