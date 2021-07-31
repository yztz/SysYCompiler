package compiler.asm;

import compiler.genir.code.AddressOrData;
import compiler.genir.code.InterRepresent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class RegGetter {
     public abstract Reg getReg(InterRepresent ir, AddressOrData address);
     //public abstract void setReg(InterRepresent ir,AddressOrData address,Reg register);
     public abstract Map<AddressRWInfo, Reg> getMapOfIR(InterRepresent ir);
     protected abstract boolean isFreeReg(Reg reg);
     protected abstract boolean isFreeRegIgnoreCurrentIR(Reg reg);
     public abstract void stepToNextIR();
     /**
      * 获取空闲的寄存器
      * @param index 第几个空闲的寄存器
      */
     private Reg getFreeReg(int index) {
          int i = 0;
          for (Reg register : usableRegs) {
               if (isFreeRegIgnoreCurrentIR(register)){
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

     protected Reg[] usableRegs = {
             Regs.R4,
             Regs.R5,
             Regs.R6,
             Regs.R7,
             Regs.R8,
             Regs.R9,
             Regs.R10,
             Regs.R12,
             Regs.R0,
             Regs.R1,
             Regs.R2,
             Regs.R3
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
               if (isFreeRegIgnoreCurrentIR(register) & filter.apply(register)){
                    if(i==index)
                         return register;
                    i++;
               }
          }
          return null;
     }

     public List<Reg> getUsingReg()
     {
          List<Reg> regs = new ArrayList<>();
          for (int i = 0; i < usableRegs.length; i++) {
               if(!isFreeReg(usableRegs[i]))
                    regs.add(usableRegs[i]);
          }

          return regs;
     }

     /**
      * 获取所有接下来还要使用的IR
      */
     public abstract List<Reg> getUsingRegNext();

     /**
      * 获取临时寄存器，保证在下条IR前释放
      */
     public Reg getTmpRegister() {
          return getFreeReg(0);
     }

     public abstract void releaseAll();
}
