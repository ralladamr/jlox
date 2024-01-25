package us.rall.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment closure;
    private final Boolean isInitializer;

    LoxFunction(Stmt.Function declaration, Environment closure, Boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
    }

    LoxFunction bind(LoxInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new LoxFunction(declaration, environment, isInitializer);
    }

    @Override
    public int arity() {
        return declaration.getParams().size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < arity(); i++) {
            environment.define(declaration.getParams().get(i).lexeme(), arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.getBody(), environment);
        } catch (Return returnValue) {
            if (isInitializer) {
                return closure.getAt(0, "this");
            }
            return returnValue.getValue();
        }
        if (isInitializer) {
            return closure.getAt(0, "this");
        }
        return null;
    }

    @Override
    public String toString() {
        return "<fn %s>".formatted(declaration.getName().lexeme());
    }
}
