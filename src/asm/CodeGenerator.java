package asm;

import asm.code.AsmFactory;
import common.Register;
import common.symbol.Function;
import ir.IRs;
import ir.code.IR;

import static asm.Utils.write;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CodeGenerator {
    Map<Register, Set<IName>> regDesc = new HashMap<>();
    Map<IName, Set<IAddress>> addrDesc = new HashMap<>();
    RegisterAllocator allocator;
    IRs irs;



    public CodeGenerator(IRs irs) {
        this.irs = irs;
        allocator = new RegisterAllocator(irs);
    }


    public void genCode() {
        for (IR ir : irs) {
            if (ir.isStartOfFunction()) {
                Function function = (Function) ir.label;

            }
        }
    }

    private void genFuncHead(Function function) {
        write(function.name + ":");
        if (function.existCall) {
            write("\tpush {fp, lr}");
            write("\tadd fp, sp, #4");
        } else {
            write("\tpush {fp}");
            write("\tadd fp, sp, #0");
        }
        write("");
    }






}
