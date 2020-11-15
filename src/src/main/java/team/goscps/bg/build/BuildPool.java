/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    BuildPool.java
 *   Content: BuildPool Source File
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.build;

import java.util.LinkedList;

public class BuildPool {

    public static class Pair{
        public String key;
        public String value;
    }

    public static LinkedList<Pair> TaskList = new LinkedList<>();

    /**
     * 发布一个任务
     * @param path 输入
     * @param outPath 输出
     */
    public synchronized static void PushTask(String path,String outPath){
        Pair p = new Pair();
        p.key = path;
        p.value = outPath;
        TaskList.add(p);
    }

    /**
     * 获取一个任务
     * @return 任务无任务则返回null
     */
    public synchronized static Pair GetTask(){
        if(TaskList.size() == 0) return null;
        else return TaskList.remove();
    }
}
