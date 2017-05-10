package minesweeper.util;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.scene.control.TextFormatter;
    
public class NumberFilter implements UnaryOperator<TextFormatter.Change> {

    private final Pattern pattern;

    public NumberFilter(int maxLength) {
        pattern = Pattern.compile("[0-9]{0,"+maxLength+"}");
    }

    @Override
    public TextFormatter.Change apply(TextFormatter.Change c) {
        String newText = c.getControlNewText();
        if (pattern.matcher(newText).matches()) {
            return  c;
        } else {
            return null;
        }
    }
}
