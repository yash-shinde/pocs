package toyvm.vm;

import toyvm.bytecode.Instruction;
import toyvm.bytecode.OpCode;
import toyvm.bytecode.Program;

import java.util.ArrayDeque;
import java.util.Deque;

public final class VirtualMachine {
    private final Deque<Value> stack = new ArrayDeque<>();
    private final Frame frame = new Frame();

    public void execute(Program program) {
        int ip = 0;
        while (ip < program.size()) {
            Instruction instruction = program.get(ip);
            switch (instruction.opCode()) {
                case PUSH_INT -> stack.push(new IntValue(instruction.intOperand()));
                case PUSH_STRING -> stack.push(new StringValue(instruction.stringOperand()));
                case STORE_LOCAL -> frame.storeLocal(instruction.stringOperand(), stack.pop());
                case LOAD_LOCAL -> stack.push(frame.loadLocal(instruction.stringOperand()));
                case ADD -> {
                    int b = ((IntValue) stack.pop()).value();
                    int a = ((IntValue) stack.pop()).value();
                    stack.push(new IntValue(a + b));
                }
                case COMPARE_LT -> {
                    int b = ((IntValue) stack.pop()).value();
                    int a = ((IntValue) stack.pop()).value();
                    stack.push(new BoolValue(a < b));
                }
                case COMPARE_EQ -> {
                    Value b = stack.pop();
                    Value a = stack.pop();
                    stack.push(new BoolValue(a.asDisplayString().equals(b.asDisplayString())));
                }
                case JUMP -> {
                    ip = instruction.intOperand();
                    continue;
                }
                case JUMP_IF_FALSE -> {
                    Value condition = stack.pop();
                    if (!condition.isTruthy()) {
                        ip = instruction.intOperand();
                        continue;
                    }
                }
                case PRINT -> System.out.println(stack.pop().asDisplayString());
                case HALT -> {
                    return;
                }
            }
            ip++;
        }
    }
}
