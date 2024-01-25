package us.rall.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private ClassType currentClass = ClassType.NONE;
    private FunctionType currentFunction = FunctionType.NONE;

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.getValue());
        resolveLocal(expr, expr.getName());
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.getLeft());
        resolve(expr.getRight());
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.getCallee());
        for (Expr arg : expr.getArguments()) {
            resolve(arg);
        }
        return null;
    }

    @Override
    public Void visitGetExpr(Expr.Get expr) {
        resolve(expr.getObject());
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.getExpression());
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.getLeft());
        resolve(expr.getRight());
        return null;
    }

    @Override
    public Void visitSetExpr(Expr.Set expr) {
        resolve(expr.getValue());
        resolve(expr.getObject());
        return null;
    }

    @Override
    public Void visitSuperExpr(Expr.Super expr) {
        Token keyword = expr.getKeyword();
        if (currentClass == ClassType.NONE) {
            Lox.error(keyword, "Can't use 'super' outside of a class.");
        } else if (currentClass != ClassType.SUBCLASS) {
            Lox.error(keyword, "Can't use 'super' in a class with no superclass.");
        }
        resolveLocal(expr, keyword);
        return null;
    }

    @Override
    public Void visitThisExpr(Expr.This expr) {
        Token keyword = expr.getKeyword();
        if (currentClass == ClassType.NONE) {
            Lox.error(keyword, "Can't use 'this' outside of a class.");
        }
        resolveLocal(expr, keyword);
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.getRight());
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        Token name = expr.getName();
        if (!scopes.isEmpty() && (scopes.peek().get(name.lexeme()) == Boolean.FALSE)) {
            Lox.error(name, "Can't read local variable in its own initializer.");
        }
        resolveLocal(expr, name);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.getStatements());
        endScope();
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;
        Token name = stmt.getName();
        declare(name);
        define(name);
        Expr.Variable superclass = stmt.getSuperclass();
        if (superclass != null) {
            if (name.lexeme().equals(superclass.getName().lexeme())) {
                Lox.error(superclass.getName(), "A class can't inherit from itself");
            }
            currentClass = ClassType.SUBCLASS;
            resolve(superclass);
            beginScope();
            scopes.peek().put("super", true);
        }
        beginScope();
        scopes.peek().put("this", true);
        for (Stmt.Function method : stmt.getMethods()) {
            FunctionType type = FunctionType.METHOD;
            if(method.getName().lexeme().equals("init")){
                type = FunctionType.INITIALIZER;
            }
            resolveFunction(method, type);
        }
        endScope();
        if (superclass != null) {
            endScope();
        }
        currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.getName());
        define(stmt.getName());
        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.getCondition());
        resolve(stmt.getThenBranch());
        if (stmt.getElseBranch() != null) {
            resolve(stmt.getElseBranch());
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.getKeyword(), "Can't return from top-level code.");
        }
        if (stmt.getValue() != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                Lox.error(stmt.getKeyword(), "Can't return value from an initializer.");
            }
            resolve(stmt.getValue());
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.getCondition());
        resolve(stmt.getBody());
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Token name = stmt.getName();
        declare(name);
        if (stmt.getInitializer() != null) {
            resolve(stmt.getInitializer());
        }
        define(name);
        return null;
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) {
            return;
        }
        Map<String, Boolean> scope = scopes.peek();
        String lexeme = name.lexeme();
        if (scope.containsKey(lexeme)) {
            Lox.error(name, "Already a variable with this name in this scope.");
        }
        scope.put(lexeme, false);
    }

    private void define(Token name) {
        if (scopes.isEmpty()) {
            return;
        }
        scopes.peek().put(name.lexeme(), true);
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    void resolve(List<Stmt> statements) {
        for (Stmt stmt : statements) {
            resolve(stmt);
        }
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private void resolveFunction(Stmt.Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : function.getParams()) {
            declare(param);
            define(param);
        }
        resolve(function.getBody());
        endScope();
        currentFunction = enclosingFunction;
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme())) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    private void endScope() {
        scopes.pop();
    }

    private enum ClassType {
        NONE,
        CLASS,
        SUBCLASS
    }

    private enum FunctionType {
        NONE,
        FUNCTION,
        INITIALIZER,
        METHOD
    }
}
