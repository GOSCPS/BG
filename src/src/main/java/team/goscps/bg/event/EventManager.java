/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    EventManager.java
 *   Content: EventManager API Source File
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.event;

import java.util.HashMap;
import java.util.Map;

//负责管理线程之间的Event
public class EventManager {

    //为每个线程准备一个总线
    private static Map<String,Object> EventBusList = new HashMap<>();

    public static EventBus GetThreadEventBus(){
        return (EventBus)EventBusList.get(Thread.currentThread().getName());
    }

    public static void ResignedThread(String tName,Object eventBus){
        EventBusList.put(tName,eventBus);
    }
}