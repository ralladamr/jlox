package us.rall.lox;

/**
 * A runtime error in the Lox interpreter.
 */
class RuntimeError extends RuntimeException {
    private final Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }

    /**
     * Get the token where the error occurred.
     *
     * @return The error {@link Token}.
     */
    public Token getToken() {
        return token;
    }
}
