package toyvm.vm;

public record BoolValue(boolean value) implements Value {
    @Override
    public boolean isTruthy() {
        return value;
    }

    @Override
    public String asDisplayString() {
        return Boolean.toString(value);
    }
}
