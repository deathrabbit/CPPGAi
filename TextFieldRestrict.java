package CPPGAi;

import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;

import java.util.function.Supplier;

/**
 * TextFieldRestrict
 *
 * Extends the TextField object by restricting the text entered
 * to only integers and to some maximum value.
 * Best used with custom FocusListenerRestrict class.
 *
 * @author Scott Matsumura
 * @version 1.0
 */
public class TextFieldRestrict extends TextField {

    private int maxValue_;
    private Supplier<Integer> supplier_;

    /**
     * Constructor
     *
     * Initial maximum value is set to the maximum possible integer
     * value as default.
     *
     */
    public TextFieldRestrict() {
        maxValue_ = Integer.MAX_VALUE;
        supplier_ = null;
    }

    /**
     * Sets the maximum integer value that can be entered.
     *
     * @param maxValue  The maximum integer value.
     */
    void setMaxValue(final int maxValue) {
        maxValue_ = maxValue;
        supplier_ = null;
    }

    /**
     * Sets the maximum integer value with a supplier method.
     *
     * @param supplier  Supplier interface
     */
    void setMaxValue(final Supplier<Integer> supplier) {
        supplier_ = supplier;
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if (valid(start, end, text)) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text) {
        IndexRange selectionRange = getSelection();
        if (valid(selectionRange.getStart(), selectionRange.getEnd(), text)) {
            super.replaceSelection(text);
        }
    }

    private boolean valid(int start, int end, String text) {
        String attemptedText = getText().substring(0, start) + text + getText().substring(end);
        boolean returnBoolean;
        if (attemptedText.length() == 0) {
            returnBoolean = true;
        } else {
            try {
                if (supplier_ != null) {
                    maxValue_ = supplier_.get();
                }
                returnBoolean = Integer.parseInt(attemptedText) <= maxValue_;
            }
            catch (Exception e) {
                returnBoolean = false;
            }
        }
        return returnBoolean;
    }
}