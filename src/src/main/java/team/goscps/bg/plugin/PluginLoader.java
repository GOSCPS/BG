/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    PluginLoader.java
 *   Content: PluginLoader Source File
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.plugin;

import team.goscps.bg.api.*;
import team.goscps.bg.tool.Tools;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
    //Plugin 实例
    public static Object[] plugins = new Object[0];

    //读取自己的Plugin
    public PluginLoader() throws Exception {
        Tools.Green_Println("Load Jar " + this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
        LoadJar(this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
    }

    /**
     * 获取包内所有类
     * @param path 包路径
     * @return class列表
     * @throws IOException 来自JarFile
     */
    private LinkedList<String> GetAllClassFromJar(String path) throws IOException {
        JarFile jarFile = new JarFile(path);
        Enumeration<JarEntry> entry = jarFile.entries();

        //获取Jar内所有类
        LinkedList<String> ClassList = new LinkedList<>();

        while (entry.hasMoreElements()) {
            JarEntry jarEntry = entry.nextElement();
            var s = jarEntry.getName();

            if(s.equals("module-info.class")
            || s.startsWith("javax") || s.startsWith("javassist")){
                continue;
            }

            //删去.class
            //获取类名
            if(s.endsWith(".class")){
                s = s.substring(0,s.length()-".class".length());
                s = s.replace('/','.');
                ClassList.add(s);
            }
        }

        return ClassList;
    }


    /**
     * 从Jar加载插件
     * @param path Jar路径
     * @throws Exception 加载异常
     */
    public void LoadJar(String path) throws Exception {
        var CLassList = GetAllClassFromJar(path);

        URL url = new URL("file:" + path);
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[] {url});

        LinkedList<Class<?>> PluginList = new LinkedList<>();

        //挨个加载
        for (var cs :
                CLassList) {

            Class<?> c = null;
            try{
                c = urlClassLoader.loadClass(cs);
            }catch (Exception err){
                continue;
            }

            var s = c.getAnnotation(Plugin.class);

            if(s != null){
                Tools.Green_Println("Load Plugin `" + s.name() + "`");
                PluginList.add(c);
            }
        }

        //挨个实例化
        int ptr = plugins.length;

        plugins =java.util.Arrays.copyOf(plugins,plugins.length+PluginList.size());

        for(int a=ptr;a != plugins.length;a++){
            plugins[a] = PluginList.get(a).getDeclaredConstructor().newInstance();
        }
    }

}
