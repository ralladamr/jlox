package us.rall.lox;

/**
 * Represents a Lox expression.
 */
abstract class Expr {
    /**
     * A method for the visitor pattern.
     *
     * @param visitor The visitor object.
     * @param <T>     The type used to represent the expression.
     * @return A representation of the expression.
     */
    abstract <T> T accept(Visitor<T> visitor);

    /**
     * An interface for the visitor pattern.
     *
     * @param <T> The type of output on visiting an expression.
     */
    interface Visitor<T> {
        /**
         * Visit a binary expression.
         *
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitBinaryExpr(Binary expr);

        /**
         * Visit a grouping expression.
         *
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitGroupingExpr(Grouping expr);

        /**
         * Visit a literal expression.
         *
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitLiteralExpr(Literal expr);

        /**
         * Visit a unary expression.
         *
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitUnaryExpr(Unary expr);

        /**
         * Visit a variable expression.
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitVariableExpr(Variable expr);
    }

    /**
     * A binary expression.
     */
    static class Binary extends Expr {
        private final Expr left;
        private final Token operator;
        private final Expr right;

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        public Expr getLeft() {
            return left;
        }

        public Token getOperator() {
            return operator;
        }

        public Expr getRight() {
            return right;
        }
    }

    /**
     * A grouping expression.
     */
    static class Grouping extends Expr {
        private final Expr expression;

        Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        public Expr getExpression() {
            return expression;
        }
    }

    /**
     * A literal expression.
     */
    static class Literal extends Expr {
        private final Object value;

        Literal(Object value) {
            this.value = value;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        public Object getValue() {
            return value;
        }
    }

    /**
     * A unary expression.
     */
    static class Unary extends Expr {
        private final Token operator;
        private final Expr right;

        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        public Token getOperator() {
            return operator;
        }

        public Expr getRight() {
            return right;
        }
    }

    /**
     * A variable expression.
     */
    static class Variable extends Expr {
        private final Token name;

        Variable(Token name) {
            this.name = name;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitVariableExpr(this);
        }

        public Token getName() {
            return name;
        }
    }
}
