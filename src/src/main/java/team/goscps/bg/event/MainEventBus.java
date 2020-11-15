/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    MainEventBus.java
 *   Content: MainEventBus API Source File
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.event;

//main 总线
public class MainEventBus {
    private static EventBus bus = new EventBus("main");

    public static EventBus GetMainEventBus(){
        return bus;
    }
}
