package CPPGAi;

import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Genetic Algorithm for Integers (int)
 *
 * @author Scott Matsumura
 * @version 1.0
 */
abstract class GeneticAlgorithmInteger {

    private final int CORES = Runtime.getRuntime().availableProcessors();
    private final int NO_FITNESS_VALUE = Integer.MIN_VALUE;

    private int minGeneValue_;
    private int geneSize_;
    private int chromosomeSize_;
    private int populationSize_;

    private boolean useCallNewChromosome_;

    private int[][] population_;
    private int[] fitness_;

    private boolean stopProcess_;
    private boolean isPopulated_;
    private boolean isRunningPopulate_;
    private boolean isRunningEvolution_;

    @FunctionalInterface
    private interface CrossoverMethod {
        int[] apply(final int[] parentA, final int[] parentB, final int genomeSize,
                    final int crossoverType, final Boolean[] uniformBooleanArray);
    }

    @FunctionalInterface
    private interface MutateOptimizeMethod {
        int[] get(int[] child, final int[] randomFillArray, final int minGeneValue, final int geneSize,
                  final int genomeSize, final int mutationAmount, final int optimizeAmount, final int nextIndex);
    }

    /**
     * GeneticAlgorithmInteger Constructor
     */
    GeneticAlgorithmInteger() {
        populationSize_ = -1;
        chromosomeSize_ = -1;
        useCallNewChromosome_ = false;
        stopProcess_ = false;
        isRunningPopulate_ = false;
        isRunningEvolution_ = false;
        isPopulated_ = false;
    }

    /**
     *
     * Set Population
     * Used for loading from a saved file.
     *
     * @param population        Population array (use null if intended to populate a new array)
     * @param fitness           Fitness array (use null if intended to populate a new array)
     * @param minGeneValue      Lower bound of gene value
     * @param geneSize          Size of gene
     */
    final void setPopulation(int[][] population, int[] fitness, int minGeneValue, int geneSize) {
        population_ = population;
        fitness_ = fitness;
        minGeneValue_ = minGeneValue;
        geneSize_ = geneSize;
        if (population == null || fitness == null) {
            chromosomeSize_ = -1;
            populationSize_ = -1;
            isPopulated_ = false;
        } else {
            chromosomeSize_ = population[0].length;
            populationSize_ = population.length;
            isPopulated_ = true;
        }
    }

    /**
     * Use callNewChromosome method
     *
     * @param setting       "true" use callNewChromosome method
     *                      "false" does not use callNewChromosome method
     */
    final void useCallNewChromosome(boolean setting) {
        useCallNewChromosome_ = setting;
    }

    /**
     * Checks if threads are in running.
     *
     * @return      "true" is created threads are still working
     *              "false" is threads are finished
     */
    final boolean isRunning() {
        return isRunningPopulate_ || isRunningEvolution_;
    }

    /**
     * Checks if array is populated.
     *
     * @return      "true" is array is populated
     *              "false" is array is not populated
     */
    final boolean isPopulated() {
        return isPopulated_;
    }

    /**
     * Populates a new array
     *
     * @param minGeneValue      Lower bound of gene value
     * @param geneSize          Size of gene
     * @param chromosomeSize    Size of chromosome
     * @param populationSize    Size of population
     * @param function          Method to be executed when array is populated or interrupted
     */
    final void runPopulate(final int minGeneValue, final int geneSize, final int chromosomeSize, final int populationSize,
                           final Consumer<Boolean> function) {
        if (!this.isRunning()) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                isRunningPopulate_ = true;
                int[][] newPopulation = new int[populationSize][];
                int[] newFitness = new int[populationSize];
                this.populateArray(newPopulation, newFitness, minGeneValue, geneSize, chromosomeSize, populationSize,
                        0, function);
            });
            executorService.shutdown();
        }
    }

    /**
     * Populates or shrinks a preexisting array
     *
     * @param newPopulationSize     Size of new population
     * @param function              Boolean Consumer function to be executed when array is populated or interrupted
     */
    final void editPopulate(final int newPopulationSize, final Consumer<Boolean> function) {
        if (!this.isRunning()) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                isRunningPopulate_ = true;
                if (newPopulationSize < populationSize_) {
                    int[][] newPopulation = new int[newPopulationSize][];
                    int[] newFitness = new int[newPopulationSize];
                    System.arraycopy(population_, 0, newPopulation, 0, newPopulationSize);
                    System.arraycopy(fitness_, 0, newFitness, 0, newPopulationSize);
                    populationSize_ = newPopulationSize;
                    population_ = newPopulation;
                    fitness_ = newFitness;
                    isRunningPopulate_ = false;
                    Platform.runLater(() -> function.accept(true));
                } else if (newPopulationSize > populationSize_) {
                    int[][] newPopulation = new int[newPopulationSize][];
                    int[] newFitness = new int[newPopulationSize];
                    System.arraycopy(population_, 0, newPopulation, 0, populationSize_);
                    System.arraycopy(fitness_, 0, newFitness, 0, populationSize_);
                    int firstIndex = populationSize_;
                    this.populateArray(newPopulation, newFitness, minGeneValue_, geneSize_, chromosomeSize_, newPopulationSize,
                            firstIndex, function);
                }
            });
            executorService.shutdown();
        }
    }

    /**
     * Runs the genetic algorithm.
     * This assumes arrays are populated.
     *
     * @param crossoverType         Number of crossover or "0" for uniform crossover
     * @param mutationPercent       Mutation percentage (0 - 100)
     * @param optimizePercent       Optimization percentage (0 - 100)
     * @param function              Runnable function to be executed when evolution is interrupted
     */
    final void runEvolve(final int crossoverType, final int mutationPercent, final int optimizePercent, final Runnable function) {
        int mutationAmount = (int) Math.round(chromosomeSize_ * mutationPercent / 100.0);
        int optimizeAmount = (int) Math.round(chromosomeSize_ * optimizePercent / 100.0);

        if (isPopulated_ && !this.isRunning()) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                isRunningEvolution_ = true;
                this.evolution(crossoverType, mutationAmount, optimizeAmount, function);
            });
            executorService.shutdown();
        }
    }

    /**
     * Method call to interrupt and stop the genetic algorithm.
     */
    final void stop() {
        stopProcess_ = true;
    }

    /**
     * Get population array
     *
     * @return      Population array
     */
    final int[][] getPopulation() {
        return population_;
    }

    /**
     * Get fitness array
     *
     * @return      Fitness array
     */
    final int[] getFitness() {
        return fitness_;
    }

    /**
     * Get lower bound gene value
     *
     * @return      Lower bound gene value
     */
    final int getMinGeneValue() {
        return minGeneValue_;
    }

    /**
     * Get gene size
     *
     * @return      Gene size
     */
    final int getGeneSize() {
        return geneSize_;
    }

    /**
     * Get genes per genome size
     *
     * @return      Genes per genome size
     */
    final int getChromosomeSize() {
        return chromosomeSize_;
    }

    /**
     * Get population size
     *
     * @return      Population size
     */
    final int getPopulationSize() {
        return populationSize_;
    }

    /**
     * Get stop process
     *
     * @return      Stop process
     */
    final boolean getStopProcess() {
        return stopProcess_;
    }

    /**
     * Abstract fitnessFunction method.
     * Lower value represents better fitness.
     *
     * @param chromosome        Chromosome
     * @param minGeneValue      Lower bound of gene value
     * @param geneSize          Size of gene
     * @param chromosomeSize    Size of chromosome
     * @return                  Fitness of chromosome
     */
    abstract int fitnessFunction(final int[] chromosome, final int minGeneValue, final int geneSize, final int chromosomeSize);

    /**
     * Method that is called every time a new genome replaces an old genome.
     * This method is also called during initial populating process with "oldGenome"
     * equalling "null".  Intended to be "@Override".
     *
     * @param oldChromosome         Chromosome being replaced
     * @param oldChromosomeFitness  The fitnessFunction of chromosome being replaced
     * @param newChromosome         New chromosome
     * @param newChromosomeFitness  The fitnessFunction of the new chromosome
     */
    void callNewChromosome(final int[] oldChromosome, final int oldChromosomeFitness, final int[] newChromosome,
                           final int newChromosomeFitness) {}

    /**
     * Fills the "population_" array with random numbers.
     *
     * @param population        "population" array to be filled with random numbers
     * @param fitness           "fitness" array
     * @param minGeneValue      Lower bound of gene value
     * @param geneSize          Size of gene
     * @param chromosomeSize    Size of chromosome
     * @param populationSize    Size of population
     * @param firstIndex        The beginning index of the "population" array to be filled
     * @param function          Boolean Consumer function to be executed when array is populated or interrupted
     */
    private void populateArray(final int[][] population, final int[] fitness,
                               final int minGeneValue, final int geneSize, final int chromosomeSize, final int populationSize,
                               final int firstIndex, final Consumer<Boolean> function) {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        ExecutorService populateExecutorService = Executors.newFixedThreadPool(CORES);
        for (int i = 0; i < CORES; i++) {
            Random rand = new Random(System.currentTimeMillis() * i);
            final int iFinal = i;
            final int maxGeneValue = minGeneValue + geneSize;
            populateExecutorService.execute(() -> {
                if (useCallNewChromosome_) {
                    for (int n = iFinal + firstIndex; n < populationSize && !stopProcess_; n = n + CORES) {
                        population[n] = rand.ints(chromosomeSize, minGeneValue, maxGeneValue).toArray();
                        fitness[n] = fitnessFunction(population[n], minGeneValue, geneSize, chromosomeSize);
                        this.callNewChromosome(null, NO_FITNESS_VALUE, population[n], NO_FITNESS_VALUE);
                    }
                } else {
                    for (int n = iFinal + firstIndex; n < populationSize && !stopProcess_; n = n + CORES) {
                        population[n] = rand.ints(chromosomeSize, minGeneValue, maxGeneValue).toArray();
                        fitness[n] = fitnessFunction(population[n], minGeneValue, geneSize, chromosomeSize);
                    }
                }

                int count = atomicInteger.incrementAndGet();
                if (count == CORES) {
                    if (!stopProcess_) {
                        isPopulated_ = true;
                    }
                    boolean completed = !stopProcess_;
                    stopProcess_ = false;

                    if (completed) {
                        population_ = population;
                        fitness_ = fitness;
                        minGeneValue_ = minGeneValue;
                        geneSize_ = geneSize;
                        chromosomeSize_ = chromosomeSize;
                        populationSize_ = populationSize;
                    }

                    populateExecutorService.shutdown();
                    isRunningPopulate_ = false;
                    stopProcess_ = false;
                    if (function != null) {
                        Platform.runLater(() -> function.accept(completed));

                    }
                }
            });
        }
    }

    /**
     * Evolution
     *
     * @param crossoverType         Number of crossover or "0" for uniform crossover
     * @param mutationAmount        Number of mutations
     * @param optimizeAmount        Number of optimizations
     * @param function              Runnable function to be executed when evolution is interrupted
     */
    private void evolution(final int crossoverType, final int mutationAmount, final int optimizeAmount, final Runnable function) {
        final AtomicInteger threadCount = new AtomicInteger(0);
        final AtomicBoolean[] populationLock = new AtomicBoolean[populationSize_];
        for (int i = 0; i < populationSize_; i++) {
            populationLock[i] = new AtomicBoolean(false);
        }

        ExecutorService evolutionExecutorService = Executors.newFixedThreadPool(CORES);
        for (int i = 0; i < CORES; i++) {
            CrossoverMethod crossoverMethod;
            switch(crossoverType) {
                case 0:crossoverMethod = this::uniformCrossover;
                    break;
                case 1: crossoverMethod = this::singlePointCrossover;
                    break;
                case 2: crossoverMethod = this::twoPointCrossover;
                    break;
                default: crossoverMethod = this::multiPointCrossover;
                    break;
            }

            final int minGeneValueFinal = minGeneValue_;
            final int geneSizeFinal = geneSize_;
            final int chromosomeSizeFinal = chromosomeSize_;
            final int populationSizeFinal = populationSize_;
            final int indexCoreFinal = i;

            evolutionExecutorService.execute(() -> this.propagate(
                    minGeneValueFinal, geneSizeFinal, chromosomeSizeFinal, populationSizeFinal,
                    crossoverType, mutationAmount, optimizeAmount, crossoverMethod,
                indexCoreFinal, threadCount, populationLock, evolutionExecutorService, function));
        }
    }

    /**
     *
     * @param minGeneValue              Lower bound of gene value
     * @param geneSize                  Size of gene
     * @param chromosomeSize            Size of chromosome
     * @param populationSize            Size of population
     * @param crossoverType             Number of point crossovers
     * @param mutationAmount            Amount of gene mutations
     * @param optimizeAmount            Amount of gene optimizations
     * @param crossoverMethod           CrossoverMethod lambda
     * @param indexCore                 Index core number used for random number generator
     * @param threadCount               Atomic integer used to determine last thread running
     * @param populationLock            Locks to prevent concurrent access to chromosomes
     * @param evolutionExecutorService  ExecutorService to allow final thread to shutdown service
     * @param function                  Runnable function to be executed when evolution is interrupted
     */
    private void propagate (final int minGeneValue, final int geneSize, final int chromosomeSize, final int populationSize,
                            final int crossoverType, final int mutationAmount, final int optimizeAmount,
                            final CrossoverMethod crossoverMethod, final int indexCore, final AtomicInteger threadCount,
                            final AtomicBoolean[] populationLock, final ExecutorService evolutionExecutorService,
                            final Runnable function) {
        final Boolean[] uniformBooleanArray = this.uniformBooleanArrayFill(chromosomeSize);
        int nextIndex = mutationAmount;
        int amount = mutationAmount + optimizeAmount;
        Random rand = new Random(System.currentTimeMillis() * indexCore);

        MutateOptimizeMethod mutateOptimizeMethod;
        MutateOptimizeMethod noneMethod = (int[] child, final int[] randomFillArrayFinal, final int minGeneValueFinal,
                                           final int geneSizeFinal, final int chromosomeSizeFinal,
                                           final int mutationAmountFinal, final int optimizeAmountFinal,
                                           final int nextIndexFinal) -> child;

        MutateOptimizeMethod mutateMethod = (int[] child, final int[] randomFillArrayFinal, final int minGeneValueFinal,
                                             final int geneSizeFinal, final int chromosomeSizeFinal,
                                             final int mutationAmountFinal, final int optimizeAmountFinal,
                                             final int nextIndexFinal) ->
                this.mutate(child, randomFillArrayFinal, nextIndexFinal, minGeneValueFinal, geneSizeFinal, mutationAmountFinal);

        MutateOptimizeMethod optimizeMethod = (int[] child, final int[] randomFillArrayFinal, final int minGeneValueFinal,
                                               final int geneSizeFinal, final int chromosomeSizeFinal,
                                               final int mutationAmountFinal, final int optimizeAmountFinal,
                                               final int nextIndexFinal) ->
                this.optimize(child, randomFillArrayFinal, nextIndexFinal, minGeneValueFinal, geneSizeFinal,
                        chromosomeSizeFinal);

        MutateOptimizeMethod bothMethod = (int[] child, final int[] randomFillArrayFinal, final int minGeneValueFinal,
                                           final int geneSizeFinal, final int chromosomeSizeFinal,
                                           final int mutationAmountFinal, final int optimizeAmountFinal,
                                           final int nextIndexFinal) -> {
            child = this.mutate(child, randomFillArrayFinal, nextIndexFinal, minGeneValueFinal, geneSizeFinal, mutationAmountFinal);
            return this.optimize(child, randomFillArrayFinal, nextIndexFinal, minGeneValueFinal, geneSizeFinal,
                    chromosomeSizeFinal);
        };

        if (mutationAmount == 0 && optimizeAmount == 0) {
            mutateOptimizeMethod = noneMethod;
        } else if (optimizeAmount == 0) {
            mutateOptimizeMethod = mutateMethod;
        } else if (mutationAmount == 0) {
            mutateOptimizeMethod = optimizeMethod;
        } else {
            mutateOptimizeMethod = bothMethod;
        }

        if (useCallNewChromosome_) {
            while (!stopProcess_) {
                int populationRandomOne = rand.nextInt(populationSize);
                while (!populationLock[populationRandomOne].compareAndSet(false, true)) {
                    populationRandomOne = rand.nextInt(populationSize);
                }
                int populationRandomTwo = rand.nextInt(populationSize);

                while (!populationLock[populationRandomTwo].compareAndSet(false, true)) {
                    populationRandomTwo = rand.nextInt(populationSize);
                }

                int[] randomFillArray = rand.ints(0, chromosomeSize).distinct().limit(amount).toArray();

                int childOne[] = crossoverMethod.apply(population_[populationRandomOne], population_[populationRandomTwo],
                        chromosomeSize, crossoverType, uniformBooleanArray);
                childOne = mutateOptimizeMethod.get(childOne, randomFillArray, minGeneValue, geneSize, chromosomeSize,
                        mutationAmount, optimizeAmount, nextIndex);
                int childOneFitness = fitnessFunction(childOne, minGeneValue, geneSize, chromosomeSize);

                int childTwo[] = crossoverMethod.apply(population_[populationRandomOne], population_[populationRandomTwo],
                        chromosomeSize, crossoverType, uniformBooleanArray);
                childTwo = mutateOptimizeMethod.get(childTwo, randomFillArray, minGeneValue, geneSize, chromosomeSize,
                        mutationAmount, optimizeAmount, nextIndex);
                int childTwoFitness = fitnessFunction(childTwo, minGeneValue, geneSize, chromosomeSize);

                if (childOneFitness > childTwoFitness) {
                    int lowGenome[] = childTwo;
                    int lowFitness = childTwoFitness;
                    childTwo = childOne;
                    childTwoFitness = childOneFitness;
                    childOne = lowGenome;
                    childOneFitness = lowFitness;
                }

                int oldGenomeOneFitness = fitness_[populationRandomOne];
                int oldGenomeTwoFitness = fitness_[populationRandomTwo];

                final int populationRandomOneFinal = populationRandomOne;
                final int[] childOneFinal = childOne;
                final int childOneFitnessFinal = childOneFitness;
                final int populationRandomTwoFinal = populationRandomTwo;
                final int[] childTwoFinal = childTwo;
                final int childTwoFitnessFinal = childTwoFitness;

                if (childOneFitness < oldGenomeOneFitness && childTwoFitness < oldGenomeTwoFitness) {
                    Platform.runLater(() -> this.callNewChromosome(population_[populationRandomOneFinal], oldGenomeOneFitness,
                            childOneFinal, childOneFitnessFinal));
                    Platform.runLater(() -> this.callNewChromosome(population_[populationRandomTwoFinal], oldGenomeTwoFitness,
                            childTwoFinal, childTwoFitnessFinal));

                    population_[populationRandomOne] = childOne;
                    population_[populationRandomTwo] = childTwo;
                    fitness_[populationRandomOne] = childOneFitness;
                    fitness_[populationRandomTwo] = childTwoFitness;
                } else if (childOneFitness < oldGenomeTwoFitness && childTwoFitness < oldGenomeOneFitness) {
                    Platform.runLater(() -> this.callNewChromosome(population_[populationRandomOneFinal], oldGenomeTwoFitness,
                            childTwoFinal, childTwoFitnessFinal));
                    Platform.runLater(() -> this.callNewChromosome(population_[populationRandomTwoFinal], oldGenomeOneFitness,
                            childOneFinal, childOneFitnessFinal));

                    population_[populationRandomOne] = childTwo;
                    population_[populationRandomTwo] = childOne;
                    fitness_[populationRandomOne] = childTwoFitness;
                    fitness_[populationRandomTwo] = childOneFitness;
                } else if (oldGenomeOneFitness < oldGenomeTwoFitness) {
                    if (childOneFitness < oldGenomeTwoFitness) {
                        Platform.runLater(() -> this.callNewChromosome(population_[populationRandomTwoFinal], oldGenomeOneFitness,
                                childOneFinal, childOneFitnessFinal));
                        population_[populationRandomTwo] = childOne;
                        fitness_[populationRandomTwo] = childOneFitness;
                    }
                } else if (childOneFitness < oldGenomeOneFitness) {
                    Platform.runLater(() -> this.callNewChromosome(population_[populationRandomOneFinal], oldGenomeOneFitness,
                            childOneFinal, childOneFitnessFinal));
                    population_[populationRandomOne] = childOne;
                    fitness_[populationRandomOne] = childOneFitness;
                }
                populationLock[populationRandomOne].set(false);
                populationLock[populationRandomTwo].set(false);
            }
        } else {
            while (!stopProcess_) {
                int populationRandomOne = rand.nextInt(populationSize);
                while (!populationLock[populationRandomOne].compareAndSet(false, true)) {
                    populationRandomOne = rand.nextInt(populationSize);
                }

                int populationRandomTwo = rand.nextInt(populationSize);

                while (!populationLock[populationRandomTwo].compareAndSet(false, true)) {
                    populationRandomTwo = rand.nextInt(populationSize);
                }

                int[] randomFillArray = rand.ints(0, chromosomeSize).distinct().limit(amount).toArray();

                int childOne[] = crossoverMethod.apply(population_[populationRandomOne], population_[populationRandomTwo],
                        chromosomeSize, crossoverType, uniformBooleanArray);
                childOne = mutateOptimizeMethod.get(childOne, randomFillArray, minGeneValue, geneSize, chromosomeSize,
                        mutationAmount, optimizeAmount, nextIndex);
                int childOneFitness = fitnessFunction(childOne, minGeneValue, geneSize, chromosomeSize);

                int childTwo[] = crossoverMethod.apply(population_[populationRandomOne], population_[populationRandomTwo],
                        chromosomeSize, crossoverType, uniformBooleanArray);
                childTwo = mutateOptimizeMethod.get(childTwo, randomFillArray, minGeneValue, geneSize, chromosomeSize,
                        mutationAmount, optimizeAmount, nextIndex);
                int childTwoFitness = fitnessFunction(childTwo, minGeneValue, geneSize, chromosomeSize);

                if (childOneFitness > childTwoFitness) {
                    int lowGenome[] = childTwo;
                    int lowFitness = childTwoFitness;
                    childTwo = childOne;
                    childTwoFitness = childOneFitness;
                    childOne = lowGenome;
                    childOneFitness = lowFitness;
                }

                int oldGenomeOneFitness = fitness_[populationRandomOne];
                int oldGenomeTwoFitness = fitness_[populationRandomTwo];

                if (childOneFitness < oldGenomeOneFitness && childTwoFitness < oldGenomeTwoFitness) {
                    population_[populationRandomOne] = childOne;
                    population_[populationRandomTwo] = childTwo;
                    fitness_[populationRandomOne] = childOneFitness;
                    fitness_[populationRandomTwo] = childTwoFitness;
                } else if (childOneFitness < oldGenomeTwoFitness && childTwoFitness < oldGenomeOneFitness) {
                    population_[populationRandomOne] = childTwo;
                    population_[populationRandomTwo] = childOne;
                    fitness_[populationRandomOne] = childTwoFitness;
                    fitness_[populationRandomTwo] = childOneFitness;
                } else if (oldGenomeOneFitness < oldGenomeTwoFitness) {
                    if (childOneFitness < oldGenomeTwoFitness) {
                        population_[populationRandomTwo] = childOne;
                        fitness_[populationRandomTwo] = childOneFitness;
                    }
                } else if (childOneFitness < oldGenomeOneFitness) {
                    population_[populationRandomOne] = childOne;
                    fitness_[populationRandomOne] = childOneFitness;
                }
                populationLock[populationRandomOne].set(false);
                populationLock[populationRandomTwo].set(false);
            }
        }

        int count = threadCount.incrementAndGet();
        if (count == CORES) {
            evolutionExecutorService.shutdown();
            isRunningEvolution_ = false;
            stopProcess_ = false;
            if (function != null) {
                Platform.runLater(function);
            }
        }
    }

    /**
     * Fills "uniformBooleanArray_" array with half "true" and half "false".
     * This is used for uniform (0.50) crossover.
     *
     * @param chromosomeSize    Size of chromosome
     * @return                  Boolean array for uniform crossover
     */
    private Boolean[] uniformBooleanArrayFill(final int chromosomeSize) {
        Boolean[] uniformBooleanArray = new Boolean[chromosomeSize];
        Arrays.fill(uniformBooleanArray, 0, chromosomeSize / 2, true);
        Arrays.fill(uniformBooleanArray, chromosomeSize / 2, chromosomeSize, false);
        return uniformBooleanArray;
    }

    /**
     * Uniform crossover at 0.50.
     *
     * @param parentA               Parent "A" chromosome
     * @param parentB               Parent "B" chromosome
     * @param chromosomeSize        Size of chromosome
     * @param crossoverType         Crossover type (not used here)
     * @param uniformBooleanArray   uniformBooleanArray
     * @return                      child chromosome
     */
    private int[] uniformCrossover(final int[] parentA, final int[] parentB, final int chromosomeSize,
                                   final int crossoverType, final Boolean[] uniformBooleanArray) {
        int[] child = new int[chromosomeSize];
        Collections.shuffle(Arrays.asList(uniformBooleanArray));

        for (int i = 0; i < chromosomeSize; i++) {
            if (uniformBooleanArray[i]) {
                child[i] = parentA[i];
            } else {
                child[i] = parentB[i];
            }
        }

        return child;
    }

    /**
     * Single-point crossover at a random location.
     *
     * @param parentA               Parent "A" chromosome
     * @param parentB               Parent "B" chromosome
     * @param chromosomeSize        Size of chromosome
     * @param crossoverType         Crossover type (not used here)
     * @param uniformBooleanArray   uniformBooleanArray (not used here)
     * @return                      child chromosome
     */
    private int[] singlePointCrossover(final int[] parentA, final int[] parentB, final int chromosomeSize,
                                       final int crossoverType, final Boolean[] uniformBooleanArray) {
        int[] child = new int[chromosomeSize];
        int crossoverPoint = new Random().nextInt(chromosomeSize - 1) + 1;

        System.arraycopy(parentA, 0, child, 0, crossoverPoint);
        System.arraycopy(parentB, crossoverPoint, child, crossoverPoint, chromosomeSize - crossoverPoint);

        return child;
    }

    /**
     * Two-point crossover at random locations.
     *
     * @param parentA               Parent "A" chromosome
     * @param parentB               Parent "B" chromosome
     * @param chromosomeSize        Size of chromosome
     * @param crossoverType         Crossover type (not used here)
     * @param uniformBooleanArray   uniformBooleanArray (not used here)
     * @return                      child chromosome
     */
    private int[] twoPointCrossover(final int[] parentA, final int[] parentB, final int chromosomeSize,
                                    final int crossoverType, final Boolean[] uniformBooleanArray) {
        int[] child = new int[chromosomeSize];
        int[] nPointArray = new Random().ints(1, chromosomeSize).distinct().limit(2).sorted().toArray();

        System.arraycopy(parentA, 0, child, 0, nPointArray[0]);
        System.arraycopy(parentB, nPointArray[0], child, nPointArray[0], nPointArray[1] - nPointArray[0]);
        System.arraycopy(parentA, nPointArray[1], child, nPointArray[1], chromosomeSize - nPointArray[1]);

        return child;
    }

    /**
     * Multi-point crossover at random locations.
     * This method assumes this is never called when "crossoverType_" is "0".
     * For single or two-point crossovers, use the singlePointCrossover or
     *  twoPointCrossover methods for better performance.
     *
     * @param parentA               Parent "A" chromosome
     * @param parentB               Parent "B" chromosome
     * @param chromosomeSize            Size of chromosome
     * @param crossoverType         Crossover type (not used here)
     * @param uniformBooleanArray   uniformBooleanArray
     * @return                      child chromosome
     */
    private int[] multiPointCrossover(final int[] parentA, final int[] parentB, final int chromosomeSize,
                                      final int crossoverType, final Boolean[] uniformBooleanArray) {
        int[] child = new int[chromosomeSize];
        int[] nPointArray = new Random().ints(1, chromosomeSize).distinct().limit(crossoverType).sorted().toArray();

        System.arraycopy(parentA, 0, child, 0, nPointArray[0]);

        for (int i = 0; i < crossoverType - 2; i = i + 2) {
            System.arraycopy(parentB, nPointArray[i], child, nPointArray[i], nPointArray[i + 1] - nPointArray[i]);
            System.arraycopy(parentA, nPointArray[i + 1], child, nPointArray[i + 1], nPointArray[i + 2] - nPointArray[i + 1]);
        }

        if (crossoverType % 2 == 1) {
            System.arraycopy(parentB, nPointArray[crossoverType - 1], child, nPointArray[crossoverType - 1], chromosomeSize - nPointArray[crossoverType - 1]);
        } else {
            System.arraycopy(parentB, nPointArray[crossoverType - 2], child, nPointArray[crossoverType - 2], nPointArray[crossoverType - 1] - nPointArray[crossoverType - 2]);
            System.arraycopy(parentA, nPointArray[crossoverType - 1], child, nPointArray[crossoverType - 1], chromosomeSize - nPointArray[crossoverType - 1]);
        }

        return child;
    }

    /**
     * Random mutations of the genome
     *
     * @param chromosome            Chromosome
     * @param randomFillArray       Array of random genome index locations
     * @param nextIndex             Location of start of randomArray for optimize method
     * @param minGeneValue          Lower bound value of gene
     * @param geneSize              Gene size
     * @param mutationAmount        Number of gene mutations based on selected percentage
     * @return                      Chromosome after mutation
     */
    private int[] mutate(final int[] chromosome, final int[] randomFillArray, final int nextIndex, final int minGeneValue,
                         final int geneSize, final int mutationAmount) {
        int[] randomGeneArray = new Random().ints(minGeneValue, minGeneValue + geneSize).limit(mutationAmount).toArray();
        for (int i = 0; i < nextIndex; i++) {
            chromosome[randomFillArray[i]] = randomGeneArray[i];
        }
        return chromosome;
    }

    /**
     * Random mutations of the chromosome
     *
     * @param chromosome            Chromosome
     * @param randomArray           Array of random chromosome index locations
     * @param nextIndex             Location of start of randomArray for optimize method
     * @param minGeneValue          Lower bound value of gene
     * @param geneSize              Gene size
     * @param chromosomeSize        Chromosome size
     * @return                      Chromosome after optimization
     */
    private int[] optimize(final int[] chromosome, final int[] randomArray, final int nextIndex, final int minGeneValue,
                           final int geneSize, final int chromosomeSize) {
        for (int i = nextIndex; i < randomArray.length; i++) {
            int minFitness = Integer.MAX_VALUE;
            int minValue = -1;
            for (int j = minGeneValue; j < geneSize; j++) {
                chromosome[randomArray[i]] = j;
                int fitness = this.fitnessFunction(chromosome, minGeneValue, geneSize, chromosomeSize);
                if (fitness < minFitness) {
                    minFitness = fitness;
                    minValue = j;
                }
            }
            chromosome[randomArray[i]] = minValue;
        }
        return chromosome;
    }
}