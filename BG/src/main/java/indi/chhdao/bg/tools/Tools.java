//* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// BSD 3-Clause License
//
// Copyright (c) 2020, 彩虹海盗
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// 3. Neither the name of the copyright holder nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

package indi.chhdao.bg.tools;

import com.google.gson.GsonBuilder;
import com.ibm.icu.impl.locale.XCldrStub;
import indi.chhdao.bg.exception.DetailedException;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chhdao
 * @version 0.1.0
 * @since 2020-9-19
 */

public class Tools {

    /***
     * 判断Aims相比于Source是否需要更改
     * @param Source 源文件，必须存在
     * @param Aims 目标文件，可以不存在
     * @return Aims不存在 或者 Source晚于Aims修改 则返回true
     */
    public static boolean NeedChange(String Source,
                                     String Aims) {
        //如果Aims不存在则需要更改
        if (!new File(Aims).exists()) return true;
        //如果Source晚于Aims修改则需要更改
        return new File(Source).lastModified() > new File(Aims).lastModified();
    }

    /**
     * 删除数组的一个元素
     *
     * @param Array       数组
     * @param DeleteIndex 要删除的数组元素索引
     * @param <T>         数组类型
     * @return 返回删除元素后的数组
     */
    public static <T> T[] DeleteArrays(T[] Array, int DeleteIndex) {
        T[] Head = Arrays.copyOf(Array, DeleteIndex);
        T[] End = Arrays.copyOfRange(Array, DeleteIndex + 1, Array.length);
        T[] Aims = Arrays.copyOf(Array, Array.length - 1);
        System.arraycopy(Head, 0, Aims, 0, Head.length);
        System.arraycopy(End, 0, Aims, Head.length, End.length);
        return Aims;
    }

    /**
     * 将String转换为field所属类型
     *
     * @param Source 要转换的String
     * @param field  field
     * @return 转换后的类型
     * @throws Exception 未知类型
     */
    public static Object GetFieldVulan(String Source, Field field) throws Exception {
        String TypeStr = field.getGenericType().toString();

        return switch (TypeStr) {
            case "class java.lang.String" -> Source;
            case "boolean" -> Boolean.parseBoolean(Source);
            case "int" -> Integer.getInteger(Source);
            case "double" -> Double.parseDouble(Source);
            case "float" -> Float.parseFloat(Source);
            case "short" -> Short.parseShort(Source);
            case "long" -> Long.parseLong(Source);
            case "byte" -> Byte.parseByte(Source);
            default -> throw new DetailedException("unknow type", "Param:field", "unknow type:" + TypeStr);
        };
    }

    /**
     * 读取文件所有内容
     *
     * @param f 文件
     * @return 文件内容
     * @throws Exception IO异常
     */
    public static String ReadAllText(String f) throws Exception {
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
     * 写入文件
     *
     * @param f      要写入的文件的路径
     * @param Source 要写入的字符串
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void WriteAllText(String f, String Source){
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取URL所有内容
     *
     * @param Url 统一资源定位器
     * @return URL内容
     * @throws Exception IO异常
     */
    public static String ReadURLAllText(String Url) throws Exception {
        URL url = new URL(Url);
        InputStream is = url.openStream();
        BufferedReader rd =
                new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        List<String> MarkList = rd.lines().collect(Collectors.toList());
        return XCldrStub.Joiner.on("\n").join(MarkList);
    }

    /**
     * 获取目录下所有文件，包括子目录文件
     * @param Path 目录
     * @return 返回文件名称列表
     */
    public static String[] GetFileList(String Path){
        ArrayList<String> List = new ArrayList<>();
        File f = new File(Path);
        var fList = f.listFiles();
        for(var s:fList){
            //是目录，继续添加子目录文件
            if(s.isDirectory())
                List.addAll(Arrays.asList(GetFileList(s.toString())));
            else List.add(s.toString());
        }
        String[] out = new String[List.size()];
        List.toArray(out);
        return out;
    }

    /**
     * 制作文件路径
     * @param s 文件列表
     * @return 在每个文件之间添加File.separator
     */
    public static String MakeFilePath(String ... s){
        return XCldrStub.Joiner.on(File.separator).join(Arrays.asList(s));
    }

    /**
     * 获取Json字符串
     * @param obj 要获取Json的对象
     * @return Json
     */
    public static String GetJson(Object obj){
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
    public static <T> T ClassToJson(String Json, Class<T> obj){
        GsonBuilder gson = new GsonBuilder().
                serializeNulls()//不忽略null
                .setPrettyPrinting();//设置输出格式化
        return gson.create().fromJson(Json,obj);
    }

    /**
     * 更换文件后缀
     * @param File 文件名称
     * @param Ends 后缀
     * @return 返回更换后的文件后缀
     */
    public static String ChangeEnds(String File,String Ends){
        String F = new File(File).getName();
        if(F.contains(".")){
            F = F.substring(0,F.lastIndexOf('.')) + Ends;
        }
        else{
            F = F + Ends;
        }
        return new File(File).getPath().substring(0,
                new File(File).getPath().length() -
                new File(File).getName().length()) + F;
    }

    /**
     * 创建路径和文件
     * @param File 文件路径
     * @throws Exception IO异常
     */
    public static void CreateFileAndDirs(String File) throws Exception{
        if(new File(File).exists()) return;

        String Dirs = new File(File).getPath().substring(0,
                new File(File).getPath().length() -
                        new File(File).getName().length());
        if(!new File(Dirs).exists())
        if(!new File(Dirs).mkdirs())
            throw new DetailedException("Mkdirs Fall Down",Dirs,"Mkdirs Fall Down");

        if(!new File(File).exists())
        if(!new File(new File(File).getName()).createNewFile())
            throw new DetailedException("Create File Fall Down",Dirs,"Create File Fall Down");
    }

    /**
     * 通用解析反射赋值函数
     * -DName=Vulan 给Name赋值Vulan
     * @param Cls 类类型
     * @param obj 实例
     * @param Param 字符串
     * @throws Exception 解析异常
     */
    public static void ReflectionAssignment(Class<?> Cls,Object obj,String Param) throws Exception{
        if(Param.startsWith("-D")){
            var s = Param.substring(2);

            var VulanName = s.substring(0,s.indexOf("="));
            var Vulan = s.substring(s.indexOf("=")+1);

            Field f = Cls.getField(VulanName);
            f.set(obj, Tools.GetFieldVulan(Vulan,f));
        }
        else throw new DetailedException("Param","Param:" + Param,"Not -D");
    }

}