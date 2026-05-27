package toyvm.vm;

public enum NullValue implements Value {
    INSTANCE;

    @Override
    public boolean isTruthy() {
        return false;
    }

    @Override
    public String asDisplayString() {
        return "null";
    }
}
