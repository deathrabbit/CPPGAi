package CPPGAi;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

/**
 * Controller
 *
 * JavaFX Controller class.
 *
 * @author Scott Matsumura
 * @version 1.0
 */
public class Controller implements Serializable {

    @FXML
    private Tab tabHeaderHome_;

    @FXML
    private Tab tabHeaderPopulation_;

    @FXML
    private Tab tabHeaderCrossover_;

    @FXML
    private Tab tabHeaderResults_;

    @FXML
    private ToggleButton toggleButtonPopulate_;

    @FXML
    private ToggleButton toggleButtonEvolution_;

    @FXML
    private Label labelTimer_;

    @FXML
    private TextField textFieldTimer_;

    @FXML
    private Label labelFileName_;

    @FXML
    private TextField textFieldFile_;

    @FXML
    private Button buttonSaveFile_;

    @FXML
    private Button buttonSaveAsFile_;

    @FXML
    private Button buttonNewFile_;

    @FXML
    private Button buttonOpenFile_;

    @FXML
    private Label labelGeneSize_;

    @FXML
    private Label labelRangeGeneSize_;

    @FXML
    private TextFieldRestrict textFieldGeneSize_;

    @FXML
    private Label labelChromosomeSize_;

    @FXML
    private Label labelRangeChromosomeSize_;

    @FXML
    private TextFieldRestrict textFieldChromosomeSize_;

    @FXML
    private Label labelPopulationSize_;

    @FXML
    private Label labelRangePopulationSize_;

    @FXML
    private TextFieldRestrict textFieldPopulationSize_;

    @FXML
    private ToggleButton toggleButtonPopulationLock_;

    @FXML
    private ToggleButton toggleButtonPopulationResize_;

    @FXML
    private Button buttonPopulationRevert_;

    @FXML
    private Label labelMutation_;

    @FXML
    private Label labelRangeMutation_;

    @FXML
    private Label labelHelpMutation_;

    @FXML
    private TextFieldRestrict textFieldMutation_;

    @FXML
    private Label labelCrossoverType_;

    @FXML
    private RadioButton radioButtonCrossoverUniform_;

    @FXML
    private RadioButton radioButtonCrossoverSingle_;

    @FXML
    private RadioButton radioButtonCrossoverTwo_;

    @FXML
    private RadioButton radioButtonCrossoverCustom_;

    @FXML
    private Label labelCustomCrossover_;

    @FXML
    private Label labelCustomCrossoverRange1_;

    @FXML
    private Label labelCustomCrossoverRange2_;

    @FXML
    private TextFieldRestrict textFieldCustomCrossover_;

    @FXML
    private Label labelOptimization_;

    @FXML
    private Label labelRangeOptimization_;

    @FXML
    private Label labelHelpOptimization_;

    @FXML
    private TextFieldRestrict textFieldOptimization_;

    @FXML
    private CheckBox checkBoxLiveGetPopulation_;

    @FXML
    private Label labelLiveGetPopulation_;

    @FXML
    private Label labelRangeLiveGetPopulation_;

    @FXML
    private TextFieldRestrict textFieldLiveGetPopulation_;

    @FXML
    private ToggleButton toggleButtonPrintPopulation_;

    @FXML
    private Label labelPrintPopulation_;

    @FXML
    private Label labelRangePrintPopulation_;

    @FXML
    private TextFieldRestrict textFieldPrintPopulation_;

    @FXML
    private Label labelStandardDeviation_;

    @FXML
    private ToggleButton toggleButtonStandardDeviation_;

    @FXML
    private TextField textFieldStandardDeviation_;

    @FXML
    private Tab tabBodyLiveResults_;

    @FXML
    private ScrollPane scrollPaneLiveResults_;

    @FXML
    private TextArea textAreaLiveResults_;

    @FXML
    private Tab tabBodyGetResults_;

    @FXML
    private ScrollPane scrollPaneGetResults_;

    @FXML
    private TextArea textAreaGetResults_;

    private final TabPane tabPaneBody_;
    private final ToggleGroup crossoverRadioGroup_;
    private final ControlManager controlManager_;
    private final ScheduledExecutorService scheduledExecutorTimer_;
    private final MyGeneticAlgorithmInteger geneticAlgorithm_;
    private final GeneticAlgorithmTools geneticAlgorithmTools_;

    private Stage stage_;
    private FileChooser fileChooser_;
    private final File DEFAULT_FILE;
    private File file_;

    private ScheduledFuture<?> timerFuture_;
    private int populationSizeStore_;
    private int timerHours_;
    private int timerMinutes_;
    private int timerSeconds_;

    private boolean doneIOFile_;
    private boolean donePrintPopulation_;
    private boolean doneStandardDeviation_;

    public Controller() {
        tabPaneBody_ = new TabPane();
        crossoverRadioGroup_ = new ToggleGroup();
        controlManager_ = new ControlManager();
        scheduledExecutorTimer_ = Executors.newSingleThreadScheduledExecutor();

        geneticAlgorithm_ = new MyGeneticAlgorithmInteger();
        geneticAlgorithmTools_ = new GeneticAlgorithmTools();

        fileChooser_ = new FileChooser();
        DEFAULT_FILE = new File(System.getProperty("user.home") + DefaultConstants.DEFAULT_FOLDER
                + DefaultConstants.DEFAULT_FILE + "." + DefaultConstants.DEFAULT_EXTENSION);
        file_ = DEFAULT_FILE;

        populationSizeStore_ = 0;
        timerHours_ = 0;
        timerMinutes_ = 0;
        timerSeconds_ = 0;
        doneIOFile_ = true;
        donePrintPopulation_ = true;
        doneStandardDeviation_ = true;
    }

    @FXML @SuppressWarnings("Duplicates")
    public void initialize() {
        // Header tabs
        tabHeaderHome_.setText(LayoutConstants.HEADER_TAB[0]);
        tabHeaderPopulation_.setText(LayoutConstants.HEADER_TAB[1]);
        tabHeaderCrossover_.setText(LayoutConstants.HEADER_TAB[2]);
        tabHeaderResults_.setText(LayoutConstants.HEADER_TAB[3]);

        // Populate toggle button
        toggleButtonPopulate_.setText(LayoutConstants.POPULATE_TOGGLEBUTTON[0]);
        controlManager_.insert("PopulateButton", false, (enable) -> toggleButtonPopulate_.setDisable(!enable));

        // Evolution toggle button
        toggleButtonEvolution_.setText(LayoutConstants.EVOLUTION_TOGGLEBUTTON[0]);
        controlManager_.insert("EvolutionButton", false, (enable) -> toggleButtonEvolution_.setDisable(!enable));

        // Timer
        labelTimer_.setText(LayoutConstants.TIME_LABEL);
        textFieldTimer_.setText(this.timerFormat());
        textFieldTimer_.setEditable(false);
        textFieldTimer_.setStyle(LayoutConstants.NOT_EDITABLE_COLOR_BACKGROUND);

        // File name
        labelFileName_.setText(LayoutConstants.FILENAME_LABEL);
        textFieldFile_.setEditable(false);
        textFieldFile_.setStyle(LayoutConstants.NOT_EDITABLE_COLOR_BACKGROUND);

        // Save button
        buttonSaveFile_.setText(LayoutConstants.SAVE_BUTTON[0]);
        controlManager_.insert("SaveButton", false, (enable) -> buttonSaveFile_.setDisable(!enable));

        // Save As button
        buttonSaveAsFile_.setText(LayoutConstants.SAVEAS_BUTTON[0]);
        controlManager_.insert("SaveAsButton", (enable) -> buttonSaveAsFile_.setDisable(!enable));

        // New file button
        buttonNewFile_.setText(LayoutConstants.NEW_BUTTON[0]);
        controlManager_.insert("NewButton", (enable) -> buttonNewFile_.setDisable(!enable));

        // Open file button
        buttonOpenFile_.setText(LayoutConstants.OPEN_BUTTON[0]);
        controlManager_.insert("OpenButton", (enable) -> buttonOpenFile_.setDisable(!enable));

        // Gene size control
        labelGeneSize_.setText(LayoutConstants.GENE_SIZE_LABEL[0]);
        labelRangeGeneSize_.setText(LayoutConstants.GENE_SIZE_LABEL[1]);
        textFieldGeneSize_.setText(Integer.toString(DefaultConstants.DEFAULT_GENE_SIZE));
        textFieldGeneSize_.setMaxValue(() -> {
            controlManager_.control("SaveButton.true.setDefault");
            return DefaultConstants.MAX_GENE_SIZE;
        });
        textFieldGeneSize_.focusedProperty().addListener(new FocusListenerRestrict(textFieldGeneSize_,
                DefaultConstants.MIN_GENE_SIZE, DefaultConstants.DEFAULT_GENE_SIZE));
        controlManager_.insert("GeneSize", (enable) -> {
            if (enable) {
                labelGeneSize_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                labelRangeGeneSize_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                textFieldGeneSize_.setEditable(true);
                textFieldGeneSize_.setStyle(LayoutConstants.ENABLE_COLOR_BACKGROUND);
            } else {
                labelGeneSize_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                labelRangeGeneSize_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                textFieldGeneSize_.setEditable(false);
                textFieldGeneSize_.setStyle(LayoutConstants.DISABLE_COLOR_BACKGROUND);
            }
        });

        // Chromosome size control
        labelChromosomeSize_.setText(LayoutConstants.CHROMOSOME_SIZE_LABEL[0]);
        labelRangeChromosomeSize_.setText(LayoutConstants.CHROMOSOME_SIZE_LABEL[1]);
        textFieldChromosomeSize_.setText(Integer.toString(DefaultConstants.DEFAULT_CHROMOSOME_SIZE));
        textFieldChromosomeSize_.setMaxValue(() -> {
            controlManager_.control("SaveButton.true.setDefault");
            return DefaultConstants.MAX_CHROMOSOME_SIZE;
        });
        textFieldChromosomeSize_.focusedProperty().addListener(new FocusListenerRestrict
                (textFieldChromosomeSize_, DefaultConstants.MIN_CHROMOSOME_SIZE, DefaultConstants.DEFAULT_CHROMOSOME_SIZE));
        controlManager_.insert("ChromosomeSize", (enable) -> {
            if (enable) {
                labelChromosomeSize_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                labelRangeChromosomeSize_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                textFieldChromosomeSize_.setEditable(true);
                textFieldChromosomeSize_.setStyle(LayoutConstants.ENABLE_COLOR_BACKGROUND);
            } else {
                labelChromosomeSize_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                labelRangeChromosomeSize_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                textFieldChromosomeSize_.setEditable(false);
                textFieldChromosomeSize_.setStyle(LayoutConstants.DISABLE_COLOR_BACKGROUND);
            }
        });

        // Population size control
        labelPopulationSize_.setText(LayoutConstants.POPULATION_SIZE_LABEL[0]);
        labelRangePopulationSize_.setText(LayoutConstants.POPULATION_SIZE_LABEL[1]);
        textFieldPopulationSize_.setText(Integer.toString(DefaultConstants.DEFAULT_POPULATION_SIZE));
        textFieldPopulationSize_.setMaxValue(() -> {
            controlManager_.control("SaveButton.true.setDefault");
            return DefaultConstants.MAX_POPULATION_SIZE;
        });
        textFieldPopulationSize_.focusedProperty().addListener(new FocusListenerRestrict
                (textFieldPopulationSize_, DefaultConstants.MIN_POPULATION_SIZE, DefaultConstants.DEFAULT_POPULATION_SIZE,
                        (Integer currentNum) -> {
                    int otherNum = Integer.parseInt(textFieldCustomCrossover_.getText());
                    int percentCurrentNum = (int) Math.round(currentNum * DefaultConstants.MAX_CUSTOM_CROSSOVER_PERCENTAGE / 100.0);
                    if (otherNum > percentCurrentNum) { textFieldCustomCrossover_.setText(Integer.toString(percentCurrentNum)); }
                        }));
        controlManager_.insert("PopulationSize", (enable) -> {
            if (enable) {
                labelPopulationSize_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                labelRangePopulationSize_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                textFieldPopulationSize_.setEditable(true);
                textFieldPopulationSize_.setStyle(LayoutConstants.ENABLE_COLOR_BACKGROUND);
            } else {
                labelPopulationSize_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                labelRangePopulationSize_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                textFieldPopulationSize_.setEditable(false);
                textFieldPopulationSize_.setStyle(LayoutConstants.DISABLE_COLOR_BACKGROUND);
            }
        });

        // Lock population toggle button
        toggleButtonPopulationLock_.setText(LayoutConstants.LOCK_TOGGLEBUTTON[1]);
        controlManager_.insert("LockButton", (enable) -> toggleButtonPopulationLock_.setDisable(!enable));

        // Resize population toggle button
        toggleButtonPopulationResize_.setText(LayoutConstants.RESIZE_TOGGLEBUTTON[0]);
        controlManager_.insert("ResizeButton", (enable) -> toggleButtonPopulationResize_.setDisable(!enable));

        // Revert population button
        buttonPopulationRevert_.setText(LayoutConstants.REVERT_BUTTON);
        controlManager_.insert("RevertButton", (enable) -> buttonPopulationRevert_.setDisable(!enable));

        // Crossover type radio buttons
        labelCrossoverType_.setText(LayoutConstants.RADIOBUTTON_CROSSOVER_LABELS[0]);
        radioButtonCrossoverUniform_.setText(LayoutConstants.RADIOBUTTON_CROSSOVER_LABELS[1]);
        radioButtonCrossoverSingle_.setText(LayoutConstants.RADIOBUTTON_CROSSOVER_LABELS[2]);
        radioButtonCrossoverTwo_.setText(LayoutConstants.RADIOBUTTON_CROSSOVER_LABELS[3]);
        radioButtonCrossoverCustom_.setText(LayoutConstants.RADIOBUTTON_CROSSOVER_LABELS[4]);

        // Crossover radio button toggle group
        radioButtonCrossoverUniform_.setToggleGroup(crossoverRadioGroup_);
        radioButtonCrossoverUniform_.setUserData(0);
        radioButtonCrossoverSingle_.setToggleGroup(crossoverRadioGroup_);
        radioButtonCrossoverSingle_.setUserData(1);
        radioButtonCrossoverSingle_.setSelected(true);
        radioButtonCrossoverTwo_.setToggleGroup(crossoverRadioGroup_);
        radioButtonCrossoverTwo_.setUserData(2);
        radioButtonCrossoverCustom_.setToggleGroup(crossoverRadioGroup_);
        radioButtonCrossoverCustom_.setUserData(3);
        crossoverRadioGroup_.selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> controlManager_.control("SaveButton.true.setDefault"));
        controlManager_.insert("CrossoverButton", (enable) -> {
            if (enable) {
                labelCrossoverType_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                radioButtonCrossoverUniform_.setDisable(false);
                radioButtonCrossoverSingle_.setDisable(false);
                radioButtonCrossoverTwo_.setDisable(false);
                radioButtonCrossoverCustom_.setDisable(false);
                if (radioButtonCrossoverCustom_.isSelected()) {
                    controlManager_.control("CustomCrossover.true");
                }
            } else {
                labelCrossoverType_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                radioButtonCrossoverUniform_.setDisable(true);
                radioButtonCrossoverSingle_.setDisable(true);
                radioButtonCrossoverTwo_.setDisable(true);
                radioButtonCrossoverCustom_.setDisable(true);
                controlManager_.control("CustomCrossover.false");
            }
        });

        // Custom crossover control
        labelCustomCrossover_.setText(LayoutConstants.CUSTOM_POINT_CROSSOVER_LABEL);
        labelCustomCrossoverRange1_.setText(LayoutConstants.CUSTOM_POINT_CROSSOVER_RANGE[0]);
        labelCustomCrossoverRange2_.setText(LayoutConstants.CUSTOM_POINT_CROSSOVER_RANGE[1]);
        textFieldCustomCrossover_.setText(Integer.toString(LayoutConstants.DEFAULT_CUSTOM_POINT_CROSSOVER));
        textFieldCustomCrossover_.setMaxValue(() -> {
            controlManager_.control("SaveButton.true.setDefault");
            return (int) Math.round(Integer.parseInt(textFieldPopulationSize_.getText())
                    * DefaultConstants.MAX_CUSTOM_CROSSOVER_PERCENTAGE / 100.0);
        });
        textFieldCustomCrossover_.focusedProperty().addListener(new FocusListenerRestrict
                (textFieldCustomCrossover_, LayoutConstants.MIN_CUSTOM_POINT_CROSSOVER,
                        LayoutConstants.DEFAULT_CUSTOM_POINT_CROSSOVER));
        controlManager_.insert("CustomCrossover", (enable) -> {
            if (enable) {
                labelCustomCrossover_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                labelCustomCrossoverRange1_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                labelCustomCrossoverRange2_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                textFieldCustomCrossover_.setEditable(true);
                textFieldCustomCrossover_.setStyle(LayoutConstants.ENABLE_COLOR_BACKGROUND);
            } else {
                labelCustomCrossover_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                labelCustomCrossoverRange1_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                labelCustomCrossoverRange2_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                textFieldCustomCrossover_.setEditable(false);
                textFieldCustomCrossover_.setStyle(LayoutConstants.DISABLE_COLOR_BACKGROUND);
            }
        });

        // Mutation control
        labelMutation_.setText(LayoutConstants.MUTATION_LABEL[0]);
        labelRangeMutation_.setText(LayoutConstants.MUTATION_LABEL[1]);
        labelHelpMutation_.setText(LayoutConstants.MUTATION_LABEL[2]);
        textFieldMutation_.setText(Integer.toString(DefaultConstants.DEFAULT_MUTATION));
        textFieldMutation_.setMaxValue(() -> {
            controlManager_.control("SaveButton.true.setDefault");
            return DefaultConstants.MAX_MUTATION;
        });
        textFieldMutation_.focusedProperty().addListener(new FocusListenerRestrict
                (textFieldMutation_, DefaultConstants.MIN_MUTATION, DefaultConstants.DEFAULT_MUTATION));
        controlManager_.insert("Mutation", (enable) -> {
            if (enable) {
                labelMutation_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                labelRangeMutation_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                labelHelpMutation_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                textFieldMutation_.setEditable(true);
                textFieldMutation_.setStyle(LayoutConstants.ENABLE_COLOR_BACKGROUND);
            } else {
                labelMutation_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                labelRangeMutation_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                labelHelpMutation_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                textFieldMutation_.setEditable(false);
                textFieldMutation_.setStyle(LayoutConstants.DISABLE_COLOR_BACKGROUND);
            }
        });

        // Optimization control
        labelOptimization_.setText(LayoutConstants.OPTIMIZATION_LABEL[0]);
        labelRangeOptimization_.setText(LayoutConstants.OPTIMIZATION_LABEL[1]);
        labelHelpOptimization_.setText(LayoutConstants.OPTIMIZATION_LABEL[2]);
        textFieldOptimization_.setText(Integer.toString(DefaultConstants.DEFAULT_OPTIMIZATION));
        textFieldOptimization_.setMaxValue(() -> {
            controlManager_.control("SaveButton.true.setDefault");
            return DefaultConstants.MAX_OPTIMIZATION;
        });
        textFieldOptimization_.focusedProperty().addListener(new FocusListenerRestrict
                (textFieldOptimization_, DefaultConstants.MIN_OPTIMIZATION, DefaultConstants.DEFAULT_OPTIMIZATION));
        controlManager_.insert("Optimization", (enable) -> {
            if (enable) {
                labelOptimization_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                labelRangeOptimization_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                labelHelpOptimization_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                textFieldOptimization_.setEditable(true);
                textFieldOptimization_.setStyle(LayoutConstants.ENABLE_COLOR_BACKGROUND);
            } else {
                labelOptimization_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                labelRangeOptimization_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                labelHelpOptimization_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                textFieldOptimization_.setEditable(false);
                textFieldOptimization_.setStyle(LayoutConstants.DISABLE_COLOR_BACKGROUND);
            }
        });

        // Live results check box
        checkBoxLiveGetPopulation_.setText(LayoutConstants.LIVE_POPULATION_CHECKBOX);
        controlManager_.insert("LiveCheckbox", (enable) -> {
            if (enable) {
                checkBoxLiveGetPopulation_.setDisable(false);
                if (checkBoxLiveGetPopulation_.isSelected()) {
                    controlManager_.control("LiveResults.true");
                }
            } else {
                checkBoxLiveGetPopulation_.setDisable(true);
                controlManager_.control("LiveResults.false");
            }
        });

        // Live results population control
        labelLiveGetPopulation_.setText(LayoutConstants.LIVE_POPULATION_LABEL[0]);
        labelRangeLiveGetPopulation_.setText(LayoutConstants.LIVE_POPULATION_LABEL[1]);
        textFieldLiveGetPopulation_.setText(Integer.toString(DefaultConstants.DEFAULT_DISPLAY_LIVE_POPULATION));
        textFieldLiveGetPopulation_.setMaxValue(() -> DefaultConstants.MAX_DISPLAY_LIVE_POPULATION);
        textFieldLiveGetPopulation_.focusedProperty().addListener(new FocusListenerRestrict(textFieldLiveGetPopulation_,
                DefaultConstants.MIN_DISPLAY_LIVE_POPULATION, DefaultConstants.DEFAULT_DISPLAY_LIVE_POPULATION));
        controlManager_.insert("LiveResults", (enable) -> {
            if (enable) {
                labelLiveGetPopulation_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                labelRangeLiveGetPopulation_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                textFieldLiveGetPopulation_.setEditable(true);
                textFieldLiveGetPopulation_.setStyle(LayoutConstants.ENABLE_COLOR_BACKGROUND);
            } else {
                labelLiveGetPopulation_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                labelRangeLiveGetPopulation_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                textFieldLiveGetPopulation_.setEditable(false);
                textFieldLiveGetPopulation_.setStyle(LayoutConstants.DISABLE_COLOR_BACKGROUND);
            }
        });

        // Print population control
        labelPrintPopulation_.setText(LayoutConstants.PRINT_POPULATION_LABEL[0]);
        toggleButtonPrintPopulation_.setText(LayoutConstants.PRINT_POPULATION_TOGGLE_BUTTON[0]);
        labelRangePrintPopulation_.setText(LayoutConstants.PRINT_POPULATION_LABEL[1]);
        textFieldPrintPopulation_.setText(Integer.toString(DefaultConstants.DEFAULT_DISPLAY_PRINT_POPULATION));
        textFieldPrintPopulation_.setMaxValue(() -> DefaultConstants.MAX_DISPLAY_PRINT_POPULATION);
        textFieldPrintPopulation_.focusedProperty().addListener(new FocusListenerRestrict(textFieldPrintPopulation_,
                DefaultConstants.MIN_DISPLAY_PRINT_POPULATION, DefaultConstants.DEFAULT_DISPLAY_PRINT_POPULATION));
        controlManager_.insert("PrintResults", (enable) -> {
            if (enable) {
                labelPrintPopulation_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                toggleButtonPrintPopulation_.setDisable(false);
                labelRangePrintPopulation_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                textFieldPrintPopulation_.setEditable(true);
                textFieldPrintPopulation_.setStyle(LayoutConstants.ENABLE_COLOR_BACKGROUND);
            } else {
                labelPrintPopulation_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                toggleButtonPrintPopulation_.setDisable(true);
                labelRangePrintPopulation_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                textFieldPrintPopulation_.setEditable(false);
                textFieldPrintPopulation_.setStyle(LayoutConstants.DISABLE_COLOR_BACKGROUND);
            }
        });

        // Standard deviation control
        labelStandardDeviation_.setText(LayoutConstants.STANDARD_DEVIATION_LABELS);
        toggleButtonStandardDeviation_.setText(LayoutConstants.STANDARD_DEVIATION_BUTTON[0]);
        textFieldStandardDeviation_.setEditable(false);
        textFieldStandardDeviation_.setStyle(LayoutConstants.NOT_EDITABLE_COLOR_BACKGROUND);
        controlManager_.insert("StandardDeviation", (enable) -> {
            if (enable) {
                labelStandardDeviation_.setTextFill(LayoutConstants.ENABLE_TEXT_COLOR);
                toggleButtonStandardDeviation_.setDisable(false);
            } else {
                labelStandardDeviation_.setTextFill(LayoutConstants.DISABLE_TEXT_COLOR);
                toggleButtonStandardDeviation_.setDisable(true);
            }
        });

        // Body tabs
        tabBodyLiveResults_.setText(LayoutConstants.BODY_TAB[0]);
        tabBodyGetResults_.setText(LayoutConstants.BODY_TAB[1]);
        tabPaneBody_.getTabs().addAll(tabBodyLiveResults_, tabBodyGetResults_);

        // TextArea size to parent pane
        textAreaLiveResults_.prefWidthProperty().bind(scrollPaneLiveResults_.widthProperty());
        textAreaLiveResults_.prefHeightProperty().bind(scrollPaneLiveResults_.heightProperty());

        textAreaGetResults_.prefWidthProperty().bind(scrollPaneGetResults_.widthProperty());
        textAreaGetResults_.prefHeightProperty().bind(scrollPaneGetResults_.heightProperty());

        // Start
        controlManager_.control(
                "PopulateButton.false", "EvolutionButton.false", "SaveButton.false", "SaveAsButton.false", "NewButton.false", "OpenButton.false",
                "GeneSize.false", "ChromosomeSize.false", "PopulationSize.false", "LockButton.false", "ResizeButton.false", "RevertButton.false",
                "CrossoverButton.false", "CustomCrossover.false", "Mutation.false", "Optimization.false",
                "LiveCheckbox.false", "LiveResults.false", "PrintResults.false", "StandardDeviation.false");
        geneticAlgorithm_.addFields(textAreaLiveResults_);
        this.initializeFile();
    }

    /**
     * Closing methods
     */
    private boolean isRunning() {
        return !doneIOFile_ && !donePrintPopulation_ && !doneStandardDeviation_;
    }

    private boolean isAllRunning() {
        return geneticAlgorithm_.isRunning() && geneticAlgorithmTools_.isRunning() && this.isRunning();
    }

    boolean isOneRunning() {
        return geneticAlgorithm_.isRunning() || geneticAlgorithmTools_.isRunning() || this.isRunning();
    }

    boolean isSaved() {
        return buttonSaveFile_.isDisabled();
    }

    void stopRunning() {
        try {
            scheduledExecutorTimer_.shutdownNow();
            geneticAlgorithm_.stop();
            geneticAlgorithmTools_.stop();
            while (this.isAllRunning()) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populate button
     */
    public void onPopulate() {
        if (!geneticAlgorithm_.isPopulated()) {
            if (toggleButtonPopulate_.isSelected()) {
                controlManager_.control(
                        "PopulateButton.true", "EvolutionButton.false", "SaveButton.false", "SaveAsButton.false", "NewButton.false", "OpenButton.false",
                        "GeneSize.false", "ChromosomeSize.false", "PopulationSize.false", "LockButton.false", "ResizeButton.false", "RevertButton.false",
                        "CrossoverButton.false", "CustomCrossover.false", "Mutation.false", "Optimization.false",
                        "LiveCheckbox.false", "LiveResults.false", "PrintResults.false", "StandardDeviation.false");
                toggleButtonPopulate_.setText(LayoutConstants.POPULATE_TOGGLEBUTTON[1]);

                int minGeneValue = DefaultConstants.MIN_GENE_VALUE;
                int geneSize = Integer.parseInt(textFieldGeneSize_.getText());
                int chromosomeSize = Integer.parseInt(textFieldChromosomeSize_.getText());
                int populationSize = Integer.parseInt(textFieldPopulationSize_.getText());
                geneticAlgorithm_.runPopulate(minGeneValue, geneSize, chromosomeSize, populationSize, this::waitDonePopulate);
            } else {
                geneticAlgorithm_.stop();
            }
        }
    }

    private void waitDonePopulate(Boolean success) {
        if (success) {
            controlManager_.control(
                    "PopulateButton.false", "EvolutionButton.true", "SaveButton.true", "SaveAsButton.true", "NewButton.true", "OpenButton.true",
                    "GeneSize.false", "ChromosomeSize.false", "PopulationSize.false", "LockButton.true", "ResizeButton.false", "RevertButton.false",
                    "CrossoverButton.true", "CustomCrossover.null", "Mutation.true", "Optimization.true",
                    "LiveCheckbox.true", "LiveResults.null", "PrintResults.true", "StandardDeviation.true");
            toggleButtonPopulate_.setText(LayoutConstants.POPULATE_TOGGLEBUTTON[2]);
        } else {
            controlManager_.control(
                    "PopulateButton.true", "EvolutionButton.false", "SaveButton.default", "SaveAsButton.true", "NewButton.true", "OpenButton.true",
                    "GeneSize.true", "ChromosomeSize.true", "PopulationSize.true", "LockButton.false", "ResizeButton.false", "RevertButton.false",
                    "CrossoverButton.false", "CustomCrossover.false", "Mutation.false", "Optimization.false",
                    "LiveCheckbox.false", "LiveResults.false", "PrintResults.false", "StandardDeviation.false");
            toggleButtonPopulate_.setText(LayoutConstants.POPULATE_TOGGLEBUTTON[0]);
        }
    }

    /**
     * Evolve button
     */
    public void onEvolution() {
        if (geneticAlgorithm_.isPopulated()) {
            if (toggleButtonEvolution_.isSelected()) {
                controlManager_.control(
                        "PopulateButton.false", "EvolutionButton.true", "SaveButton.false", "SaveAsButton.false", "NewButton.false", "OpenButton.false",
                        "GeneSize.false", "ChromosomeSize.false", "PopulationSize.false", "LockButton.false", "ResizeButton.false", "RevertButton.false",
                        "CrossoverButton.false", "CustomCrossover.false", "Mutation.false", "Optimization.false",
                        "LiveCheckbox.false", "LiveResults.false", "PrintResults.false", "StandardDeviation.false");
                toggleButtonEvolution_.setText(LayoutConstants.EVOLUTION_TOGGLEBUTTON[1]);
                this.timerStart();
                geneticAlgorithm_.useCallNewChromosome(checkBoxLiveGetPopulation_.isSelected());
                geneticAlgorithm_.setDisplayFitness(Integer.parseInt(textFieldLiveGetPopulation_.getText()));

                int crossoverType = (int) crossoverRadioGroup_.getSelectedToggle().getUserData();
                if (crossoverType == 3) {
                    crossoverType = Integer.parseInt(textFieldCustomCrossover_.getText());
                }
                int mutationPercent = Integer.parseInt(textFieldMutation_.getText());
                int optimizePercent = Integer.parseInt(textFieldOptimization_.getText());
                geneticAlgorithm_.runEvolve(crossoverType, mutationPercent, optimizePercent, this::waitDoneEvolution);
            } else {
                this.timerStop();
                geneticAlgorithm_.stop();
                geneticAlgorithmTools_.stop();
            }
        }
    }

    private void waitDoneEvolution() {
        controlManager_.control(
                "PopulateButton.false", "EvolutionButton.true", "SaveButton.true", "SaveAsButton.true", "NewButton.true", "OpenButton.true",
                "GeneSize.false", "ChromosomeSize.false", "PopulationSize.false", "LockButton.true", "ResizeButton.false", "RevertButton.false",
                "CrossoverButton.true", "CustomCrossover.null", "Mutation.true", "Optimization.true",
                "LiveCheckbox.true", "LiveResults.null", "PrintResults.true", "StandardDeviation.true");
        toggleButtonEvolution_.setText(LayoutConstants.EVOLUTION_TOGGLEBUTTON[0]);
    }

    /**
     * File controls
     */
    public void onSaveFile() {
        buttonSaveFile_.setText(LayoutConstants.SAVE_BUTTON[1]);
        textFieldFile_.setStyle(LayoutConstants.TEXTFIELD_TEXT_COLOR_RED);
        textFieldFile_.setText(LayoutConstants.FILENAME_SAVE + file_.getName());
        controlManager_.control(
                "PopulateButton.setDefault.false", "EvolutionButton.setDefault.false", "SaveButton.false", "SaveAsButton.false", "NewButton.false", "OpenButton.false",
                "GeneSize.setDefault.false", "ChromosomeSize.setDefault.false", "PopulationSize.setDefault.false", "LockButton.setDefault.false", "ResizeButton.false", "RevertButton.false",
                "CrossoverButton.setDefault.false", "CustomCrossover.false", "Mutation.setDefault.false", "Optimization.setDefault.false",
                "LiveCheckbox.setDefault.false", "LiveResults.false", "PrintResults.setDefault.false", "StandardDeviation.setDefault.false");
        fileChooser_.setTitle("Save file");
        this.saveCurrentFile(file_, this::waitDoneOnSaveFile);
    }

    private void waitDoneOnSaveFile(Boolean success, File file) {
        buttonSaveFile_.setText(LayoutConstants.SAVE_BUTTON[0]);
        textFieldFile_.setStyle(LayoutConstants.TEXTFIELD_TEXT_COLOR_BLACK);
        textFieldFile_.setText(file.getName());
        if (success) {
            controlManager_.control(
                    "PopulateButton.default", "EvolutionButton.default", "SaveButton.false.setDefault", "SaveAsButton.true", "NewButton.true", "OpenButton.true",
                    "GeneSize.default", "ChromosomeSize.default", "PopulationSize.default", "LockButton.default", "ResizeButton.false", "RevertButton.false",
                    "CrossoverButton.default", "CustomCrossover.null", "Mutation.default", "Optimization.default",
                    "LiveCheckbox.default", "LiveResults.null", "PrintResults.default", "StandardDeviation.default");
        } else {
            this.saveFileErrorMessage(file);
            controlManager_.control(
                    "PopulateButton.default", "EvolutionButton.default", "SaveButton.true.setDefault", "SaveAsButton.true", "NewButton.true", "OpenButton.true",
                    "GeneSize.default", "ChromosomeSize.default", "PopulationSize.default", "LockButton.default", "ResizeButton.false", "RevertButton.false",
                    "CrossoverButton.default", "CustomCrossover.null", "Mutation.default", "Optimization.default",
                    "LiveCheckbox.default", "LiveResults.null", "PrintResults.default", "StandardDeviation.default");
        }
    }

    public void onSaveAsFile() {
        fileChooser_.setTitle("Save file as");
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        fileChooser_.setInitialFileName(DefaultConstants.DEFAULT_FILE + " " + dateTimeFormatter.format(localDate));
        File file = fileChooser_.showSaveDialog(stage_);
        if (file != null) {
            buttonSaveAsFile_.setText(LayoutConstants.SAVEAS_BUTTON[1]);
            textFieldFile_.setStyle(LayoutConstants.TEXTFIELD_TEXT_COLOR_RED);
            textFieldFile_.setText(LayoutConstants.FILENAME_SAVE + file.getName());
            controlManager_.control(
                    "PopulateButton.setDefault.false", "EvolutionButton.setDefault.false", "SaveButton.false", "SaveAsButton.false", "NewButton.false", "OpenButton.false",
                    "GeneSize.setDefault.false", "ChromosomeSize.setDefault.false", "PopulationSize.setDefault.false", "LockButton.setDefault.false", "ResizeButton.false", "RevertButton.false",
                    "CrossoverButton.setDefault.false", "CustomCrossover.false", "Mutation.setDefault.false", "Optimization.setDefault.false",
                    "LiveCheckbox.setDefault.false", "LiveResults.false", "PrintResults.setDefault.false", "StandardDeviation.setDefault.false");

            this.saveCurrentFile(file, this::waitDoneOnSaveAsFile);
        }
    }

    private void waitDoneOnSaveAsFile(Boolean success, File file) {
        buttonSaveAsFile_.setText(LayoutConstants.SAVEAS_BUTTON[0]);
        textFieldFile_.setStyle(LayoutConstants.TEXTFIELD_TEXT_COLOR_BLACK);
        if (success) {
            file_ = file;
            textFieldFile_.setText(file_.getName());
            controlManager_.control(
                    "PopulateButton.default", "EvolutionButton.default", "SaveButton.false.setDefault", "SaveAsButton.true", "NewButton.true", "OpenButton.true",
                    "GeneSize.default", "ChromosomeSize.default", "PopulationSize.default", "LockButton.default", "ResizeButton.false", "RevertButton.false",
                    "CrossoverButton.default", "CustomCrossover.null", "Mutation.default", "Optimization.default",
                    "LiveCheckbox.default", "LiveResults.null", "PrintResults.default", "StandardDeviation.default");
        } else {
            textFieldFile_.setText(file_.getName());
            controlManager_.control(
                    "PopulateButton.default", "EvolutionButton.default", "SaveButton.true.setDefault", "SaveAsButton.true", "NewButton.true", "OpenButton.true",
                    "GeneSize.default", "ChromosomeSize.default", "PopulationSize.default", "LockButton.default", "ResizeButton.false", "RevertButton.false",
                    "CrossoverButton.default", "CustomCrossover.null", "Mutation.default", "Optimization.default",
                    "LiveCheckbox.default", "LiveResults.null", "PrintResults.default", "StandardDeviation.default");
            this.saveFileErrorMessage(file);
        }
    }

    private void saveFileErrorMessage(File file) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(LayoutConstants.FILENAME_SAVE_ERROR_MESSAGE[0] + file.getName()
                + LayoutConstants.FILENAME_SAVE_ERROR_MESSAGE[1]);
        alert.showAndWait();
    }

    public void onNewFile() {
        fileChooser_.setTitle("New file");
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        fileChooser_.setInitialFileName(DefaultConstants.DEFAULT_FILE + " " + dateTimeFormatter.format(localDate));
        File file = fileChooser_.showSaveDialog(stage_);

        if (file != null) {
            buttonNewFile_.setText(LayoutConstants.NEW_BUTTON[1]);
            textFieldFile_.setStyle(LayoutConstants.TEXTFIELD_TEXT_COLOR_RED);
            textFieldFile_.setText(LayoutConstants.FILENAME_SAVE + DEFAULT_FILE.getName());

            controlManager_.control(
                    "PopulateButton.setDefault.false", "EvolutionButton.setDefault.false", "SaveButton.false", "SaveAsButton.false", "NewButton.false", "OpenButton.false",
                    "GeneSize.setDefault.false", "ChromosomeSize.setDefault.false", "PopulationSize.setDefault.false", "LockButton.setDefault.false", "ResizeButton.false", "RevertButton.false",
                    "CrossoverButton.setDefault.false", "CustomCrossover.false", "Mutation.setDefault.false", "Optimization.setDefault.false",
                    "LiveCheckbox.setDefault.false", "LiveResults.false", "PrintResults.setDefault.false", "StandardDeviation.setDefault.false");

            int crossoverType = DefaultConstants.DEFAULT_CROSSOVER_TYPE;
            int mutationPercent = DefaultConstants.DEFAULT_MUTATION;
            int optimizePercent = DefaultConstants.DEFAULT_OPTIMIZATION;
            int minGeneValue = DefaultConstants.MIN_GENE_VALUE;
            int geneSize = DefaultConstants.DEFAULT_GENE_SIZE;
            int chromosomeSize = DefaultConstants.DEFAULT_CHROMOSOME_SIZE;
            int populationSize = DefaultConstants.DEFAULT_POPULATION_SIZE;

            this.saveFile(null, null, minGeneValue, geneSize, chromosomeSize, populationSize,
                    crossoverType, mutationPercent, optimizePercent, 0, 0, 0,
                    file, this::waitDoneOnNewFile);
        }
    }

    private void waitDoneOnNewFile(Boolean success, File file) {
        buttonNewFile_.setText(LayoutConstants.NEW_BUTTON[0]);
        textFieldFile_.setStyle(LayoutConstants.TEXTFIELD_TEXT_COLOR_BLACK);
        if (success) {
            file_ = file;
            textFieldFile_.setText(file.getName());
            geneticAlgorithm_.setPopulation(null, null, DefaultConstants.MIN_GENE_VALUE, DefaultConstants.DEFAULT_GENE_SIZE);

            this.displayPopulationControls(DefaultConstants.DEFAULT_GENE_SIZE, DefaultConstants.DEFAULT_CHROMOSOME_SIZE,
                    DefaultConstants.DEFAULT_POPULATION_SIZE);
            this.displayCrossoverControls(DefaultConstants.DEFAULT_CROSSOVER_TYPE, DefaultConstants.DEFAULT_MUTATION,
                    DefaultConstants.DEFAULT_OPTIMIZATION);
            this.timerSet(0,0,0);

            toggleButtonPopulate_.setText(LayoutConstants.POPULATE_TOGGLEBUTTON[0]);
            toggleButtonPopulate_.setSelected(false);
            toggleButtonEvolution_.setText(LayoutConstants.EVOLUTION_TOGGLEBUTTON[0]);
            toggleButtonEvolution_.setSelected(false);

            this.clearResultTabs();

            controlManager_.control(
                    "PopulateButton.true", "EvolutionButton.false", "SaveButton.false.setDefault", "SaveAsButton.true", "NewButton.true", "OpenButton.true",
                    "GeneSize.true", "ChromosomeSize.true", "PopulationSize.true", "LockButton.false", "ResizeButton.false", "RevertButton.false",
                    "CrossoverButton.false", "CustomCrossover.false", "Mutation.false", "Optimization.false",
                    "LiveCheckbox.false", "LiveResults.false", "PrintResults.false", "StandardDeviation.false");
        } else {
            textFieldFile_.setText(file_.getName());
            this.saveFileErrorMessage(file);
            controlManager_.control(
                    "PopulateButton.default", "EvolutionButton.default", "SaveButton.true.setDefault", "SaveAsButton.true", "NewButton.true", "OpenButton.true",
                    "GeneSize.default", "ChromosomeSize.default", "PopulationSize.default", "LockButton.default", "ResizeButton.false", "RevertButton.false",
                    "CrossoverButton.default", "CustomCrossover.null", "Mutation.default", "Optimization.default",
                    "LiveCheckbox.default", "LiveResults.null", "PrintResults.default", "StandardDeviation.default");
        }
    }

    public void onOpenFile() {
        fileChooser_.setTitle("Open file");
        File file = fileChooser_.showOpenDialog(stage_);
        if (file != null) {
            textFieldFile_.setStyle(LayoutConstants.TEXTFIELD_TEXT_COLOR_RED);
            textFieldFile_.setText(LayoutConstants.FILENAME_OPEN + file.getName());
            buttonOpenFile_.setText(LayoutConstants.OPEN_BUTTON[1]);
            controlManager_.control(
                    "PopulateButton.setDefault.false", "EvolutionButton.setDefault.false", "SaveButton.false", "SaveAsButton.false", "NewButton.false", "OpenButton.false",
                    "GeneSize.setDefault.false", "ChromosomeSize.setDefault.false", "PopulationSize.setDefault.false", "LockButton.setDefault.false", "ResizeButton.false", "RevertButton.false",
                    "CrossoverButton.setDefault.false", "CustomCrossover.false", "Mutation.setDefault.false", "Optimization.setDefault.false",
                    "LiveCheckbox.setDefault.false", "LiveResults.false", "PrintResults.setDefault.false", "StandardDeviation.setDefault.false");
            this.openFile(file, this::waitDoneOnOpenFile);
        }
    }

    private void waitDoneOnOpenFile(boolean success, File file) {
        this.clearResultTabs();
        buttonOpenFile_.setText(LayoutConstants.OPEN_BUTTON[0]);
        textFieldFile_.setStyle(LayoutConstants.TEXTFIELD_TEXT_COLOR_BLACK);
        if (success) {
            file_ = file;
            textFieldFile_.setText(file_.getName());
            if (geneticAlgorithm_.isPopulated()) {
                toggleButtonPopulate_.setText(LayoutConstants.POPULATE_TOGGLEBUTTON[2]);
                toggleButtonPopulate_.setSelected(false);
                toggleButtonEvolution_.setText(LayoutConstants.EVOLUTION_TOGGLEBUTTON[0]);
                toggleButtonEvolution_.setSelected(false);
                controlManager_.control(
                        "PopulateButton.false", "EvolutionButton.true", "SaveButton.false.setDefault", "SaveAsButton.true", "NewButton.true", "OpenButton.true",
                        "GeneSize.false", "ChromosomeSize.false", "PopulationSize.false", "LockButton.true", "ResizeButton.false", "RevertButton.false",
                        "CrossoverButton.true", "CustomCrossover.null", "Mutation.true", "Optimization.true",
                        "LiveCheckbox.true", "LiveResults.null", "PrintResults.true", "StandardDeviation.true");
            } else {
                toggleButtonPopulate_.setText(LayoutConstants.POPULATE_TOGGLEBUTTON[0]);
                toggleButtonPopulate_.setSelected(false);
                toggleButtonEvolution_.setText(LayoutConstants.EVOLUTION_TOGGLEBUTTON[0]);
                toggleButtonEvolution_.setSelected(false);
                controlManager_.control(
                        "PopulateButton.true", "EvolutionButton.false", "SaveButton.false.setDefault", "SaveAsButton.true", "NewButton.true", "OpenButton.true",
                        "GeneSize.true", "ChromosomeSize.true", "PopulationSize.true", "LockButton.false", "ResizeButton.false", "RevertButton.false",
                        "CrossoverButton.false", "CustomCrossover.false", "Mutation.false", "Optimization.false",
                        "LiveCheckbox.false", "LiveResults.false", "PrintResults.false", "StandardDeviation.false");
            }
        } else {
            textFieldFile_.setText(file_.getName());
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText(LayoutConstants.FILENAME_OPEN_ERROR_MESSAGE);
            alert.showAndWait();
            controlManager_.control(
                    "PopulateButton.default", "EvolutionButton.default", "SaveButton.default", "SaveAsButton.true", "NewButton.true", "OpenButton.true",
                    "GeneSize.default", "ChromosomeSize.default", "PopulationSize.default", "LockButton.default", "ResizeButton.false", "RevertButton.false",
                    "CrossoverButton.default", "CustomCrossover.null", "Mutation.default", "Optimization.default",
                    "LiveCheckbox.default", "LiveResults.null", "PrintResults.default", "StandardDeviation.default");
        }
    }

    private void saveCurrentFile(final File file, final BiConsumer<Boolean, File> waitDoneMethod) {
        int crossoverType = (int) crossoverRadioGroup_.getSelectedToggle().getUserData();
        if (crossoverType == 3) {
            crossoverType = Integer.parseInt(textFieldCustomCrossover_.getText());
        }
        int mutationPercent = Integer.parseInt(textFieldMutation_.getText());
        int optimizePercent = Integer.parseInt(textFieldOptimization_.getText());

        if (geneticAlgorithm_.isPopulated()) {
            this.saveFile(geneticAlgorithm_.getPopulation(), geneticAlgorithm_.getFitness(),
                    geneticAlgorithm_.getMinGeneValue(), geneticAlgorithm_.getGeneSize(), geneticAlgorithm_.getChromosomeSize(),
                    geneticAlgorithm_. getPopulationSize(), crossoverType, mutationPercent, optimizePercent, timerHours_,
                    timerMinutes_, timerSeconds_, file, waitDoneMethod);
        } else {
            int minGeneValue = DefaultConstants.MIN_GENE_VALUE;
            int geneSize = Integer.parseInt(textFieldGeneSize_.getText());
            int chromosomeSize = Integer.parseInt(textFieldChromosomeSize_.getText());
            int populationSize = Integer.parseInt(textFieldPopulationSize_.getText());
            this.saveFile(null, null, minGeneValue, geneSize, chromosomeSize, populationSize,
                    crossoverType, mutationPercent, optimizePercent, timerHours_, timerMinutes_, timerSeconds_, file,
                    waitDoneMethod);
        }
    }

    private void saveFile(final int[][] population, final int[] fitness, final int minGeneValue, final int geneSize,
                          final int chromosomeSize, final int populationSize, final int crossoverType, final int mutationPercent,
                          final int optimizePercent, final int timerHours, final int timerMinutes, final int timerSeconds,
                          final File file, final BiConsumer<Boolean, File> waitDoneMethod) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            doneIOFile_ = false;
            try {
                ArrayList<Object> arrayList = new ArrayList<>(13);
                arrayList.add(DefaultConstants.DEFAULT_EXTENSION);
                arrayList.add(population);
                arrayList.add(fitness);
                arrayList.add(minGeneValue);
                arrayList.add(geneSize);
                arrayList.add(chromosomeSize);
                arrayList.add(populationSize);
                arrayList.add(crossoverType);
                arrayList.add(mutationPercent);
                arrayList.add(optimizePercent);
                arrayList.add(timerHours);
                arrayList.add(timerMinutes);
                arrayList.add(timerSeconds);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(arrayList);
                fileOutputStream.close();
                Platform.runLater(() -> waitDoneMethod.accept(true, file));
            } catch (IOException e) {
                Platform.runLater(() -> waitDoneMethod.accept(false, file));
            }
            doneIOFile_ = true;
        });
        executorService.shutdown();
    }

    private void openFile(final File file, final BiConsumer<Boolean, File> waitDoneMethod) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            doneIOFile_ = false;
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                Object object = objectInputStream.readObject();
                if (object instanceof ArrayList<?>) {
                    ArrayList<?> arrayList = (ArrayList<?>) object;
                    int[][] population = (int[][]) arrayList.get(1);
                    int[] fitness = (int[]) arrayList.get(2);
                    int minGeneValue = (Integer) arrayList.get(3);

                    String fileExtension = (String) arrayList.get(0);

                    int geneSize = (Integer) arrayList.get(4);
                    if (geneSize < DefaultConstants.MIN_GENE_SIZE) {
                        geneSize = DefaultConstants.MIN_GENE_SIZE;
                    } else if (geneSize > DefaultConstants.MAX_GENE_SIZE) {
                        geneSize = DefaultConstants.MAX_GENE_SIZE;
                    }

                    int chromosomeSize = (Integer) arrayList.get(5);
                    if (chromosomeSize < DefaultConstants.MIN_CHROMOSOME_SIZE) {
                        chromosomeSize = DefaultConstants.MIN_CHROMOSOME_SIZE;
                    } else if (chromosomeSize > DefaultConstants.MAX_CHROMOSOME_SIZE) {
                        chromosomeSize = DefaultConstants.MAX_CHROMOSOME_SIZE;
                    }

                    int populationSize = (Integer) arrayList.get(6);
                    if (populationSize < DefaultConstants.MIN_POPULATION_SIZE) {
                        populationSize = DefaultConstants.MIN_POPULATION_SIZE;
                    } else if (populationSize > DefaultConstants.MAX_POPULATION_SIZE) {
                        populationSize = DefaultConstants.MAX_POPULATION_SIZE;
                    }

                    int crossoverType = (Integer) arrayList.get(7);

                    int mutationPercent = (Integer) arrayList.get(8);
                    if (mutationPercent < DefaultConstants.MIN_MUTATION) {
                        mutationPercent = DefaultConstants.MIN_MUTATION;
                    } else if (mutationPercent > DefaultConstants.MAX_MUTATION) {
                        mutationPercent = DefaultConstants.MAX_MUTATION;
                    }

                    int optimizePercent = (Integer) arrayList.get(9);
                    if (optimizePercent < DefaultConstants.MIN_OPTIMIZATION) {
                        optimizePercent = DefaultConstants.MIN_OPTIMIZATION;
                    } else if (optimizePercent > DefaultConstants.MAX_OPTIMIZATION) {
                        optimizePercent = DefaultConstants.MAX_OPTIMIZATION;
                    }

                    int timerHours = (Integer) arrayList.get(10);
                    int timerMinutes = (Integer) arrayList.get(11);
                    int timerSeconds = (Integer) arrayList.get(12);

                    this.displayPopulationControls(geneSize, chromosomeSize, populationSize);
                    this.displayCrossoverControls(crossoverType, mutationPercent, optimizePercent);

                    if (population != null && fitness != null) {
                        geneticAlgorithm_.setPopulation(population, fitness, minGeneValue, geneSize);
                    }

                    this.timerSet(timerHours, timerMinutes, timerSeconds);

                    Platform.runLater(() -> waitDoneMethod.accept(Objects.equals(fileExtension, DefaultConstants.DEFAULT_EXTENSION), file));
                }
            } catch (IOException | ClassNotFoundException e) {
                Platform.runLater(() -> waitDoneMethod.accept(false, file));
            }
            doneIOFile_ = true;
        });
        executorService.shutdown();
    }

    private void initializeFile() {
        fileChooser_.getExtensionFilters().add(new FileChooser.ExtensionFilter
                (DefaultConstants.DEFAULT_EXTENSION_FILTER[0], DefaultConstants.DEFAULT_EXTENSION_FILTER[1]));

        File defaultFolderFile = DEFAULT_FILE.getParentFile();
        textFieldFile_.setStyle(LayoutConstants.TEXTFIELD_TEXT_COLOR_RED);
        textFieldFile_.setText(LayoutConstants.FILENAME_OPEN + DEFAULT_FILE.getName());

        if (DEFAULT_FILE.exists()) {
            this.openFile(DEFAULT_FILE, this::waitDoneInitializeFile);
        } else {
            if (defaultFolderFile.exists()) {
                this.saveCurrentFile(DEFAULT_FILE, this::waitDoneInitializeFile);
            } else if (defaultFolderFile.mkdir()) {
                this.saveCurrentFile(DEFAULT_FILE, this::waitDoneInitializeFile);
            }
        }
    }

    private void waitDoneInitializeFile(boolean success, File file) {
        textFieldFile_.setStyle(LayoutConstants.TEXTFIELD_TEXT_COLOR_BLACK);
        if (success) {
            textFieldFile_.setText(file.getName());
            if (geneticAlgorithm_.isPopulated()) {
                controlManager_.control(
                        "PopulateButton.false", "EvolutionButton.true", "SaveButton.false.setDefault", "SaveAsButton.true", "NewButton.true", "OpenButton.true",
                        "GeneSize.false", "ChromosomeSize.false", "PopulationSize.false", "LockButton.true", "ResizeButton.false", "RevertButton.false",
                        "CrossoverButton.true", "CustomCrossover.null", "Mutation.true", "Optimization.true",
                        "LiveCheckbox.true", "LiveResults.null", "PrintResults.true", "StandardDeviation.true");
                toggleButtonPopulate_.setText(LayoutConstants.POPULATE_TOGGLEBUTTON[2]);
                toggleButtonPopulate_.setSelected(false);
                toggleButtonEvolution_.setText(LayoutConstants.EVOLUTION_TOGGLEBUTTON[0]);
                toggleButtonEvolution_.setSelected(false);
            } else {
                controlManager_.control(
                        "PopulateButton.true", "EvolutionButton.false", "SaveButton.false.setDefault", "SaveAsButton.true", "NewButton.true", "OpenButton.true",
                        "GeneSize.true", "ChromosomeSize.true", "PopulationSize.true", "LockButton.false", "ResizeButton.false", "RevertButton.false",
                        "CrossoverButton.false", "CustomCrossover.false", "Mutation.false", "Optimization.false",
                        "LiveCheckbox.false", "LiveResults.false", "PrintResults.false", "StandardDeviation.false");
                toggleButtonPopulate_.setText(LayoutConstants.POPULATE_TOGGLEBUTTON[0]);
                toggleButtonPopulate_.setSelected(false);
                toggleButtonEvolution_.setText(LayoutConstants.EVOLUTION_TOGGLEBUTTON[0]);
                toggleButtonEvolution_.setSelected(false);
            }
        } else {
            controlManager_.control(
                    "PopulateButton.false", "EvolutionButton.false", "SaveButton.false", "SaveAsButton.false", "NewButton.true", "OpenButton.true",
                    "GeneSize.false", "ChromosomeSize.false", "PopulationSize.false", "LockButton.false", "ResizeButton.false", "RevertButton.false",
                    "CrossoverButton.false", "CustomCrossover.false", "Mutation.false", "Optimization.false",
                    "LiveCheckbox.false", "LiveResults.false", "PrintResults.false", "StandardDeviation.false");
            textFieldFile_.setText(LayoutConstants.FILENAME_NONE);
        }
    }

    void setStage(Stage stage) {
        stage_ = stage;
    }

    private void clearResultTabs() {
        textAreaLiveResults_.clear();
        textAreaGetResults_.clear();
    }

    /**
     * Population controls
     */
    public void onPopulationUnlock() {
        if (toggleButtonPopulationLock_.isSelected()) {
            controlManager_.control(
                    "PopulationSize.true", "LockButton.true", "ResizeButton.true", "RevertButton.true");
            toggleButtonPopulationLock_.setText(LayoutConstants.LOCK_TOGGLEBUTTON[0]);
            populationSizeStore_ = Integer.parseInt(textFieldPopulationSize_.getText());
        } else {
            controlManager_.control(
                    "PopulationSize.false", "LockButton.true", "ResizeButton.false", "RevertButton.false");
            toggleButtonPopulationLock_.setText(LayoutConstants.LOCK_TOGGLEBUTTON[1]);
        }
    }

    public void onPopulationResize() {
        if (geneticAlgorithm_.isPopulated()) {
            if (toggleButtonPopulationResize_.isSelected()) {
                if (populationSizeStore_ == Integer.parseInt(textFieldPopulationSize_.getText())) {
                    toggleButtonPopulationResize_.setSelected(false);
                } else {
                    controlManager_.control(
                            "PopulationSize.false", "LockButton.false", "ResizeButton.null", "RevertButton.false");
                    toggleButtonPopulationResize_.setText(LayoutConstants.RESIZE_TOGGLEBUTTON[1]);
                    geneticAlgorithm_.editPopulate(Integer.parseInt(textFieldPopulationSize_.getText()), this::waitDonePopulationResize);
                }
            } else {
                geneticAlgorithm_.stop();
            }
        } else {
            toggleButtonPopulationResize_.setSelected(false);
        }
    }

    private void waitDonePopulationResize(boolean success) {
        if (!success) {
            textFieldPopulationSize_.setText(Integer.toString(populationSizeStore_));
        }
        controlManager_.control(
                "PopulationSize.false", "LockButton.true", "ResizeButton.false", "RevertButton.false");
        toggleButtonPopulationResize_.setSelected(false);
        toggleButtonPopulationResize_.setText(LayoutConstants.RESIZE_TOGGLEBUTTON[0]);
        toggleButtonPopulationLock_.setSelected(false);
        toggleButtonPopulationLock_.setText(LayoutConstants.LOCK_TOGGLEBUTTON[1]);
    }

    public void onPopulationRevert() {
        textFieldPopulationSize_.setText(Integer.toString(populationSizeStore_));
    }

    private void displayPopulationControls(final int geneSize, final int chromosomeSize, final int populationSize) {
        textFieldGeneSize_.setText(Integer.toString(geneSize));
        textFieldChromosomeSize_.setText(Integer.toString(chromosomeSize));
        textFieldPopulationSize_.setText(Integer.toString(populationSize));
    }

    /**
     * Crossover controls
     */
    public void onCustomCrossover() {
        controlManager_.control("CustomCrossover.true");
    }

    public void offCustomCrossover() {
        controlManager_.control("CustomCrossover.false");
    }

    public void onCheckBoxLivePopulation() {
        if (checkBoxLiveGetPopulation_.isSelected()) {
            controlManager_.control("LiveResults.true");
        } else {
            controlManager_.control("LiveResults.false");
        }
    }

    private void displayCrossoverControls(final int crossoverType, final int mutation, final int optimization) {
        switch(crossoverType) {
            case 0:
                radioButtonCrossoverUniform_.setSelected(true);
            case 1:
                radioButtonCrossoverSingle_.setSelected(true);
                break;
            case 2:
                radioButtonCrossoverTwo_.setSelected(true);
                break;
            default:
                radioButtonCrossoverCustom_.setSelected(true);
                textFieldCustomCrossover_.setText(Integer.toString(crossoverType));
        }
        textFieldMutation_.setText(Integer.toString(mutation));
        textFieldOptimization_.setText(Integer.toString(optimization));
    }

    /**
     * Results controls
     */
    public void onPrintPopulation() {
        if (geneticAlgorithm_.isPopulated()) {
            if (toggleButtonPrintPopulation_.isSelected()) {
                donePrintPopulation_ = false;
                controlManager_.control("PrintResults.false");
                toggleButtonPrintPopulation_.setText(LayoutConstants.PRINT_POPULATION_TOGGLE_BUTTON[1]);
                geneticAlgorithmTools_.printPopulation(geneticAlgorithm_.getPopulation(), geneticAlgorithm_.getFitness(),
                        geneticAlgorithm_.getPopulationSize(), geneticAlgorithm_.getChromosomeSize(),
                        Integer.parseInt(textFieldPrintPopulation_.getText()), this::waitDonePrintPopulation);
            }
        } else {
            toggleButtonPrintPopulation_.setSelected(false);
        }
    }

    private void waitDonePrintPopulation(String results) {
        textAreaGetResults_.setText(results);
        toggleButtonPrintPopulation_.setText(LayoutConstants.PRINT_POPULATION_TOGGLE_BUTTON[0]);
        toggleButtonPrintPopulation_.setSelected(false);
        donePrintPopulation_ = true;
        controlManager_.control("PrintResults.true");
    }

    public void onStandardDeviation() {
        if (geneticAlgorithm_.isPopulated()) {
            if (toggleButtonStandardDeviation_.isSelected()) {
                doneStandardDeviation_ = false;
                controlManager_.control("StandardDeviation.false");
                toggleButtonStandardDeviation_.setText(LayoutConstants.STANDARD_DEVIATION_BUTTON[1]);
                geneticAlgorithmTools_.getStandardDeviation(geneticAlgorithm_.getPopulation(),
                        geneticAlgorithm_.getPopulationSize(), geneticAlgorithm_.getChromosomeSize(),
                        this::waitDoneStandardDeviation);
            }
        } else {
            toggleButtonStandardDeviation_.setSelected(false);
        }
    }

    private void waitDoneStandardDeviation(String results) {
        textFieldStandardDeviation_.setText(results);
        toggleButtonStandardDeviation_.setText(LayoutConstants.STANDARD_DEVIATION_BUTTON[0]);
        toggleButtonStandardDeviation_.setSelected(false);
        doneStandardDeviation_ = true;
        controlManager_.control("StandardDeviation.true");
    }

    /**
     * Timer
     */
    private void timerStart() {
        timerFuture_ = scheduledExecutorTimer_.scheduleAtFixedRate(() -> {
            timerSeconds_++;
            textFieldTimer_.setText(this.timerFormat());
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void timerStop() {
        timerFuture_.cancel(false);
    }

    private void timerSet(int hours, int minutes, int seconds) {
        timerHours_ = hours;
        timerMinutes_ = minutes;
        timerSeconds_ = seconds;
        textFieldTimer_.setText(this.timerFormat());
    }

    private String timerFormat() {
        if (timerSeconds_ >= 60) {
            timerSeconds_ = timerSeconds_ - 60;
            timerMinutes_++;
        }
        if (timerMinutes_ >= 60) {
            timerMinutes_ = timerMinutes_ - 60;
            timerHours_++;
        }
        return Integer.toString(timerHours_) + " : " + String.format("%02d", timerMinutes_) + " : " +
                String.format("%02d", timerSeconds_);
    }
}
