package ru.ifmo.nds;

import ru.ifmo.nds.jfb.JFBDouble;
import ru.ifmo.nds.jfb.JFBInt;
import ru.ifmo.nds.jfb.hybrid.Dummy;
import ru.ifmo.nds.jfb.hybrid.ENS;
import ru.ifmo.nds.jfb.hybrid.LinearNDS;
import ru.ifmo.nds.jfb.hybrid.NDT;
import ru.ifmo.nds.jfb.hybrid.ps.OneFifthRuleParameterStrategyFactory;
import ru.ifmo.nds.jfb.hybrid.ps.ParameterStrategyFactory;
import ru.ifmo.nds.util.FenwickRankQueryStructureDouble;
import ru.ifmo.nds.util.RedBlackRankQueryStructure;
import ru.ifmo.nds.util.VanEmdeBoasRankQueryStructureInt;

public final class JensenFortinBuzdalov {
    private JensenFortinBuzdalov() {}

    public static NonDominatedSortingFactory<JFBDouble> getRedBlackTreeSweepImplementation(int allowedThreads) {
        return (p, d) -> new JFBDouble(new RedBlackRankQueryStructure(p), d, allowedThreads, Dummy.getWrapperInstance());
    }

    public static NonDominatedSortingFactory<JFBDouble> getFenwickSweepImplementation(int allowedThreads) {
        return (p, d) -> new JFBDouble(new FenwickRankQueryStructureDouble(p), d, allowedThreads, Dummy.getWrapperInstance());
    }

    public static NonDominatedSortingFactory<JFBInt> getVanEmdeBoasImplementation() {
        return (p, d) -> new JFBInt(new VanEmdeBoasRankQueryStructureInt(p), d, 1, Dummy.getWrapperInstance());
    }

    public static NonDominatedSortingFactory<JFBInt> getVanEmdeBoasHybridENSImplementation() {
        return (p, d) -> new JFBInt(new VanEmdeBoasRankQueryStructureInt(p), d, 1, new ENS(100, 200, ParameterStrategyFactory.CONST, ParameterStrategyFactory.CONST));
    }

    public static NonDominatedSortingFactory<JFBInt> getVanEmdeBoasHybridNDTImplementation(int threshold) {
        return (p, d) -> new JFBInt(new VanEmdeBoasRankQueryStructureInt(p), d, 1, new NDT(100, 20000, threshold, ParameterStrategyFactory.CONST, ParameterStrategyFactory.CONST));
    }

    public static NonDominatedSortingFactory<JFBDouble> getRedBlackTreeSweepHybridFNDSImplementation(int allowedThreads) {
        return (p, d) -> new JFBDouble(new RedBlackRankQueryStructure(p), d, allowedThreads, LinearNDS.getWrapperInstance());
    }

    public static NonDominatedSortingFactory<JFBDouble> getRedBlackTreeSweepHybridENSImplementation(int allowedThreads) {
        return (p, d) -> new JFBDouble(new RedBlackRankQueryStructure(p), d, allowedThreads, new ENS(100, 200, new OneFifthRuleParameterStrategyFactory(), new OneFifthRuleParameterStrategyFactory()));
    }

    public static NonDominatedSortingFactory<JFBDouble> getRedBlackTreeSweepHybridNDTImplementation(int threshold, int allowedThreads) {
        return (p, d) -> new JFBDouble(new RedBlackRankQueryStructure(p), d, allowedThreads, new NDT(100, 20000, threshold, ParameterStrategyFactory.CONST, ParameterStrategyFactory.CONST));
    }
}
//new OneFifthRuleParameterStrategyFactory(), new OneFifthRuleParameterStrategyFactory()
//new ConstDecreaseParameterStrategyFactory(0), new ConstDecreaseParameterStrategyFactory(0)