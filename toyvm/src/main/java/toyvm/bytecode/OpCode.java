package toyvm.bytecode;

public enum OpCode {
    PUSH_INT,
    PUSH_STRING,
    ADD,
    COMPARE_LT,
    COMPARE_EQ,
    STORE_LOCAL,
    LOAD_LOCAL,
    JUMP,
    JUMP_IF_FALSE,
    PRINT,
    HALT
}
