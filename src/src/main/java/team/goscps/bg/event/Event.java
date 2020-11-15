/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
  *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
  *   使用 GOSCPS 许可证
  *   File:    YourFileNameHere
  *   Content: YourFileContentHere
  *   Copyright (c) 2020 GOSCPS 保留所有权利.
  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.event;

//事件接口
public abstract class Event {
    //是否能撤销
    public abstract boolean CanCancel();

    //获取事件名称
    public abstract String GetName();

    //参数
    public Event(Object[] message){
        Info = message;
    }

    //获取参数
    private final Object[] Info;

    public Object[] GetInfo(){
        return Info;
    }

    //总线名称
    private String BusName;

    //设置 获取 总线名称
    public String GetEventBusName(){
        return BusName;
    }
    public void SetEventBusName(String busName){
        BusName = busName;
    }
}
