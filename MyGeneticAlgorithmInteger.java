package CPPGAi;

import javafx.scene.control.TextArea;

import java.util.Arrays;

/**
 * MyGeneticAlgorithmInteger
 *
 */
class MyGeneticAlgorithmInteger extends GeneticAlgorithmInteger {

    private TextArea listResults_;
    private int displayFitness_;

    MyGeneticAlgorithmInteger() {
        displayFitness_ = DefaultConstants.DEFAULT_DISPLAY_LIVE_POPULATION;
    }

    /**
     * Retrieve TextArea pane from GUI
     *
     * @param textAreaResults   TextArea to display the results
     */
    void addFields(final TextArea textAreaResults) {
        listResults_ = textAreaResults;
    }

    /**
     * Retrieve fitness threshold value
     *
     * @param displayFitness    Display fitness threshold
     */
    void setDisplayFitness(final int displayFitness) {
        displayFitness_ = displayFitness;
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
    @Override
    int fitnessFunction(final int[] chromosome, final int minGeneValue, final int geneSize, final int chromosomeSize) {
        // Enter fitness function here
        int fitnessValue = 0;
        int nextStep;
        for (int j = 0; j < chromosomeSize - 1; j++) {
            for (int i = j + 1; i < chromosomeSize; i++) {
                nextStep = i - j;
                if (chromosome[j] == chromosome[i]) {
                    fitnessValue++;
                }
                if (chromosome[j] == chromosome[i] + nextStep) {
                    fitnessValue++;
                }
                if (chromosome[j] == chromosome[i] - nextStep) {
                    fitnessValue++;
                }
            }
        }
        return fitnessValue;
    }

    /**
     * Method that is called every time a new chromosome replaces an old chromosome.
     * This method is also called during initial populating process with
     * "oldChromosome" equalling "null";
     *
     * @param oldChromosome             Chromosome being replaced
     * @param oldChromosomeFitness      The fitnessFunction of chromosome being replaced
     * @param newChromosome             New chromosome
     * @param newChromosomeFitness      The fitnessFunction of the new chromosome
     */
    @Override
    void callNewChromosome(int[] oldChromosome, int oldChromosomeFitness, int[] newChromosome, int newChromosomeFitness) {
        // This displays the results in the GUI
        if (newChromosomeFitness >= 0 && newChromosomeFitness <= displayFitness_) {
            listResults_.appendText("(Fitness: " + String.format("%2d", newChromosomeFitness) + " ) " + Arrays.toString(newChromosome) + "\n");
        }
    }
}
