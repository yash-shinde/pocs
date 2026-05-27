package toyvm.vm;

public record StringValue(String value) implements Value {
    @Override
    public String asDisplayString() {
        return value;
    }
}
