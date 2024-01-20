package us.rall.lox;

/**
 * Represents a Lox token.
 */
public class Token {
    private final TokenType type;
    private final String lexeme;
    private final Object literal;
    private final int line;

    /**
     * Class constructor specifying all parts of token.
     *
     * @param type    The type of the token.
     * @param lexeme  The parsed lexeme.
     * @param literal The value of the token.
     * @param line    The source line of the token.
     */
    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String getLexeme() {
        return lexeme;
    }

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
