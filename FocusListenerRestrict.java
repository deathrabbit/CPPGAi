package CPPGAi;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

/**
 * FocusListenerRestrict
 *
 * This FocusListener assumes the text in the TextField is an integer.
 * Highlights text in a TextField when on focus.
 * Replaces the text with a minimum value when focus goes out.
 * Best used with the custom TextFieldRestrict class.
 *
 * @author Scott Matsumura
 * @version 1.0
 */
public class FocusListenerRestrict implements ChangeListener<Boolean> {

    private final TextField textField_;
    private final int minValue_;
    private final int defaultValue_;
    private final Consumer<Integer> consumer_;

    /**
     * Constructor
     *
     * @param textField     TextField object in which this class will be implemented on.
     * @param minValue      The minimum integer value that will replace the TextField value
     *                          if lower.
     * @param defaultValue  The integer value that will replace the TextField value if
     *                          empty.
     */
    FocusListenerRestrict(TextField textField, int minValue, int defaultValue) {
        textField_ = textField;
        minValue_ = minValue;
        defaultValue_ = defaultValue;
        consumer_ = null;
    }

    /**
     * Constructor with consumer method that passes in the updated value
     *
     *
     * @param textField         TextField object in which this class will be implemented on.
     * @param minValue          The minimum integer value that will replace the TextField value
     *                              if lower or empty.
     * @param defaultValue      The integer value that will replace the TextField value if
     *                              empty.
     * @param consumer          Consumer method.
     */
    FocusListenerRestrict(TextField textField, int minValue, int defaultValue, Consumer<Integer> consumer) {
        textField_ = textField;
        minValue_ = minValue;
        defaultValue_ = defaultValue;
        consumer_ = consumer;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (textField_.isFocused() && !textField_.getText().isEmpty()) {
            Platform.runLater(textField_::selectAll);
        } else {
            if (textField_.getText().isEmpty()) {
                textField_.setText(Integer.toString(defaultValue_));
            }
            int currentNum = Integer.parseInt(textField_.getText());
            if (currentNum < minValue_) {
                textField_.setText(Integer.toString(minValue_));
                currentNum = minValue_;
            }
            if (consumer_ != null) {
                consumer_.accept(currentNum);
            }
        }
    }
}
