package compiler.asm;

import compiler.genir.code.AddressOrData;
import compiler.genir.code.InterRepresent;

import java.util.Map;
import java.util.function.Function;

public abstract class RegGetter {
     public abstract Reg getRegOfAddress(InterRepresent ir, AddressOrData address);
     public abstract Map<AddressRWInfo, Reg> getMapOfIR(InterRepresent ir);
     protected abstract boolean isFreeReg(Reg reg);

     /**
      * 获取空闲的寄存器
      * @param index 第几个空闲的寄存器
      */
     private Reg getFreeReg(int index) {
          int i = 0;
          for (Reg register : usableRegs) {
               if (isFreeReg(register)){
                    if(i==index)
                         return register;
                    i++;
               }
          }
          return null;
     }

     protected Reg getFreeReg() {
          return getFreeReg(0);
     }

     private Reg[] usableRegs = {
             Regs.R4,
             Regs.R5,
             Regs.R6,
             Regs.R7,
             Regs.R8,
             Regs.R9,
             Regs.R10,
             Regs.R11,
             Regs.R12,
             Regs.R13,
             Regs.R14,
     };

     /**
      * 获取临时寄存器，保证在下条IR前释放
      */
     public Reg getTmpRegister(int index) {
          return getFreeReg(index);
     }

     public Reg getTmpRegister(Function<Reg, Boolean> filter, int index) {
          int i = 0;
          for (Reg register : usableRegs) {
               if (isFreeReg(register) & filter.apply(register)){
                    if(i==index)
                         return register;
                    i++;
               }
          }
          return null;
     }

     /**
      * 获取临时寄存器，保证在下条IR前释放
      */
     public Reg getTmpRegister() {
          return getFreeReg(0);
     }
}
