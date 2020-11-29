/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    Scripter.java
 *   Content: Scripter Source File
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.script;

import com.ibm.icu.impl.locale.XCldrStub;
import team.goscps.bg.tool.StringIterator;
import team.goscps.bg.tool.Tools;

import java.util.*;

public class Scripter {
    //文件系统资源
    private final LinkedHashSet<String> Sources = new LinkedHashSet<>();

    /**
     * 添加资源
     * @param Path 文件系统路径
     */
    public void AddSources(String Path){
        Sources.add(Path);
    }

    public String Render(String Text) throws Exception{
        int Rows = 0;
        try {
            String[] Lines = Text.split("(\\r\\n)|(\\r)|(\\n)");

            for (; Rows < Lines.length; Rows++) {
                String s = Lines[Rows];

                int begin = s.indexOf("{{{"), end = s.indexOf("}}}");

                if (begin != -1 && end != -1) {
                    // }}} 在 {{{之前
                    if (end < begin) {
                        throw new Exception("`}}}` Unexpected Token");
                    }

                    s = s.replace(s.substring(begin, end+3)
                            , Parse(s.substring(begin + 3, end).trim()));

                    Lines[Rows] = s;
                }
            }

            return XCldrStub.Joiner.on("\n").join(Arrays.asList(Lines));
        }
        catch(Exception err){
            Tools.Red_Println("Error At " + (Rows+1));
            throw err;
        }
    }

    /*----------  Parser储存区  ----------*/
    //变量类型
    private enum VariableType{
        Number,
        Double,
        String,
        Array,
        Null
    }

    //变量类
    private class Variable{
        public double aDouble = 0.0;
        public String string = "";
        public Variable[] array;
        public long number = 0;

        public String GetString(){
            switch(type){
                case Null:
                    return "Null";
                case Double:
                    return Double.toString(aDouble);
                case Number:
                    return Long.toString(number);
                case String:
                    return string;
                case Array:
                    StringBuilder builder = new StringBuilder();
                    for(var v:array){
                        builder.append(v.GetString()).append(" ");
                    }
                    return builder.toString();
            }
            return "";
        }

        public Object GetObject(){
            switch(type){
                case Null:
                    return "Null";
                case Double:
                    return aDouble;
                case Number:
                    return number;
                case String:
                    return string;
                case Array:
                    return array;
            }
            return null;
        }

        public Object IndexOfObject(int index) throws Exception{
            return Index(index).GetObject();
        }

        public Variable Index(int index) throws Exception{
            if(this.type != VariableType.Array){
                throw new Exception("Use Index For Not-Array Type");
            }
            return array[index];
        }

        VariableType type = VariableType.Null;
    }

    //局部变量
    private final LinkedHashMap<String,Variable> PartVariable = new LinkedHashMap<>(8);
    private final LinkedHashMap<String,Variable> ForList = new LinkedHashMap<>(4);
    private final ArrayList<Long> Count = new ArrayList<>(4);

    //字节流计数器
    private StringIterator it;

    /*----------  储存区结束  ----------*/

    /**
     * 获取变量
     * @param Name 变量名称
     * @return 变量字符串
     * @throws Exception 找不到变量
     */
    private String GetValueString(String Name) throws Exception{
        //For变量 > 局部变量 > 全局变量
        var f = ForList.get(Name);
        if(f != null){
            return f.GetString();
        }

        f = PartVariable.get(Name);
        if(f != null){
            return f.GetString();
        }

        var s = ScriptConfig.EnvironmentalVariable.get(Name);
        if(!s.isEmpty()){
            return s;
        }

        throw new Exception(String.format("Not Found Name:%s",Name));
    }

    /**
     * 获取变量
     * @param Name 变量名称
     * @return 变量字符串
     * @throws Exception 找不到变量
     */
    private Variable GetValue(String Name) throws Exception{
        //For变量 > 局部变量 > 全局变量
        var f = ForList.get(Name);
        if(f != null){
            return f;
        }

        f = PartVariable.get(Name);
        if(f != null){
            return f;
        }

        var s = ScriptConfig.EnvironmentalVariable.get(Name);
        if(s != null && !s.isEmpty()){
            Variable v = new Variable();
            v.type = VariableType.String;
            v.string = s;
            return v;
        }

        throw new Exception(String.format("Not Found Name:%s",Name));
    }

    /**
     * 解析语句
     * @param Statement 语句字符串
     * @return 解析后的语句替换结果
     * @throws Exception 解析错误
     */
    private String Parse(String Statement) throws Exception{
        it = new StringIterator(Statement);
        it.AfterSpace();

        //解析变量
        if((!it.IsMax()) && it.Get().equals("$")){
            it.Next();
            StringBuilder varName = new StringBuilder();

            while((!it.IsMax()) && (!it.Get().isBlank())){
                varName.append(it.Get());
                it.Next();
            }

            if(varName.toString().isEmpty()){
                throw new Exception("Not Found Var Name:"+ it.Get());
            }

            it.AfterSpace();

            //如果结束，则替换到变量
            if(it.IsMax()){
                return GetValueString(varName.toString());
            }

            //是=，解析为赋值
            if(it.Get().equals("=")){
                it.Next();
                var v = Parse_Assignment();
                PartVariable.put(varName.toString(),v);
                it.AfterSpace();
                if(!it.IsMax()) throw new Exception("Unexpected Token");
                return "";
            }

        }
        //格式化字符串输出
        else if((!it.IsMax()) && it.Get().equals("f")){
            StringBuilder builder = new StringBuilder();
            for(int a=0;a < "format".length();a++){
                builder.append(it.Get());
                it.Next();
            }
            if(!builder.toString().equals("format")) {
                throw new Exception("Unknown Token");
            }

            Statement = Parse_Format();
        }
        //注释
        else if((!it.IsMax()) && it.Get().equals("#")){
            return "";
        }
        else{
            throw new Exception("Unknown Statement");
        }


        return Statement;
    }

    /**
     * 解析字符串，迭代器要求处在开头"位置
     * "Hello World"
     * ↑------------
     * 解析后:
     * "Hello World"
     * -------------↑
     * @return 解析后的字符串
     * @throws Exception 异常
     */
    private String Parse_String() throws Exception{
        if(it.IsMax() || (!it.Get().equals("\""))) throw new Exception("Miss Token:"+ it.Get());
        it.Next();
        StringBuilder builder = new StringBuilder();

        while(true){
            if(it.Get().equals("\"")){
                it.Next();
                return builder.toString();
            }

            else if(it.Get().equals("\\")){
                it.Next();
                switch(it.Get()){
                    case "'":
                        builder.append('\'');
                        it.Next();
                        break;
                    case "\"":
                        builder.append('\"');
                        it.Next();
                        break;
                    case "n":
                        builder.append('\n');
                        it.Next();
                        break;
                    case "t":
                        builder.append('\t');
                        it.Next();
                        break;
                    case "r":
                        builder.append('\r');
                        it.Next();
                        break;
                    case "u":
                        it.Next();
                        builder.append(Parse_Unicode());
                        break;
                }
            }
            else{
                builder.append(it.Get());
                it.Next();
            }
        }
    }

    /**
     * 解析Unicode
     * 迭代器要求处在Unicode代码位置
     * \uABCD
     * --↑---
     * 解析后
     * \uABCD
     * ------↑
     * @return 解析后的字符串
     * @throws Exception 解析错误
     */
    private String Parse_Unicode() throws Exception{
        StringBuilder builder = new StringBuilder();
        for(int a=0;a < 4;a++){

            if(it.IsMax()){
                throw new Exception("miss token:"+ it.Get());
            }

            builder.append(it.Get());
            it.Next();
        }
        char singleChar = (char) Integer.parseInt(builder.toString(), 16);
        return String.valueOf(singleChar);
    }

    /**
     * 解析赋值
     * @return 变量
     * $a = "Hello World"
     * -----↑------------
     * 解析后
     * $a = "Hello World"
     * ------------------↑
     * @throws Exception 解析错误
     */
    private Variable Parse_Assignment() throws Exception{
        Variable v = new Variable();

        if(it.IsMax()) throw new Exception("Not Found Token:"+ it.Get());

        it.AfterSpace();

        /*字符串*/
        if(it.Get().equals("\"")){
            v.type = VariableType.String;
            v.string = Parse_String();
        }
        /*数组*/
        else if(it.Get().equals("[")){
            it.Next();
            ArrayList<Variable> arrayList = new ArrayList<>();

            while(true){
                arrayList.add(Parse_Assignment());


                if(it.Get().equals("]")){
                    //跳过] 结束
                    it.Next();
                    break;
                }
                else if(!it.Get().equals(",")){
                    throw new Exception("Miss Token:" + it.Get());
                }
                //跳过 ,
                it.Next();
            }
            v.type = VariableType.Array;

            Variable[] buf = new Variable[arrayList.size()];
            v.array = arrayList.toArray(buf);
        }
        /*数字*/
        else if(Character.isDigit(it.Get().charAt(0)) || it.Get().equals(".")){

            //读取数字
            StringBuilder builder = new StringBuilder();
            while(true){
                if(Character.isDigit(it.Get().charAt(0)) || it.Get().equals(".")) {
                    builder.append(it.Get());
                    it.Next();
                }
                else{
                    break;
                }
            }

            //对于数字
            if(builder.toString().matches("[0-9]+")){
                v.type = VariableType.Number;
                v.number = Long.parseLong(builder.toString());
            }
            //对于浮点数
            else if(builder.toString().matches("[0-9]+\\.[0-9]+")){
                v.type = VariableType.Double;
                v.aDouble = Double.parseDouble(builder.toString());
            }
            else{
                throw new Exception("Error Value:"+ it.Get());
            }
        }
        else{
            throw new Exception("Error Value:"+ it.Get());
        }

        return v;
    }

    /**
     * 解析变量
     * 排除 . $ # 三个字符
     * $Hello_World.1
     * -↑------------
     * $Hello_World.1
     * ------------↑-
     * @return 变量名称
     * @throws Exception 解析错误
     */
    private String Parse_Var() throws Exception{
        StringBuilder builder = new StringBuilder();

        while(!it.IsMax()){
            if(it.Get().equals(".")
            || it.Get().equals("$")
            || it.Get().equals("#")
            || Character.isSpaceChar(it.Get().charAt(0))){
                break;
            }
            else{
                builder.append(it.Get());
            }
            it.Next();
        }

        if(builder.toString().isEmpty()) throw new Exception("Unexpected Var Name:" + builder.toString());
        return builder.toString();
    }

    /**
     * 解析可能是数组的变量
     * $array.1
     * ------↑-
     * $array.1
     * --------↑
     * @return 索引
     * @throws Exception 解析错误
     */
    private int Parse_Var_Array() throws Exception {
        if (!it.Get().equals(".")) {
            throw new Exception("Unexpected Token");
        }
        it.Next();

        StringBuilder builder = new StringBuilder();
        while (!it.IsMax()) {
            if (Character.isDigit(it.Get().charAt(0))) {
                builder.append(it.Get());
                it.Next();
            } else break;
        }

        if(builder.toString().isEmpty()) throw new Exception("Unexpected Index");
        else return Integer.parseInt(builder.toString());
    }

    /**
     * 解析format
     * format "%s %s" array.1 string
     * ------↑----------------------
     * format "%s %s" array.1 string
     * -----------------------------↑
     * @return format后的字符串
     * @throws Exception 解析错误
     */
    private String Parse_Format() throws Exception{
        it.AfterSpace();

        if(!it.Get().equals("\"")){
            throw new Exception("Miss Format String");
        }
        var f = Parse_String();

        it.AfterSpace();

        ArrayList<Object> ForMatList = new ArrayList<>();

        //解析参数
        while(!it.IsMax()){
            Object obj = null;

            if(it.Get().equals("$")){
                it.Next();
            }
            else{
                throw new Exception("Not Var");
            }

            String var = Parse_Var();
            if((!it.IsMax()) && it.Get().equals(".")) {
                Variable v = GetValue(var).Index(Parse_Var_Array());

                //多维数组
                while((!it.IsMax()) && it.Get().equals(".")) {
                    v = v.Index(Parse_Var_Array());
                }
                obj = v.GetObject();
            }
            else {
                obj = GetValue(var).GetObject();
            }

            ForMatList.add(obj);

            it.AfterSpace();
        }

        return String.format(f,ForMatList.toArray());
    }

}
