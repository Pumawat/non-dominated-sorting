package ru.ifmo.nds.jfb.hybrid.ps;

public class ConstDecreaseParameterStrategy implements ParameterStrategy {
    private final int decreaseConst;

    ConstDecreaseParameterStrategy(int decreaseConst) {
        this.decreaseConst = decreaseConst;
    }

    @Override
    public int nextIfFailure(int current) {
        int res = current - decreaseConst;
        return res <= 0 ? current : res;
    }

    @Override
    public int nextIfSuccess(int current) {
        return current;
    }
}
