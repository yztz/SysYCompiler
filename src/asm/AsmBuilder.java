package asm;

public class AsmBuilder {
    private final AsmSection building;

    public AsmBuilder() {
        building = new AsmSection();
    }
    public AsmBuilder addDirective(String directive)
    {
        building.add(String.format("\t.%s",directive));
        return this;
    }
    public AsmBuilder addDirective(String directive, String arg)
    {
        building.add(String.format("\t.%s\t%s",directive,arg));
        return this;
    }
    public AsmBuilder addDirective(String directive,String arg1,String arg2)
    {
        building.add(String.format("\t.%s\t%s, %s",directive,arg1,arg2));
        return this;
    }
    public AsmBuilder word(int value)
    {
        return addDirective("word",String.valueOf(value));
    }
    public AsmBuilder label(String label)
    {
        building.add(String.format(".%s:",label));
        return this;
    }
    public AsmBuilder data()
    {
        return addDirective("data");
    }
    public AsmBuilder globl(String ident)
    {
        return addDirective("globl",ident);
    }
    public AsmBuilder align(int size)
    {
        return addDirective("align",String.valueOf(size));
    }
    public AsmBuilder align(int size,byte fillData)
    {
        return addDirective("align",String.valueOf(size),String.valueOf(fillData));
    }
    public AsmBuilder type(String targetLabel,Type type)
    {
        switch (type) {
            case Object:
                return addDirective("type",targetLabel,"%object");
            case Function:
                return addDirective("type",targetLabel,"%function");
        }

        return this;
    }
    public AsmBuilder size(String targetLabel,int size)
    {
        return addDirective("size",targetLabel,String.valueOf(size));
    }

    public AsmSection getBuild() {
        return building;
    }

    public enum Type{
        Object,
        Function
    }
}
