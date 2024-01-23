package us.rall.lox;

/**
 * Represents a Lox token.
 *
 * @param type    The type of the token.
 * @param lexeme  The parsed lexeme.
 * @param literal The value of the token.
 * @param line    The source line of the token.
 */
public record Token(TokenType type, String lexeme, Object literal, int line) {
    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
