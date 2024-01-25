package us.rall.lox;

class Return extends RuntimeException {
    private final Object value;

    Return(Object value) {
        super(null, null, false, false);
        this.value = value;
    }

    Object getValue() {
        return value;
    }
}
