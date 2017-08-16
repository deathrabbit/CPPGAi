package CPPGAi;

import javafx.scene.paint.Color;

/**
 * LayoutConstants
 *
 * Constants for the GUI only.
 *
 * @author Scott Matsumura
 * @version 1.0
 */
final class LayoutConstants {

    /**
     * GUI Integer constants
     */
    static final int WINDOW_SIZE[] = { 1000, 700 };
    static final int DEFAULT_CUSTOM_POINT_CROSSOVER = 3;
    static final int MIN_CUSTOM_POINT_CROSSOVER = 3;

    /**
     * GUI String constants
     */
    static final String HEADER_TAB[] = {
            "Home",
            "Population",
            "Crossover",
            "Results" };
    static final String POPULATE_TOGGLEBUTTON[] = {
            "Begin Populate",
            "STOP Populating",
            "Populated" };
    static final String EVOLUTION_TOGGLEBUTTON[] = {
            "Begin Evolution",
            "STOP Evolution" };
    static final String TIME_LABEL =
            "Timer (h : m : s)";
    static final String FILENAME_LABEL =
            "File name:";
    static final String FILENAME_SAVE =
            "Saving: ";
    static final String FILENAME_OPEN =
            "Opening: ";
    static final String FILENAME_NONE =
            "No File Selected";
    static final String FILENAME_OPEN_ERROR_MESSAGE =
            "Not a \"" + DefaultConstants.DEFAULT_EXTENSION + "\" file.";
    static final String FILENAME_SAVE_ERROR_MESSAGE[] = {
            "Cannot save \"",
            "\" file." };
    static final String SAVE_BUTTON[] = {
            "Save",
            "Saving..." };
    static final String SAVEAS_BUTTON[] = {
            "Save as",
            "Saving..." };
    static final String NEW_BUTTON[] = {
            "New",
            "Loading..." };
    static final String OPEN_BUTTON[] = {
            "Open File",
            "Opening..." };
    static final String GENE_SIZE_LABEL[] = {
            "Gene size",
            "(" + DefaultConstants.MIN_GENE_SIZE + " - " + DefaultConstants.MAX_GENE_SIZE + ")" };
    static final String CHROMOSOME_SIZE_LABEL[] = {
            "Chromosome Size",
            "(" + DefaultConstants.MIN_CHROMOSOME_SIZE + " - " + DefaultConstants.MAX_CHROMOSOME_SIZE + ")" };
    static final String POPULATION_SIZE_LABEL[] = {
            "Population Size",
            "(" + DefaultConstants.MIN_POPULATION_SIZE + " - " + DefaultConstants.MAX_POPULATION_SIZE + ")" };
    static final String LOCK_TOGGLEBUTTON[] = {
            "Lock",
            "Unlock" };
    static final String RESIZE_TOGGLEBUTTON[] = {
            "Apply Changes",
            "Working..." };
    static final String REVERT_BUTTON =
            "Revert to Previous";
    static final String RADIOBUTTON_CROSSOVER_LABELS[] = {
            "Crossover Type",
            "Uniform (0.5)",
            "Single-point",
            "Two-point",
            "Custom-point" };
    static final String CUSTOM_POINT_CROSSOVER_LABEL =
            "Custom-point Crossover";
    static final String[] CUSTOM_POINT_CROSSOVER_RANGE = {
            "(" + MIN_CUSTOM_POINT_CROSSOVER + " - to half of",
            "Chromosomes size)" };
    static final String MUTATION_LABEL[] = {
            "Mutation Level",
            "(Percentage " + DefaultConstants.MIN_MUTATION + " - " + DefaultConstants.MAX_MUTATION + ")",
            "(0 = none, 50 = half)" };
    static final String OPTIMIZATION_LABEL[] = {
            "Optimization Level",
            "(Percentage " + DefaultConstants.MIN_OPTIMIZATION + " - " + DefaultConstants.MAX_OPTIMIZATION + ")",
            "(0 = none, 50 = half)" };
    static final String LIVE_POPULATION_CHECKBOX =
            "Live Results";
    static final String LIVE_POPULATION_LABEL[] = {
            "Fitness threshold",
            "(" + DefaultConstants.MIN_DISPLAY_LIVE_POPULATION + " - " + DefaultConstants.MAX_DISPLAY_LIVE_POPULATION + ")" };
    static final String PRINT_POPULATION_LABEL[] = {
            "Fitness threshold",
            "(" + DefaultConstants.MIN_DISPLAY_PRINT_POPULATION + " - " + DefaultConstants.MAX_DISPLAY_PRINT_POPULATION + ")" };
    static final String PRINT_POPULATION_TOGGLE_BUTTON[] = {
            "Get Results",
            "Working..." };
    static final String STANDARD_DEVIATION_LABELS =
            "Standard Deviations";
    static final String STANDARD_DEVIATION_BUTTON[] = {
            "Get Standard Deviation",
            "Working..." };
    static final String BODY_TAB[] = {
            "Live Results",
            "Results",
            "Help" };

    /**
     * Style constants
     */
    static final String NOT_EDITABLE_COLOR_BACKGROUND =
            "-fx-background-color: WhiteSmoke; -fx-border-color: Black;";
    static final String ENABLE_COLOR_BACKGROUND =
            null;
    static final String DISABLE_COLOR_BACKGROUND =
            "-fx-text-fill: Grey; -fx-background-color: LightGrey;";
    static final Color ENABLE_TEXT_COLOR =
            Color.BLACK;
    static final Color DISABLE_TEXT_COLOR =
            Color.LIGHTGRAY;
    static final String TEXTFIELD_TEXT_COLOR_RED =
            "-fx-text-inner-color: Red;";
    static final String TEXTFIELD_TEXT_COLOR_BLACK =
            "-fx-text-inner-color: Black;";

    /**
     * Alert Dialog
     */
    static final String TASK_ALERT =
            "Task still running...";
    static final String TASK_ALERT_BUTTON_RETURN =
            "Return";
    static final String TASK_ALERT_BUTTON_CLOSE =
            "Close";
    static final String SAVE_ALERT =
            "Do you want to save population?";
    static final String SAVE_ALERT_BUTTON_SAVEAS =
            "Save As";
    static final String SAVE_ALERT_BUTTON_CLOSE =
            "Close";
}