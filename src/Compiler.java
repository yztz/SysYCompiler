import antlr.SysYLexer;
import antlr.SysYParser;
import compiler.Util;
import compiler.asm.AsmGen;
import compiler.genir.SysYIRListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import compiler.symboltable.FuncSymbolTable;
import compiler.symboltable.SymbolScanner;
import compiler.symboltable.SymbolTableHost;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class Compiler {
    public static void main(String[] args)
    {
        /*String className = getClasses("compiler").stream().map(c->c.getName())
                .sorted().collect(Collectors.toList()).get(14);
        System.out.println(className);*/
        String inputFileStr = null;
        String outputFileStr = null;
        boolean optimization = false;
        String operation = null;
        try {
            for (int i = 0, argsLength = args.length; i < argsLength; i++) {
                String arg = args[i];
                switch (arg) {
                    case "-S":
                        operation = arg; //也只有-S吧

                        break;
                    case "-O1":
                        optimization = true;
                        break;
                    case "-O2":
                        optimization = true;
                        break;
                    case "-O3":
                        optimization = true;
                        break;
                    case "-O4":
                        optimization = true;
                        break;
                    case "-o":
                        outputFileStr = args[i+1];
                        i++;
                        break;
                    default:
                        inputFileStr = arg;
                        break;
                }
            }

            if(inputFileStr==null || outputFileStr==null) return;
            SysYParser parser = getParser(inputFileStr);
            ParseTree tree = parser.compUnit();
            ParseTreeWalker walker = new ParseTreeWalker();
            SymbolTableHost symbolTableHost=new SymbolTableHost();
            FuncSymbolTable funcSymbolTable=new FuncSymbolTable();
            prepareSymbol(tree, walker, symbolTableHost, funcSymbolTable);
            SysYIRListener irListener = null;
            try {
                irListener = new SysYIRListener(symbolTableHost, funcSymbolTable);
                walker.walk(irListener, tree);

            }catch (NullPointerException ne)
            {
                Util.printStackAndExit(-8,ne);
            }catch (Exception e)
            {
                Util.printStackAndExit(-9,e);
            }

            System.out.println(irListener.irUnion.toString());


            AsmGen asmGen = new AsmGen(symbolTableHost);
            String result = asmGen.generate(irListener.irUnion);

            FileWriter writer;

            writer = new FileWriter(outputFileStr);
            writer.write("");//清空原文件内容
            writer.write(result);
            writer.flush();
            writer.close();
            //throw new IOException("试一试");
        }catch (Exception e)
        {
            StackTraceElement stackTraceElement = e.getStackTrace()[0];

            List<String> allClass = getClasses("compiler").stream().map(c->c.getName())
                    .sorted().collect(Collectors.toList());
            /*for (Class<?> aClass : compiler) {
                System.out.println(aClass.getName());
            }*/

            for (int i = 0; i < allClass.size(); i++) {
                if (allClass.get(i).equals(stackTraceElement.getClassName())) {
                    System.exit(i);
                }
            }
            //System.err.println(stackTraceElement.getClassName());
            //printStackAndExit(i);
            e.printStackTrace();
            try {
                throw e;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        //System.out.println(result);
    }
    /**
     * 从包package中获取所有的Class
     *
     * @param pack
     * @return
     */
    public static Set<Class<?>> getClasses(String pack) {

        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // System.err.println("file类型的扫描");
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    // System.err.println("jar类型的扫描");
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            // log
                                            // .error("添加用户自定义视图类错误
                                            // 找不到此类的.class文件");
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        // log.error("在扫描用户定义视图时从jar包获取文件出错");
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }
    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive,
                                                        Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
                                                 classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    // classes.add(Class.forName(packageName + '.' +
                    // className));
                    // 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    classes.add(
                            Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    e.printStackTrace();
                }
            }
        }
    }

    private static void prepareSymbol(ParseTree tree, ParseTreeWalker walker, SymbolTableHost symbolTableHost,
                                FuncSymbolTable funcSymbolTable) {
        SymbolScanner scanner = new SymbolScanner(symbolTableHost, funcSymbolTable);
        scanner.scanSymbol(tree);
    }
    private static SysYParser getParser(String fileName)
    {
        CharStream input = null;
        try {
            input = CharStreams.fromFileName(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert input!=null;

        SysYLexer lexer = new SysYLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        return new SysYParser(tokens);
    }
}
