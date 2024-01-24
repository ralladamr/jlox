package us.rall.lox;

/**
 * Represents a Lox statement.
 */
abstract class Stmt {
    /**
     * A method for the visitor pattern.
     *
     * @param visitor The visitor object.
     * @param <T>     The type used to represent the statement.
     * @return A representation of the statement.
     */
    abstract <T> T accept(Visitor<T> visitor);

    /**
     * An interface for the visitor pattern.
     *
     * @param <T> The type of output on visiting a statement.
     */
    interface Visitor<T> {
        /**
         * Visit an expression statement.
         *
         * @param stmt The statement to visit.
         * @return A representation of the statement.
         */
        T visitExpressionStmt(Expression stmt);

        /**
         * Visit a print statement.
         *
         * @param stmt The statement to visit.
         * @return A representation of the statement.
         */
        T visitPrintStmt(Print stmt);

        /**
         * Visit a var statment.
         *
         * @param stmt The statement to visit.
         * @return A representation of the statement.
         */
        T visitVarStmt(Var stmt);
    }

    /**
     * Represents a Lox expression statement.
     */
    static class Expression extends Stmt {
        private final Expr expression;

        Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        public Expr getExpression() {
            return expression;
        }
    }

    /**
     * Represents a Lox print statement.
     */
    static class Print extends Stmt {
        private final Expr expression;

        Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitPrintStmt(this);
        }

        public Expr getExpression() {
            return expression;
        }
    }

    /**
     * Represents a variable declaration statement.
     */
    static class Var extends Stmt {
        private final Token name;
        private final Expr initializer;

        Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitVarStmt(this);
        }

        public Token getName() {
            return name;
        }

        public Expr getInitializer() {
            return initializer;
        }
    }
}
