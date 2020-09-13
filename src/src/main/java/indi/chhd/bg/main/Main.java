package indi.chhd.bg.main;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ibm.icu.impl.locale.XCldrStub;
import indi.chhd.bg.config.*;
import com.google.gson.*;
import indi.chhd.bg.parser.Parser;
import indi.chhd.bg.plugin.DefPlugin;
import indi.chhd.bg.plugin.PluginLoader;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * BSD 3-Clause License
 *
 * Copyright (c) 2020, 彩虹海盗
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * BG入口函数
 */

public class Main {

    public static Config config;

    public static void main(String[] args){
        try {
            if (args.length == 2 && args[0].equals("Init")) {
                InitBlog(args[1]);
            }

            else if(args.length == 1 && args[0].equals("Version")){
                System.out.println("BG Version 0.1.0");
            }

            else if(args.length == 1 && args[0].equals("Build")){
                Build(false);
            }

            else if(args.length == 2 && args[0].equals("Build") && args[1].equals("--ALL")){
                Build(true);
            }

            else System.out.println("Not Found Command");

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void InitBlog(String Name) throws Exception{
        String DirStr = System.getProperty("user.dir")  + java.io.File.separator + Name;
        System.out.println("New BG-Blog:" + DirStr);

        /*创建目录*/
        //Blog根目录
        File Dir = new File(DirStr);
        if(Dir.exists()) {
            System.out.println("Error:Dir No Null");
            return;
        }
        else {
            if(!Dir.mkdir()) throw new IOException("Mkdir Fall Down");
        }

        //Plugin目录
        Dir = new File(DirStr + File.separator + "Plugin");
        if(!Dir.mkdir()) throw new IOException("Mkdir Fall Down");

        //Blog目录
        Dir = new File(DirStr + File.separator + "Blog");
        if(!Dir.mkdir()) throw new IOException("Mkdir Fall Down");

        //Build目录
        Dir = new File(DirStr + File.separator + "Build");
        if(!Dir.mkdir()) throw new IOException("Mkdir Fall Down");

        //Source目录
        Dir = new File(DirStr + File.separator + "Source");
        if(!Dir.mkdir()) throw new IOException("Mkdir Fall Down");

        //Include目录
        Dir = new File(DirStr + File.separator + "Include");
        if(!Dir.mkdir()) throw new IOException("Mkdir Fall Down");

        String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        //基础主题
        {
            String url = jarPath + "!/Theme.html";
            WriteAllText(MakeFilePath(DirStr,"Include","Theme.html"),
                    ReadURLAllText(url));
        }
        //基础文章
        {
            String url = jarPath + "!/Blog.md";
            WriteAllText(MakeFilePath(DirStr,"Blog","Blog.md"),
                    ReadURLAllText(url));
        }
        //基础css
        {
            String url = jarPath + "!/Style.css";
            WriteAllText(MakeFilePath(DirStr,"Build","Style.css"),
                    ReadURLAllText(url));
        }
        //基础主页
        {
            String url = jarPath + "!/Index.html";
            WriteAllText(MakeFilePath(DirStr,"Build","Index.html"),
                    ReadURLAllText(url));
        }
        //基础服务器
        {
            String url = jarPath + "!/Server.js";
            WriteAllText(MakeFilePath(DirStr,"Server.js"),
                    ReadURLAllText(url));
        }

        //创建配置文件
        config = new Config();
        config.Name = Name;
        WriteConfig(DirStr + File.separator + "Config.json");
    }

    public static void Build(boolean ALL) throws Exception {
        String DirStr = System.getProperty("user.dir");
        String DirBuild = DirStr + File.separator + "Build";
        String DirBlog = DirStr + File.separator + "Blog";

        ReadConfig(DirStr + File.separator + "Config.json");

        Parser parser = new Parser();
        parser.Init();

        PluginLoader ModLoad = new PluginLoader();
        ModLoad.PluginList.add(new DefPlugin());//默认插件
        ModLoad.Load(DirStr + File.separator + "Plugin", config.Plugin);

        parser.OptionsPlugin = ModLoad.PluginList;
        parser.ParserPluginList = new ArrayList<>
                (Arrays.asList(ModLoad.GetParserExtension()));

        //渲染
        String[] FLS = GetFileList(DirBlog);
        File[] FL = new File[FLS.length];
        for(int a=0;a < FLS.length;a++){
            FL[a] = new File(FLS[a]);
        }

        for (var f : FL) {
            //将Blog文件目录置换到Build
            String MiddleDir = DirBuild + f.toString().substring(DirBlog.length());
            File OutFile = new File(MiddleDir);

            //置换后缀
            if(OutFile.getName().lastIndexOf(".") != -1) {
                OutFile = new File(MiddleDir.substring(0, MiddleDir.lastIndexOf(".")) + ".html");
            }

            //文件夹，跳过
            if (f.isDirectory()) continue;

            if(!ALL) {
                //源文件修改时间早于编译时间则跳过
                if (NotNeedChange(f.toString(), OutFile.toString())) {
                    continue;
                }
                //主题文件早于源文件修改则跳过
                else if (NotNeedChange(
                        MakeFilePath(DirStr,
                                "Include","Theme.html"),
                        f.toString())) {
                    continue;
                }
            }


            String Html;
            ModLoad.Call("Start", "",f.toString());
            Html = ReadAllText(f.toString());

            Html = ModLoad.Call("BeforeConversion", Html,"");

            Html = parser.MarkdownToHtml(Html);

            Html = ModLoad.Call("AfterConversion", Html,"");
            Html = ModLoad.Call("BeforeTheming", Html,"");

            Html = Theming(ReadAllText(MakeFilePath(DirStr,
                    "Include","Theme.html")),Html);

            Html = ModLoad.Call("AfterTheming", Html,"");

            System.out.println("compiler:" + f.toString() + " TO "
                    + OutFile.toString());

            WriteAllText(OutFile.toString(), parser.MarkdownToHtml(Html));

            ModLoad.Call("End", Html,OutFile.toString());
        }

    }

    public static String ReadAllText(String File) throws Exception{
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(new File(File)), StandardCharsets.UTF_8));
        List<String> MarkList = bf.lines().collect(Collectors.toList());
        String Html = XCldrStub.Joiner.on("\n").join(MarkList);
        bf.close();
        return Html;
    }

    public static void WriteAllText(String File,String Text){
        try {
            if (!(new File(File).exists())) {
                new File(new File(File).toString().substring(0,
                        new File(File).toString().length() - new File(File).getName().length()
                )).mkdirs();
                new File(File).createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(File)), StandardCharsets.UTF_8));
            bw.write(Text);
            bw.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //如果Aims不存在或者Source晚于Aims修改则修改
    public static boolean NeedChange(String Source,String Aims){
        long SourceTime = new File(Source).lastModified();
        if(!new File(Aims).exists()) return true;
        long AimsTime = new File(Aims).lastModified();
        return SourceTime > AimsTime;
    }

    public static boolean NotNeedChange(String Source,String Aims){
        return !NeedChange(Source,Aims);
    }


    public static void ReadConfig(String File) throws Exception{
        GsonBuilder gson = new GsonBuilder().
                serializeNulls()//不忽略null
                .setPrettyPrinting();//设置输出格式化
        config = gson.create().fromJson(ReadAllText(File),Config.class);
    }

    public static void WriteConfig(String File) throws Exception{
        GsonBuilder gson = new GsonBuilder().
                serializeNulls()//不忽略null
                .setPrettyPrinting();//设置输出格式化
        String s = gson.create().toJson(config);
        WriteAllText(File,s);
    }

    public static String Theming(String Theme,String Html){
        if(Theme.contains("#{_BG_MAIN_}#")){
            Theme = Theme.replace("#{_BG_MAIN_}#",Html);
        }
        return Theme;
    }

    public static String[] GetFileList(String Path){
        ArrayList<String> List = new ArrayList<>();
        File f = new File(Path);
        var fList = f.listFiles();
        for(var s:fList){
            if(s.isDirectory()) List.addAll(Arrays.asList(GetFileList(s.toString())));
            else List.add(s.toString());
        }
        String[] out = new String[List.size()];
        List.toArray(out);
        return out;
    }

    public static String MakeFilePath(String ... p){
        return XCldrStub.Joiner.on(File.separator).join(Arrays.asList(p));
    }

    public static String ReadURLAllText(String Url) throws Exception{
        URL url = new URL("jar:file:" + Url);
        InputStream is = url.openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        List<String> MarkList = rd.lines().collect(Collectors.toList());
        String Html = XCldrStub.Joiner.on("\n").join(MarkList);
        return Html;
    }


}
