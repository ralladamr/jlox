package us.rall.lox;

import java.util.List;

/**
 * An interface for callable types in Lox.
 */
interface LoxCallable {
    /**
     * Get the arity of the function.
     *
     * @return The arity of the function.
     */
    int arity();

    /**
     * Call the function or class.
     *
     * @param interpreter The Lox interpreter.
     * @param arguments   The arguments to the function call.
     * @return The results of the function call.
     */
    Object call(Interpreter interpreter, List<Object> arguments);
}
