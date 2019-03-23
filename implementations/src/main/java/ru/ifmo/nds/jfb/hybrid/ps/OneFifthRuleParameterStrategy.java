package ru.ifmo.nds.jfb.hybrid.ps;

public class OneFifthRuleParameterStrategy implements ParameterStrategy {

    private final double failure = 1.5;
    private final double success = Math.pow(1.5, 1.0 / 19);

    @Override
    public int nextIfFailure(int current) {
        return (int) (current / failure);
    }

    @Override
    public int nextIfSuccess(int current) {
        return (int) (current * success);
    }
}
