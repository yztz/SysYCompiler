package compiler.symboltable.builtin;

import compiler.symboltable.HasInitSymbol;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;

/**
 * 做%运算时要用到，从gcc的汇编里看过来的(事实上并没有用上)
 */
public class ModNum extends HasInitSymbol {
    public static ModNum instance = new ModNum(
            new CommonToken(0),new int[]{1431655766}
    );

    @Override
    public boolean isArray() {
        return false;
    }

    public ModNum(Token symbolToken, int[] initValues) {
        super(symbolToken, initValues);
    }

    @Override
    public int getOffsetByte() {
        return 0;
    }
}
