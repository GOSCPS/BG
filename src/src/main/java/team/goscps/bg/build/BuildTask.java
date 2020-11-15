/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    BuildTask.java
 *   Content: BuildTask Thread Task
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.build;

import team.goscps.bg.event.*;
import team.goscps.bg.render.RenderManager;
import team.goscps.bg.tool.Tools;

public class BuildTask extends Thread{
    private Thread t;
    private final String threadName;
    private Exception err = null;

    public Exception GetError(){return err;}

    public BuildTask(String name) {
        threadName = name;
    }

    @Override
    public void run() {
        try {
            EventManager.ResignedThread(this.threadName,new EventBus(this.threadName));

            while(true) {
                var p = BuildPool.GetTask();
                if(p == null) break;

                Event event = new DefaultEvent(EventList.BeforeRead.value, false,
                        new String[]{p.key});
                EventManager.GetThreadEventBus().post(event);

                //读取
                String markdown = Tools.Read_All_Text(p.key);

                event = new DefaultEvent(EventList.AfterRead.value, false,
                        new String[]{p.key});
                EventManager.GetThreadEventBus().post(event);

                event = new DefaultEvent(EventList.Markdown.value, false,
                        new String[]{markdown});
                EventManager.GetThreadEventBus().post(event);

                //获取Markdown事件结果
                if(EventManager.GetThreadEventBus().HasResult()){
                    markdown = EventManager.GetThreadEventBus().GetResult()[0].toString();
                }

                //解析
                String h = RenderManager.Parse(markdown);

                event = new DefaultEvent(EventList.Html.value, false,
                        new String[]{h});
                EventManager.GetThreadEventBus().post(event);

                //获取Html事件结果
                if(EventManager.GetThreadEventBus().HasResult()){
                    h = EventManager.GetThreadEventBus().GetResult()[0].toString();
                }

                event = new DefaultEvent(EventList.BeforeWrite.value, false,
                        new String[]{p.value});
                EventManager.GetThreadEventBus().post(event);

                //写入
                Tools.Write_All_Text(p.value, h);

                event = new DefaultEvent(EventList.AfterWrite.value, false,
                        new String[]{p.value});
                EventManager.GetThreadEventBus().post(event);
            }
        }
        catch(Exception err){
            this.err = err;
            err.printStackTrace();
        }
    }

    @Override
    public void start () {
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
    }
}
