package CPPGAi;

import javafx.application.Platform;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Genetic Algorithm Tools
 *
 * @author Scott Matsumura
 * @version 1.0
 */
class GeneticAlgorithmTools {

    private final int MAX_DISPLAY_RATIO = 200000;

    private boolean isRunningPrintPopulation_;
    private boolean isRunningGetStandardDeviation_;
    private boolean stopProcess_;

    /**
     * GeneticAlgorithmTools Constructor
     */
    GeneticAlgorithmTools() {
        isRunningPrintPopulation_ = false;
        isRunningGetStandardDeviation_ = false;
        stopProcess_ = false;
    }

    /**
     * To stop threads
     */
    void stop() {
        stopProcess_ = true;
    }

    /**
     * To determine if threads are running
     *
     * @return      "true" if threads are running.
     *              "false" if not.
     */
    boolean isRunning() {
        return isRunningPrintPopulation_ || isRunningGetStandardDeviation_;
    }



    final void printPopulation(final int[][] population, final int[] fitness, final int populationSize,
                               final int chromosomeSize, final int fitnessThreshold,
                               final Consumer<String> function) {
        stopProcess_ = false;
        isRunningPrintPopulation_ = true;
        int maxLines = MAX_DISPLAY_RATIO / chromosomeSize;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            int count = 0;
            int capacity = populationSize * chromosomeSize;
            if (capacity > maxLines) {
                capacity = maxLines;
            }
            final StringBuilder stringBuilder = new StringBuilder(capacity);

            for (int i = 0; i < populationSize && count < maxLines && !stopProcess_; i++) {
                if (fitness[i] <= fitnessThreshold) {
                    count++;
                    stringBuilder.append(String.format("%4d", count)).append(" ").append("(Fitness: ")
                            .append(String.format("%2d", fitness[i])).append(") ").append(Arrays.toString(population[i]))
                            .append("\n");
                }
            }

            if (count >= maxLines) {
                stringBuilder.append("Maximum display of " + maxLines + " for chromosome size " + chromosomeSize + "\n");
            }

            String string = "";
            if (!stopProcess_) {
                string = stringBuilder.toString();
            }
            final String returnString = string;
            executorService.shutdown();
            isRunningPrintPopulation_ = false;
            stopProcess_ = false;
            if (function != null) {
                Platform.runLater(() -> function.accept(returnString));
            }
        });
    }

    /**
     * Standard Deviation
     *
     * @param population            Population array
     * @param populationSize        Size of population
     * @param chromosomeSize        Size of chromosomes
     */
    final void getStandardDeviation(final int[][] population, final int populationSize, final int chromosomeSize,
                                    final Consumer<String> function) {
        stopProcess_ = false;
        isRunningGetStandardDeviation_ = true;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            long[] sumOfSquares = new long[chromosomeSize];
            long[] total = new long[chromosomeSize];

            for (int i = 0; i < chromosomeSize && !stopProcess_; i++) {
                for (int j = 0; j < populationSize; j++) {
                    sumOfSquares[i] += population[j][i] * population[j][i];
                    total[i] += population[j][i];
                }
            }

            double[] meanSquared = new double[chromosomeSize];
            for (int i = 0; i < chromosomeSize && !stopProcess_ ; i++) {
                meanSquared[i] = (total[i] * total[i]) / (double) populationSize;
            }

            double standardDeviationTotal = 0;
            for (int i = 0; i < chromosomeSize && !stopProcess_; i++) {
                standardDeviationTotal += Math.sqrt((sumOfSquares[i] - meanSquared[i]) / populationSize);
            }

            String returnString = String.valueOf(standardDeviationTotal / chromosomeSize);
            executorService.shutdown();
            isRunningGetStandardDeviation_ = false;
            stopProcess_ = false;
            if (function != null) {
                Platform.runLater(() -> function.accept(returnString));
            }
        });
    }
}
