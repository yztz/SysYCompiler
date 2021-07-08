package antlr;

public class Test extends SysYBaseListener{
    @Override
    public void enterAddSub(SysYParser.AddSubContext ctx) {
        super.enterAddSub(ctx);
        ctx.ADD();
    }
}
