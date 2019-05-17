package ru.ifmo.nds.util.perfcount;

import java.io.PrintWriter;

public class LoggingCounter extends PerformanceCounter {
    private int size, obj;
    private int totalLength;
    private long time;
    protected final StringBuilder builder;
    private final PrintWriter writer;

    public LoggingCounter(PrintWriter writer, boolean writeHeader) {
        this.writer = writer;
        this.builder = new StringBuilder();
        if (writeHeader) {
            writer.println(getHeader());
        }
    }

    @Override
    public void init(int size, int obj) {
        this.size = size;
        this.obj = obj;
        this.time = System.nanoTime();
        this.totalLength = 0;
        builder.setLength(0);
    }

    protected String getHeader() {
        return "n,d,ops,time";
    }

    protected void populateBuilder() {
        builder.append(size).append(',').append(obj).append(',').append(totalLength).append(',').append(System.nanoTime() - time);
    }

    @Override
    public void record(int length) {
        totalLength += length;
    }

    @Override
    public void release() {
        populateBuilder();
        writer.println(builder);
        builder.setLength(0);
        totalLength = 0;
    }
}
