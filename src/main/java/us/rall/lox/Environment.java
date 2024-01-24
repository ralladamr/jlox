package us.rall.lox;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Lox environment.
 */
public class Environment {
    private final Map<String, Object> values = new HashMap<>();

    /**
     * Get a variable by name.
     *
     * @param name The name of the variable.
     * @return The value of the variable.
     */
    Object get(Token name) {
        String lexeme = name.lexeme();
        if (values.containsKey(lexeme)) {
            return values.get(lexeme);
        }
        throw new RuntimeError(name, "Undefined variable '%s'.".formatted(lexeme));
    }

    /**
     * Define a variable.
     *
     * @param name  The name of the variable.
     * @param value The value of the variable.
     */
    void define(String name, Object value) {
        values.put(name, value);
    }
}
