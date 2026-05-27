package toyvm.vm;

public sealed interface Value permits IntValue, StringValue, BoolValue, NullValue {
    default boolean isTruthy() {
        return true;
    }

    default String asDisplayString() {
        return toString();
    }
}
