/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    Tools.java
 *   Content: Bg Tools Source File
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.tool;

import com.google.gson.GsonBuilder;
import com.ibm.icu.impl.locale.XCldrStub;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Tools {

    /**
     * 输出红色字符
     * @param s 要输出的字符
     */
    public static void Red_Println(String s) {
        System.out.printf("%s%s%s\n", "\033[31m", s, "\033[0m");
    }

    /**
     * 输出绿色字符
     * @param s 要输出的字符
     */
    public static void Green_Println(String s) {
        System.out.printf("%s%s%s\n", "\033[32m", s, "\033[0m");
    }

    /**
     * 读取jar内文件
     * @param path 路径
     * @return 返回文件的文本
     * @throws Exception IO
     */
    public String Read_From_Jar(String path) throws Exception{
        var s1 = this.getClass().getResource(path).openStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(s1));

        StringBuilder buffer = new StringBuilder();

        String line = null;
        while ((line = br.readLine()) != null) {
            buffer.append(line);
            buffer.append('\n');
        }

        br.close();
        return buffer.toString();
    }

    /**
     * 制作路径
     * @param p 访问路径列表
     * @return 用目录访问符号链接后的字符串
     */
    public static String Make_File_Path(String ...p){
        return XCldrStub.Joiner.on(File.separator).join(Arrays.asList(p));
    }

    /**
     * 创建目录
     * @param p 路径
     * @return 是否创建成功
     */
    public static boolean Mkdirs(String p){
        File f =new File(p);
        return f.mkdirs();
    }

    /**
     * 读取文件所有内容
     * @param f 文件
     * @return 文件内容
     * @throws Exception IO异常
     */
    public static String Read_All_Text(String f) throws Exception {
        BufferedReader bf = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(
                                new File(f))
                        , StandardCharsets.UTF_8));
        List<String> MarkList = bf.lines().collect(Collectors.toList());
        String Str = XCldrStub.Joiner.on("\n").join(MarkList);
        bf.close();
        return Str;
    }

    /**
     * 写入文件，路径不存在则创建
     * @param f      要写入的文件的路径
     * @param Source 要写入的字符串
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void Write_All_Text(String f, String Source) throws Exception{
            //文件不存在则创建，包括父目录
            if (!(new File(f).exists())) {
                new File(new File(f).toString().substring(0,
                        new File(f).toString().length() - new File(f).getName().length()
                )).mkdirs();
                new File(f).createNewFile();
            }
            BufferedWriter bw =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(
                                            new File(f)),
                                    StandardCharsets.UTF_8));
            bw.write(Source);
            bw.close();
    }

    /**
     * 获取Json字符串
     * @param obj 要获取Json的对象
     * @return Json
     */
    public static String ObjectTOJson(Object obj){
        GsonBuilder gson = new GsonBuilder().
                serializeNulls()//不忽略null
                .setPrettyPrinting();//设置输出格式化
        return gson.create().toJson(obj);
    }

    /**
     * Json转换为对象
     * @param Json Json字符串
     * @param obj 对象.GetClass()
     * @param <T> 对象的类
     * @return 对象
     */
    public static <T> T JsonToObject(String Json, Class<T> obj){
        GsonBuilder gson = new GsonBuilder().
                serializeNulls()//不忽略null
                .setPrettyPrinting();//设置输出格式化
        return gson.create().fromJson(Json,obj);
    }

    /**
     * 将String转换为field所属类型
     *
     * @param Source 要转换的String
     * @param field  field
     * @return 转换后的类型
     * @throws Exception 未知类型
     */
    public static Object GetFieldValue(String Source, Field field) throws Exception {
        String TypeStr = field.getGenericType().toString();

        switch (TypeStr) {
            case "class java.lang.String" :return Source;
            case "boolean" :return Boolean.parseBoolean(Source);
            case "int" :return Integer.parseInt(Source);
            case "double" :return Double.parseDouble(Source);
            case "float" :return Float.parseFloat(Source);
            case "short" :return Short.parseShort(Source);
            case "long" :return Long.parseLong(Source);
            case "byte" :return Byte.parseByte(Source);
            default: throw new Exception("Unknown Type");
        }
    }

    /**
     * 通用解析反射赋值函数
     * @param Cls 类类型
     * @param obj 实例
     * @param name 名称
     * @param value 要设置的值
     * @throws Exception 解析异常
     */
    public static void ReflectionAssignment(Class<?> Cls,Object obj,String name,String value) throws Exception{
            Field f = Cls.getField(name);
            f.set(obj, Tools.GetFieldValue(value,f));
    }

    /**
     * 获取路径下所有文件，包括子目录下的文件
     * @param path 路径
     * @return 文件列表
     */
    public static String[] GetFileTree(String path){
        LinkedList<String> list = new LinkedList<>();

        File f = new File(path);
        if(f.listFiles() != null) {
            for (var fs :
                    f.listFiles()) {

                //是文件
                if (fs.isFile()) {
                    list.add(fs.getPath());
                }
                //是目录
                else if (fs.isDirectory()) {
                    list.addAll(Arrays.asList(GetFileTree(fs.getPath())));
                }
            }
            return list.toArray(new String[0]);
        }
        else
            return new String[0];
    }

    /**
     * 判断source和out的修改时间
     * @param source 源文件
     * @param out 产物 可以不存在
     * @return 如果out不存在或者source晚于out修改返回true，其他返回false
     */
    public static boolean IsChange(String source,String out){
        if(!new File(out).exists()) return true;

        return new File(source).lastModified() > new File(out).lastModified();
    }

}
