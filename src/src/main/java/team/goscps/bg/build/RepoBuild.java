/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    RepoBuild.java
 *   Content: Build a Repo
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.build;

import team.goscps.bg.config.RepoConfig;
import team.goscps.bg.event.*;
import team.goscps.bg.plugin.PluginLoader;
import team.goscps.bg.plugin.PluginVariable;
import team.goscps.bg.render.RenderManager;
import team.goscps.bg.tool.Tools;

public class RepoBuild {
    //配置文件
    public static RepoConfig c = new RepoConfig();

    //是否构建所有文件
    public static boolean BuildAll = false;

    //插件
    public static PluginLoader loader;

    //占用线程数
    public static int Threads = Runtime.getRuntime().availableProcessors()+1;

    public RepoBuild(RepoConfig config) throws Exception{
        loader = new PluginLoader();
        c = config;
    }

    /**
     * 设置构建参数
     * @param args 参数
     * @return 是否成功
     */
    public boolean Setting(String []args){
        //利用反射解析参数
        try {
            for (var s :
                    args) {
                if (!s.startsWith("-D")) {
                    throw new Exception(s);
                }

                var KeyValuePair = s.substring(2);

                var index = KeyValuePair.indexOf('=');

                if (index == -1) {
                    throw new Exception(s);
                }

                var n = KeyValuePair.substring(0,index);
                var v = KeyValuePair.substring(index+1);

                Tools.ReflectionAssignment(this.getClass(),this,n,v);
            }
        }
        catch(Exception err){
            err.printStackTrace();
            Tools.Red_Println("Error:Param Parse Error");
            Tools.Red_Println(err.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 构建
     */
    public void Start(){
        long startTime = System.nanoTime();

        try {
            //初始化变量
            PluginVariable pluginVariable = new PluginVariable(c);

            //加载插件
            LoadPlugin();

            //开始渲染
            //1.分配任务
            String[] in = Tools.GetFileTree("./Pages");

            for(int a=0;a!= in.length;a++){
                String out;
                out = "./Site" + in[a].substring("./Pages".length());

                /*if(out.endsWith(".md")) {
                    out = out.substring(0,out.length()-3);*/
                    out = out + ".html";
                //}
                BuildPool.PushTask(in[a],out);
            }

            //初始化Markdown渲染器
            Event e = new DefaultEvent(EventList.MarkdownRenderInit.value,false, new String[]{""});
            MainEventBus.GetMainEventBus().post(e);

            RenderManager.ExtensionTakeEffect();
            RenderManager.BuildRenderer();

            //2.线程池初始化
            BuildTask[] threads = new BuildTask[Threads];
            for(int a=0;a < Threads;a++){
                threads[a] = new BuildTask(String.format("Thread-%d",a));
            }

            for(int a=0;a < Threads;a++){
                threads[a].start();
            }

            for(int a=0;a < Threads;a++){
                var err = threads[a].GetError();
                if(err != null) throw err;
            }

            //写入配置文件
            Tools.Write_All_Text("./Config.json"
                    ,Tools.ObjectTOJson(pluginVariable.GetVariableConfig()));
        }
        catch(Exception err){
            err.printStackTrace();
            Tools.Red_Println("Build Error");
            return;
        }
        long totalTime = System.nanoTime() - startTime;
        totalTime /= 1000;//换算到微秒
        Tools.Green_Println("Build Success");
        Tools.Green_Println(String.format("Use %sμs",Long.toString(totalTime)));
    }

    /**
     * 加载插件
     * @throws Exception 异常
     */
    private void LoadPlugin() throws Exception{
        var s = Tools.GetFileTree("./Plugin/");
        for (var jar : s) {
            loader.LoadJar(jar);
        }
    }


}
