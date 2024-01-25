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
         * Visit a function statement.
         *
         * @param stmt The statement to visit.
         * @return A representation of the statement.
         */
        T visitFunctionStmt(Function stmt);

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
         * Visit a return statement.
         *
         * @param stmt The statement to visit.
         * @return A representation of the statement.
         */
        T visitReturnStmt(Return stmt);

        /**
         * Visit a while statement.
         *
         * @param stmt The statement to visit.
         * @return A representation of the statement.
         */
        T visitWhileStmt(While stmt);

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
     * Represents a function statement.
     */
    static class Function extends Stmt {
        private final Token name;
        private final List<Token> params;
        private final List<Stmt> body;

        Function(Token name, List<Token> params, List<Stmt> body) {
            this.name = name;
            this.params = params;
            this.body = body;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitFunctionStmt(this);
        }

        public Token getName() {
            return name;
        }

        public List<Token> getParams() {
            return params;
        }

        public List<Stmt> getBody() {
            return body;
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
     * Represents a return statement.
     */
    static class Return extends Stmt {
        private final Token keyword;
        private final Expr value;

        Return(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitReturnStmt(this);
        }

        public Token getKeyword() {
            return keyword;
        }

        public Expr getValue() {
            return value;
        }
    }

    /**
     * Represents a while statement.
     */
    static class While extends Stmt {
        private final Expr condition;
        private final Stmt body;

        While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitWhileStmt(this);
        }

        public Expr getCondition() {
            return condition;
        }

        public Stmt getBody() {
            return body;
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
