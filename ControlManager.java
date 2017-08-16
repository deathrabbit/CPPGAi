package CPPGAi;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * ControlManager
 *
 * "ControlManager" is used to manage the enabling and disabling
 * of GUI controls
 *
 * @author Scott Matsumura
 * @version 1.0
 */
class ControlManager {

    private final char DELIMITER = '.';
    private final char delimiter_;

    private enum Select {
        FALSE(false, false, false, false),
        SETDEFAULT_FALSE(false, true, false, false),
        FALSE_SETDEFAULT(false, false, true, false),
        TRUE(true, false, false, false),
        SETDEFAULT_TRUE(true, true, false, false),
        TRUE_SETDEFAULT(true, false, true, false),
        DEFAULT(false, true, true, false),
        NULL(false, false, false, true);

        private final boolean selectBoolean_;
        private final boolean preDefaultBoolean_;
        private final boolean postDefaultBoolean_;
        private final boolean nullBoolean_;

        Select(boolean selectBoolean, boolean preDefaultBoolean, boolean postDefaultBoolean, boolean nullBoolean) {
            selectBoolean_ = selectBoolean;
            preDefaultBoolean_ = preDefaultBoolean;
            postDefaultBoolean_ = postDefaultBoolean;
            nullBoolean_ = nullBoolean;
        }

        boolean getSelect() {
            return selectBoolean_;
        }

        boolean getPreDefault() {
            return preDefaultBoolean_;
        }

        boolean getPostDefault() {
            return postDefaultBoolean_;
        }

        boolean getNull() {
            return nullBoolean_;
        }

    }

    private final HashMap<String, Instructions> hashInstructions_;
    private final HashMap<String, Select> hashSelect_;

    private class Instructions {

        private boolean first_;
        private boolean currentSelect_;
        private boolean defaultSelect_;
        private final Consumer<Boolean> consumer_;

        Instructions(final boolean first, final boolean currentSelect, final boolean defaultSelect, final Consumer<Boolean> consumer) {
            first_ = first;
            currentSelect_ = currentSelect;
            defaultSelect_ = defaultSelect;
            consumer_ = consumer;
        }

        boolean getFirstBoolean() {
            return first_;
        }

        void setFirstBoolean(boolean select) {
            first_ = select;
        }

        boolean getCurrentBoolean() {
            return currentSelect_;
        }

        void setCurrentBoolean(boolean select) {
            currentSelect_ = select;
        }

        boolean getDefaultBoolean() {
            return defaultSelect_;
        }

        void setDefaultBoolean(boolean select) {
            defaultSelect_ = select;
        }

        Consumer<Boolean> getConsumer() {
            return consumer_;
        }
    }

    /**
     * Constructor
     */
    ControlManager() {
        hashInstructions_ = new HashMap<>();
        hashSelect_ = new HashMap<>();
        delimiter_ = DELIMITER;
    }

    /**
     * Constructor with custom delimiter
     *
     * @param delimiter     delimiter
     *                      default delimiter is a "."
     */
    ControlManager(final char delimiter) {
        hashInstructions_ = new HashMap<>();
        hashSelect_ = new HashMap<>();
        delimiter_ = delimiter;
    }

    /**
     * control method to enable and disable GUI controls
     *
     * @param args      Name of GUI controls
     *                  Postfix:
     *                  ".true" to enable
     *                  ".setDefault.true" to set default to current state then enable
     *                  ".true.setDefault" to enable then set default to "true"
     *                  ".false" to disable
     *                  ".setDefault.false" to set default to current state then disable
     *                  ".false.setDefault" to disable then set default to "false"
     *                  ".default" selects the default
     *                  ".null" doesn't execute method
     */
    final void control(final String... args) {
        for (String name : args) {
            Instructions instructions = hashInstructions_.get(name);
            if (instructions != null) {
                Select select = hashSelect_.get(name);

                if (!select.getNull()) {
                    if (select.getPreDefault() && select.getPostDefault()) {
                        instructions.getConsumer().accept(instructions.getDefaultBoolean());
                        instructions.setCurrentBoolean(instructions.getDefaultBoolean());

                    } else {
                        if (select.getPreDefault()) {
                            instructions.setDefaultBoolean(instructions.getCurrentBoolean());
                        } else if (select.getPostDefault()) {
                            instructions.setDefaultBoolean(select.getSelect());
                        }

                        if (instructions.getFirstBoolean()) {
                            instructions.setFirstBoolean(false);
                            instructions.setCurrentBoolean(!select.getSelect());
                        }

                        if (select.getSelect() != instructions.getCurrentBoolean()) {
                            instructions.getConsumer().accept(select.getSelect());
                            instructions.setCurrentBoolean(select.getSelect());
                        }
                    }
                }
            } else {
                if(!hashInstructions_.containsKey(name)) {
                    throw new IllegalArgumentException("\"" + name + "\" is not a proper ControlManager instruction.");
                }
            }
        }
    }

    /**
     * Insert the method that enables and disable a GUI control
     * Default state is automatically set to "false".
     *
     *
     * @param name              Name of GUI control
     * @param method            Method
     */
    final void insert(final String name, final Consumer<Boolean> method) {
        this.insertHelper(name, false, method);
    }

    /**
     * Insert the method that enables and disable a GUI control.
     * Includes setting initial default state.
     *
     * @param name              Name of GUI control
     * @param defaultState      Default state
     * @param method            Method
     */
    final void insert(final String name, final boolean defaultState,
                      final Consumer<Boolean> method) {
        this.insertHelper(name, defaultState, method);
    }

    /**
     * Helper method for "insert".
     *
     * @param name              Name of GUI control
     * @param defaultState      Default state
     * @param method            Method
     */
    private void insertHelper(final String name, final boolean defaultState,
                              final Consumer<Boolean> method) {
        String nameFalse = name + delimiter_ + "false";
        String nameDefaultFalse = name + delimiter_ + "setDefault" + delimiter_ + "false";
        String nameFalseDefault = name + delimiter_ + "false" + delimiter_ + "setDefault";
        String nameTrue = name + delimiter_ + "true";
        String nameDefaultTrue = name + delimiter_ + "setDefault" + delimiter_ + "true";
        String nameTrueDefault = name + delimiter_ + "true" + delimiter_ + "setDefault";
        String nameDefault = name + delimiter_ + "default";
        String nameNull = name + delimiter_ + "null";
        hashSelect_.put(nameFalse, Select.FALSE);
        hashSelect_.put(nameDefaultFalse, Select.SETDEFAULT_FALSE);
        hashSelect_.put(nameFalseDefault, Select.FALSE_SETDEFAULT);
        hashSelect_.put(nameTrue, Select.TRUE);
        hashSelect_.put(nameDefaultTrue, Select.SETDEFAULT_TRUE);
        hashSelect_.put(nameTrueDefault, Select.TRUE_SETDEFAULT);
        hashSelect_.put(nameDefault, Select.DEFAULT);
        hashSelect_.put(nameNull, Select.NULL);
        hashSelect_.put(name, Select.NULL);
        Instructions instructions = new Instructions(true,false, defaultState, method);
        hashInstructions_.put(nameFalse, instructions);
        hashInstructions_.put(nameDefaultFalse, instructions);
        hashInstructions_.put(nameFalseDefault, instructions);
        hashInstructions_.put(nameTrue, instructions);
        hashInstructions_.put(nameDefaultTrue, instructions);
        hashInstructions_.put(nameTrueDefault, instructions);
        hashInstructions_.put(nameDefault, instructions);
        hashInstructions_.put(nameNull, instructions);
        hashInstructions_.put(name, instructions);
    }

    /**
     /**
     * Set default state
     *
     * @param name              Name of GUI control
     * @param defaultState      Default state
     *                              "false" is disable
     *                              "true" is enable
     * @return                  "true" if successful,
     *                              "false" if not.
     */
    final boolean setDefault(final String name, final boolean defaultState) {
        boolean returnBoolean;
        if (hashInstructions_.containsKey(name)) {
            Instructions instructions = hashInstructions_.get(name);
            instructions.setDefaultBoolean(defaultState);
            returnBoolean = true;
        } else {
            returnBoolean = false;
        }
        return returnBoolean;
    }

    /**
     * Check every string to determine if method name exists.
     * Used to find illegal argument error.
     *
     * @param args      Name of methods
     * @return          Returns boolean array
     */
    final boolean[] exist(final String... args) {
        int length = args.length;
        boolean returnBoolean[] = new boolean[length];
        for (int i = 0; i < length; i++) {
            returnBoolean[i] = hashInstructions_.containsKey(args[i]);
        }
        return returnBoolean;
    }

    /**
     * Remove method
     *
     * @param name      Name of method
     * @return          Returns "true" if successful,
     *                      "false" if not.
     */
    final boolean remove(final String name) {
        boolean returnBoolean = false;
        if (hashInstructions_.containsKey(name)) {
            String nameFalse = name + delimiter_ + "false";
            String nameDefaultFalse = name + delimiter_ + "setDefault" + delimiter_ + "false";
            String nameFalseDefault = name + delimiter_ + "false" + delimiter_ + "setDefault";
            String nameTrue = name + delimiter_ + "true";
            String nameDefaultTrue = name + delimiter_ + "setDefault" + delimiter_ + "true";
            String nameTrueDefault = name + delimiter_ + "true" + delimiter_ + "setDefault";
            String nameDefault = name + delimiter_ + "default";
            String nameNull = name + delimiter_ + "null";
            hashSelect_.remove(nameFalse);
            hashSelect_.remove(nameDefaultFalse);
            hashSelect_.remove(nameFalseDefault);
            hashSelect_.remove(nameTrue);
            hashSelect_.remove(nameDefaultTrue);
            hashSelect_.remove(nameTrueDefault);
            hashSelect_.remove(nameDefault);
            hashSelect_.remove(nameNull);
            hashSelect_.remove(name);
            hashInstructions_.remove(nameFalse);
            hashInstructions_.remove(nameDefaultFalse);
            hashInstructions_.remove(nameFalseDefault);
            hashInstructions_.remove(nameTrue);
            hashInstructions_.remove(nameDefaultTrue);
            hashInstructions_.remove(nameTrueDefault);
            hashInstructions_.remove(nameDefault);
            hashInstructions_.remove(nameNull);
            hashInstructions_.remove(name);
            returnBoolean = true;
        }
        return returnBoolean;
    }
}
