package us.rall.lox;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Lox environment.
 */
public class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

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
        if (enclosing != null) {
            return enclosing.get(name);
        }
        throw new RuntimeError(name, "Undefined variable '%s'.".formatted(lexeme));
    }

    /**
     * Assign a value to a variable.
     *
     * @param name  The name of the variable.
     * @param value The value of the variable.
     */
    void assign(Token name, Object value) {
        String lexeme = name.lexeme();
        if (values.containsKey(lexeme)) {
            values.put(lexeme, value);
            return;
        }
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name, "Undefined variable'%s'.".formatted(lexeme));
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
