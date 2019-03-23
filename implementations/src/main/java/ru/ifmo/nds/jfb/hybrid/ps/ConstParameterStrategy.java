package ru.ifmo.nds.jfb.hybrid.ps;

public enum ConstParameterStrategy implements ParameterStrategy {
    INSTANCE;

    @Override
    public int nextIfFailure(int current) {
        return current;
    }

    @Override
    public int nextIfSuccess(int current) {
        return current;
    }
}
