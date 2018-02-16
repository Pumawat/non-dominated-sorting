package ru.ifmo.nds.bos;

import ru.ifmo.nds.util.ArrayHelper;
import ru.ifmo.nds.util.DoubleArraySorter;

import java.util.Arrays;

public class Improved extends AbstractImproved {
    public Improved(int maximumPoints, int maximumDimension) {
        super(maximumPoints, maximumDimension);
    }

    @Override
    public String getName() {
        return "Best Order Sort (improved implementation)";
    }

    @Override
    protected void closeImpl() {
        super.closeImpl();
    }

    private void rankPoint(int currIndex, int[] prevFI, int[] lastFI, int smallestRank, int maximalMeaningfulRank) {
        this.ranks[currIndex] = sequentialSearchRank(
                smallestRank,
                currIndex,
                prevFI,
                lastFI,
                maximalMeaningfulRank);
    }

    @Override
    protected void sortChecked(double[][] points, int[] ranks, int maximalMeaningfulRank) {
        int origN = ranks.length;
        int dim = points[0].length;
        ArrayHelper.fillIdentity(reindex, origN);
        sorter.lexicographicalSort(points, reindex, 0, origN, dim);
        int newN = DoubleArraySorter.retainUniquePoints(points, reindex, this.points, ranks);
        initializeObjectiveIndices(newN, dim);
        maximalMeaningfulRank = Math.min(maximalMeaningfulRank, newN - 1);

        Arrays.fill(this.ranks, 0, newN, -1);
        Arrays.fill(checkIndicesCount, 0, newN, dim);
        Arrays.fill(indexNeededCount, 0, newN, dim);

        for (int i = 0; i < newN; ++i) {
            ArrayHelper.fillIdentity(checkIndices[i], dim);
            Arrays.fill(indexNeeded[i], 0, dim, true);
        }

        for (int d = 0; d < dim; ++d) {
            Arrays.fill(lastFrontIndex[d], 0, newN, -1);
            Arrays.fill(prevFrontIndex[d], 0, newN, -1);
        }

        int smallestRank = 0;

        for (int hIndex = 0, ranked = 0;
             hIndex < newN && smallestRank <= maximalMeaningfulRank && ranked < newN;
             ++hIndex) {
            for (int oIndex = 0; oIndex < dim; ++oIndex) {
                int currIndex = objectiveIndices[oIndex][hIndex];
                int[] prevFI = prevFrontIndex[oIndex];
                int[] lastFI = lastFrontIndex[oIndex];
                if (this.ranks[currIndex] == -1) {
                    rankPoint(currIndex, prevFI, lastFI, smallestRank, maximalMeaningfulRank);
                    ++ranked;
                }
                indexNeeded[currIndex][oIndex] = false;
                int myRank = this.ranks[currIndex];
                if (myRank <= maximalMeaningfulRank) {
                    prevFI[currIndex] = lastFI[myRank];
                    lastFI[myRank] = currIndex;
                }
                if (--indexNeededCount[currIndex] == 0) {
                    if (smallestRank < myRank + 1) {
                        smallestRank = myRank + 1;
                        if (smallestRank > maximalMeaningfulRank) {
                            break;
                        }
                    }
                }
            }
        }

        if (smallestRank > maximalMeaningfulRank) {
            for (int i = 0; i < newN; ++i) {
                if (this.ranks[i] == -1) {
                    this.ranks[i] = maximalMeaningfulRank + 1;
                }
            }
        }

        Arrays.fill(this.points, 0, origN, null);
        for (int i = 0; i < origN; ++i) {
            ranks[i] = this.ranks[ranks[i]];
        }
    }
}
