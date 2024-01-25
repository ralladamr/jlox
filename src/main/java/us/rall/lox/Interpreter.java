package us.rall.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A tree-walk interpreter for Lox.
 */
class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private final Environment globals = new Environment();
    private final Map<Expr, Integer> locals = new HashMap<>();
    private Environment environment = globals;

    Interpreter() {
        globals.define("clock", new LoxCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
    }

    private static void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operator must be a number.");
    }

    private static void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private static boolean isEqual(Object left, Object right) {
        if (left == null) {
            return right == null;
        }
        return left.equals(right);
    }

    private static boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean) {
            return (boolean) object;
        }
        return true;
    }

    private static String stringify(Object object) {
        if (object == null) {
            return "nil";
        }
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                return text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }

    /**
     * Interpret Lox statements.
     *
     * @param statements The statements to interpret.
     */
    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError e) {
            Lox.runtimeError(e);
        }
    }

    /**
     * Resolve Lox expression.
     *
     * @param expr  The expression to resolve.
     * @param depth The scope depth of the expression.
     */
    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.getValue());
        Token name = expr.getName();
        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, name, value);
        } else {
            globals.assign(name, value);
        }
        return value;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.getLeft());
        Object right = evaluate(expr.getRight());
        Token operator = expr.getOperator();
        return switch (operator.type()) {
            case PLUS -> {
                if (left instanceof Double && right instanceof Double) {
                    yield (double) left + (double) right;
                } else if (left instanceof String && right instanceof String) {
                    yield "%s%s".formatted(left, right);
                } else {
                    throw new RuntimeError(operator, "Operands must be two numbers or two strings.");
                }
            }
            case MINUS -> {
                checkNumberOperands(operator, left, right);
                yield (double) left - (double) right;
            }
            case STAR -> {
                checkNumberOperands(operator, left, right);
                yield (double) left * (double) right;
            }
            case SLASH -> {
                checkNumberOperands(operator, left, right);
                yield (double) left / (double) right;
            }
            case GREATER -> {
                checkNumberOperands(operator, left, right);
                yield (double) left > (double) right;
            }
            case GREATER_EQUAL -> {
                checkNumberOperands(operator, left, right);
                yield (double) left >= (double) right;
            }
            case LESS -> {
                checkNumberOperands(operator, left, right);
                yield (double) left < (double) right;
            }
            case LESS_EQUAL -> {
                checkNumberOperands(operator, left, right);
                yield (double) left <= (double) right;
            }
            case BANG_EQUAL -> !isEqual(left, right);
            case EQUAL_EQUAL -> isEqual(left, right);
            default -> null;
        };
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.getCallee());
        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.getArguments()) {
            arguments.add(evaluate(argument));
        }
        if (!(callee instanceof LoxCallable function)) {
            throw new RuntimeError(expr.getParen(), "Can only call functions and classes.");
        }
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.getParen(),
                    "Expected %s arguments but got %s.".formatted(function.arity(), arguments.size()));
        }
        return function.call(this, arguments);
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.getObject());
        Token name = expr.getName();
        if (object instanceof LoxInstance) {
            return ((LoxInstance) object).get(name);
        }
        throw new RuntimeError(name, "Only instances have properties.");
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.getExpression());
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.getValue();
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.getLeft());
        if (expr.getOperator().type() == TokenType.OR) {
            if (isTruthy(left)) {
                return left;
            }
        } else if (!isTruthy(left)) {
            return left;
        }
        return evaluate(expr.getRight());
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object = evaluate(expr.getObject());
        Token name = expr.getName();
        if (!(object instanceof LoxInstance)) {
            throw new RuntimeError(name, "Only instances have fields.");
        }
        Object value = evaluate(expr.getValue());
        ((LoxInstance) object).set(name, value);
        return value;
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.getKeyword(), expr);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.getRight());
        Token operator = expr.getOperator();
        return switch (operator.type()) {
            case MINUS -> {
                checkNumberOperand(operator, right);
                yield -(double) right;
            }
            case BANG -> !isTruthy(right);
            default -> null;
        };
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.getName(), expr);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme());
        }
        return globals.get(name);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.getStatements(), new Environment(environment));
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Token stmtName = stmt.getName();
        environment.define(stmtName.lexeme(), null);
        Map<String, LoxFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.getMethods()) {
            String methodLexeme = method.getName().lexeme();
            LoxFunction function = new LoxFunction(method, environment, methodLexeme.equals("init"));
            methods.put(methodLexeme, function);
        }
        LoxClass klass = new LoxClass(stmtName.lexeme(), methods);
        environment.assign(stmtName, klass);
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        LoxFunction function = new LoxFunction(stmt, environment, false);
        environment.define(stmt.getName().lexeme(), function);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.getCondition()))) {
            execute(stmt.getThenBranch());
        } else if (stmt.getElseBranch() != null) {
            execute(stmt.getElseBranch());
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.getExpression());
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.getValue() != null) {
            value = evaluate(stmt.getValue());
        }
        throw new Return(value);
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.getCondition()))) {
            execute(stmt.getBody());
        }
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        Expr initializer = stmt.getInitializer();
        if (initializer != null) {
            value = evaluate(initializer);
        }
        environment.define(stmt.getName().lexeme(), value);
        return null;
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt stmt : statements) {
                execute(stmt);
            }
        } finally {
            this.environment = previous;
        }
    }
}
