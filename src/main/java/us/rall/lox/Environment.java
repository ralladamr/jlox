package us.rall.lox;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Lox environment.
 */
public class Environment {
    private final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Environment getEnclosing() {
        return enclosing;
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
     * Assign a value to a variable in an ancestor environment.
     *
     * @param distance The number of ancestor environments.
     * @param name     The name of the variable.
     * @param value    The value of the variable.
     */
    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme(), value);
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

    /**
     * Get a variable by name and number of ancestor environments.
     *
     * @param distance The number of ancestor environments.
     * @param name     The name of the variable.
     * @return The value of the variable.
     */
    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    private Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.getEnclosing();
        }
        return environment;
    }
}
