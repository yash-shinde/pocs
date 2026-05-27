package toyvm.bytecode;

import java.util.ArrayList;
import java.util.List;

public final class Program {
    private final List<Instruction> instructions = new ArrayList<>();

    public void add(Instruction instruction) {
        instructions.add(instruction);
    }

    public Instruction get(int index) {
        return instructions.get(index);
    }

    public int size() {
        return instructions.size();
    }
}
