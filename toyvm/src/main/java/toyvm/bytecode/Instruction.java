package toyvm.bytecode;

public record Instruction(OpCode opCode, int intOperand, String stringOperand) {
    public Instruction(OpCode opCode, int intOperand) {
        this(opCode, intOperand, null);
    }

    public Instruction(OpCode opCode, String stringOperand) {
        this(opCode, 0, stringOperand);
    }

    public static Instruction of(OpCode opCode) {
        return new Instruction(opCode, 0, null);
    }
}
