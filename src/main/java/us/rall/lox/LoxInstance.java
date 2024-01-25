package us.rall.lox;

import java.util.HashMap;
import java.util.Map;

class LoxInstance {
    private final LoxClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    Object get(Token name) {
        String lexeme = name.lexeme();
        if (fields.containsKey(lexeme)) {
            return fields.get(lexeme);
        }
        LoxFunction method = klass.findMethod(lexeme);
        if (method != null) {
            return method.bind(this);
        }
        throw new RuntimeError(name, "Undefined property '%s'.".formatted(lexeme));
    }

    void set(Token name, Object value) {
        fields.put(name.lexeme(), value);
    }

    @Override
    public String toString() {
        return "%s instance".formatted(klass);
    }
}
