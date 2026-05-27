package toyvm;

import toyvm.bytecode.Instruction;
import toyvm.bytecode.OpCode;
import toyvm.bytecode.Program;
import toyvm.vm.VirtualMachine;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Program program = new Program();
        program.add(new Instruction(OpCode.PUSH_INT, 0));
        program.add(new Instruction(OpCode.STORE_LOCAL, "i"));

        program.add(new Instruction(OpCode.LOAD_LOCAL, "i"));        // 2
        program.add(new Instruction(OpCode.PUSH_INT, 3));
        program.add(Instruction.of(OpCode.COMPARE_LT));
        program.add(new Instruction(OpCode.JUMP_IF_FALSE, 13));

        program.add(new Instruction(OpCode.LOAD_LOCAL, "i"));        // 6
        program.add(Instruction.of(OpCode.PRINT));

        program.add(new Instruction(OpCode.LOAD_LOCAL, "i"));        // 8
        program.add(new Instruction(OpCode.PUSH_INT, 1));
        program.add(Instruction.of(OpCode.ADD));
        program.add(new Instruction(OpCode.STORE_LOCAL, "i"));

        program.add(new Instruction(OpCode.JUMP, 2));                  // 12
        program.add(Instruction.of(OpCode.HALT));

        new VirtualMachine().execute(program);
    }
}
