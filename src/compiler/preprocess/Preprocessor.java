package compiler.preprocess;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;

public class Preprocessor {
    public static String preprocess(BufferedReader reader)
    {
        StringBuilder builder = new StringBuilder();
        int lineNum = 0;
        try{
        String s;
        while((s = reader.readLine())!=null){//使用readLine方法，一次读一行
            lineNum++;
            s = s.replaceAll("starttime\\(\\)",String.format("_sysy_starttime(%d)",lineNum));
            s = s.replaceAll("stoptime\\(\\)",String.format("_sysy_stoptime(%d)",lineNum));
            s = s.replace("_SYSY_N","1024");
            builder.append(s).append("\n");
        }
        reader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return builder.toString();
    }
}
