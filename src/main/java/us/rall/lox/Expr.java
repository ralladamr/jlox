package us.rall.lox;

import java.util.List;

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
         * Visit an assignment expression.
         *
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitAssignExpr(Assign expr);

        /**
         * Visit a binary expression.
         *
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitBinaryExpr(Binary expr);

        /**
         * Visit a call expression.
         *
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitCallExpr(Call expr);

        /**
         * Visit a get expression.
         *
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitGetExpr(Get expr);

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
         * Visit a logical expression.
         *
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitLogicalExpr(Logical expr);

        /**
         * Visit a set expression.
         *
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitSetExpr(Set expr);

        /**
         * Visit a super expression.
         *
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitSuperExpr(Super expr);

        /**
         * Visit a this expression.
         *
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitThisExpr(This expr);

        /**
         * Visit a unary expression.
         *
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitUnaryExpr(Unary expr);

        /**
         * Visit a variable expression.
         *
         * @param expr The expression to visit.
         * @return A representation of the expression.
         */
        T visitVariableExpr(Variable expr);
    }

    /**
     * An assignment expression.
     */
    static class Assign extends Expr {
        private final Token name;
        private final Expr value;

        Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitAssignExpr(this);
        }

        public Token getName() {
            return name;
        }

        public Expr getValue() {
            return value;
        }
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
     * A call expression.
     */
    static class Call extends Expr {
        private final Expr callee;
        private final Token paren;
        private final List<Expr> arguments;

        Call(Expr callee, Token paren, List<Expr> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitCallExpr(this);
        }

        public Expr getCallee() {
            return callee;
        }

        public Token getParen() {
            return paren;
        }

        public List<Expr> getArguments() {
            return arguments;
        }
    }

    /**
     * A get expression.
     */
    static class Get extends Expr {
        private final Expr object;
        private final Token name;

        Get(Expr object, Token name) {
            this.object = object;
            this.name = name;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitGetExpr(this);
        }

        public Expr getObject() {
            return object;
        }

        public Token getName() {
            return name;
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
     * A logical expression.
     */
    static class Logical extends Expr {
        private final Expr left;
        private final Token operator;
        private final Expr right;

        Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitLogicalExpr(this);
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
     * A set expression.
     */
    static class Set extends Expr {
        private final Expr object;
        private final Token name;
        private final Expr value;

        Set(Expr object, Token name, Expr value) {
            this.object = object;
            this.name = name;
            this.value = value;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitSetExpr(this);
        }

        public Expr getObject() {
            return object;
        }

        public Token getName() {
            return name;
        }

        public Expr getValue() {
            return value;
        }
    }

    /**
     * A super expression.
     */
    static class Super extends Expr {
        private final Token keyword;
        private final Token method;

        Super(Token keyword, Token method) {
            this.keyword = keyword;
            this.method = method;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitSuperExpr(this);
        }

        public Token getKeyword() {
            return keyword;
        }

        public Token getMethod() {
            return method;
        }
    }

    /**
     * A this expression.
     */
    static class This extends Expr {
        private final Token keyword;

        This(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            return visitor.visitThisExpr(this);
        }

        public Token getKeyword() {
            return keyword;
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
