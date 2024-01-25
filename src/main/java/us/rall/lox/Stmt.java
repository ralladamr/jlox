package us.rall.lox;

import java.util.List;

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
         * Visit a block statement.
         *
         * @param stmt The statement to visit.
         * @return A representation of the statement.
         */
        T visitBlockStmt(Block stmt);

        /**
         * Visit an expression statement.
         *
         * @param stmt The statement to visit.
         * @return A representation of the statement.
         */
        T visitExpressionStmt(Expression stmt);

        /**
         * Visit an if statement.
         *
         * @param stmt The statement to visit.
         * @return A representation of the statement.
         */
        T visitIfStmt(If stmt);

        /**
         * Visit a print statement.
         *
         * @param stmt The statement to visit.
         * @return A representation of the statement.
         */
        T visitPrintStmt(Print stmt);

        /**
         * Visit a var statement.
         *
         * @param stmt The statement to visit.
         * @return A representation of the statement.
         */
        T visitVarStmt(Var stmt);
    }

    /**
     * Represents a Lox block statement.
     */
    static class Block extends Stmt {
        private final List<Stmt> statements;

        Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitBlockStmt(this);
        }

        public List<Stmt> getStatements() {
            return statements;
        }
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
     * Represents a Lox if statement.
     */
    static class If extends Stmt {
        private final Expr condition;
        private final Stmt thenBranch;
        private final Stmt elseBranch;

        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitIfStmt(this);
        }

        public Expr getCondition() {
            return condition;
        }

        public Stmt getThenBranch() {
            return thenBranch;
        }

        public Stmt getElseBranch() {
            return elseBranch;
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
