package CPPGAi;

/**
 * DefaultConstants
 *
 * Place the default constants for your
 *  genetic algorithm here.
 */
final class DefaultConstants {
    static final String TITLE = "N-Queens";

    static final int MIN_GENE_VALUE = 0;

    static final int DEFAULT_GENE_SIZE = 20;
    static final int MIN_GENE_SIZE = 4;
    static final int MAX_GENE_SIZE = 10000;

    static final int DEFAULT_CHROMOSOME_SIZE = 20;
    static final int MIN_CHROMOSOME_SIZE = 4;
    static final int MAX_CHROMOSOME_SIZE = 10000;

    static final int DEFAULT_POPULATION_SIZE = 10000;
    static final int MIN_POPULATION_SIZE = 10;
    static final int MAX_POPULATION_SIZE = 1000000;

    static final int DEFAULT_CROSSOVER_TYPE = 1;
    static final int MAX_CUSTOM_CROSSOVER_PERCENTAGE = 50;

    static final int DEFAULT_MUTATION = 30;
    static final int MIN_MUTATION = 0;
    static final int MAX_MUTATION = 50;

    static final int DEFAULT_OPTIMIZATION = 30;
    static final int MIN_OPTIMIZATION = 0;
    static final int MAX_OPTIMIZATION = 50;

    static final int DEFAULT_DISPLAY_LIVE_POPULATION = 2;
    static final int MIN_DISPLAY_LIVE_POPULATION = 0;
    static final int MAX_DISPLAY_LIVE_POPULATION = 10;

    static final int DEFAULT_DISPLAY_PRINT_POPULATION = 3;
    static final int MIN_DISPLAY_PRINT_POPULATION = 0;
    static final int MAX_DISPLAY_PRINT_POPULATION = Integer.MAX_VALUE;

    /**
     * File constants
     */
    static final String DEFAULT_FOLDER =
            "/Documents/CPPGAi/";
    static final String DEFAULT_FILE =
            "MyPopulation";
    static final String DEFAULT_EXTENSION =
            "cppgai";
    static final String DEFAULT_EXTENSION_FILTER[] = {
            "CPPGAi Files",
            "*.cppgai" };
}
