package toyvm.vm;

public record IntValue(int value) implements Value {
    @Override
    public String asDisplayString() {
        return Integer.toString(value);
    }
}
