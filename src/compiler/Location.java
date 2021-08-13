package compiler;


import org.antlr.v4.runtime.Token;

public class Location {
    public static Location defaultLoc = new Location(-1,0);
    public int lineNum;
    public int colNum;

    public Location(int lineNum, int colNum) {
        this.lineNum = lineNum;
        this.colNum = colNum;
    }

    public Location (Token token)
    {
        lineNum = token.getLine()+1;
        colNum = token.getCharPositionInLine()+1;
    }
}
