package ru.ifmo.nds.jfb.hybrid.ps;

public interface ParameterStrategy {
    int nextIfFailure(int current);
    int nextIfSuccess(int current);
}
