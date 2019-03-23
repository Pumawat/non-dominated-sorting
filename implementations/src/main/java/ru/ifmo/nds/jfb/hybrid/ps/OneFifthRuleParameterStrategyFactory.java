package ru.ifmo.nds.jfb.hybrid.ps;

public class OneFifthRuleParameterStrategyFactory implements ParameterStrategyFactory {
    @Override
    public ParameterStrategy createStrategy() {
        return new OneFifthRuleParameterStrategy();
    }
}
