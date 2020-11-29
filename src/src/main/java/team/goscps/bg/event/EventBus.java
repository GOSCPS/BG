/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
  *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
  *   使用 GOSCPS 许可证
  *   File:    YourFileNameHere
  *   Content: YourFileContentHere
  *   Copyright (c) 2020 GOSCPS 保留所有权利.
  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.event;

import team.goscps.bg.plugin.PluginLoader;

import java.lang.reflect.Method;
import java.util.*;

public class EventBus {
    //总线名称
    private final String EventBusName;

    public EventBus(String n){
        EventBusName = n;
    }


    private boolean IsCancel = false;
    private boolean CanCancel = false;

    private boolean HasResult = false;
    private Object[] Result = null;

    public static class RegisteredPair{
        public Object o;
        public Method m;
        public String EventName;
        public EventPriority level;
    }

    private final Map<String,RegisteredPair> register = new LinkedHashMap<>();

    /**
     * 事件是否有结果
     * @return 是否
     */
    public boolean HasResult(){return HasResult;}

    /**
     * 事件的结果
     * @return 通过SetResult设置的结果
     * @throws RuntimeException 事件无结果
     */
    public Object[] GetResult() throws RuntimeException{
        if(HasResult){
            return Result;
        }
        else{
            throw new RuntimeException("There's no result");
        }
    }

    /**
     * 设置事件结果
     * @param result 要设置的结果
     */
    public void SetResult(Object[] result){
        Result = result;
        HasResult = true;
    }

    /**
     * 事件能否被撤销
     * @return 事件
     */
    public boolean CanCancel(){
        return CanCancel;
    }

    /**
     * 获取事件是否被撤销
     * @return 是否
     */
    public boolean IsCancel(){
        return IsCancel;
    }

    /**
     * 撤销事件
     * @throws RuntimeException 事件已经被撤销
     */
    public void SetCancel() throws RuntimeException{
        if(IsCancel && (!CanCancel)){
            throw new RuntimeException("Event Canceled");
        }
        else{
            IsCancel = true;
        }
    }

    /**
     * 注册事件
     * @param RegisteredId Id，用于取消注册
     * @param obj 对象
     * @param method 函数
     * @param name 事件名称
     * @param priority 事件优先级
     */
    public synchronized void RegisteredEvent(String RegisteredId,Object obj,Method method,String name,EventPriority priority)
    throws Exception{
        if(register.get(RegisteredId) != null){
            throw new Exception("RegisteredId Is Defined");
        }

        RegisteredPair p = new RegisteredPair();
        p.EventName = name;
        p.m = method;
        p.level = priority;
        p.o = obj;
        register.put(RegisteredId,p);
    }

    /**
     * 取消注册某个事件
     * @param RegisteredId 注册时候的ID
     */
    public synchronized void UnRegistered(String RegisteredId){
        register.remove(RegisteredId);
    }

    private static class Pair<K,Y>{
        public K left;
        public Y right;
    }

    /**
     * 发布事件
     * @param event 事件
     */
    public void post(Event event) throws Exception{
        //初始化设置
        CanCancel = event.CanCancel();
        HasResult = false;
        event.SetEventBusName(this.EventBusName);

        //获取所有带有ListenEvent注解的方法
        LinkedList<Pair<Method,Object>> listener = new LinkedList<>();

        for(var o: PluginLoader.plugins){
            Method[] methods = o.getClass().getMethods();

            for(var m:methods){
                var l = m.getAnnotation(ListenEvent.class);

                if(l != null){
                    Pair<Method,Object> p = new Pair<>();
                    p.left = m;
                    p.right = o;
                    listener.add(p);
                }
            }
        }

        //添加注册函数
        for(var o: register.values()){
            Pair<Method,Object> pair = new Pair<>();
            pair.left = o.m;
            pair.right = o.o;
            listener.add(pair);
        }

        //按照高到低等级发布
        //被取消则跳过
        if(!IsCancel())
        Post_Level(EventPriority.highest,event,listener);

        if(!IsCancel())
        Post_Level(EventPriority.high,event,listener);

        if(!IsCancel())
        Post_Level(EventPriority.common,event,listener);

        if(!IsCancel())
        Post_Level(EventPriority.low,event,listener);

        if(!IsCancel())
        Post_Level(EventPriority.lowest,event,listener);
    }

    /**
     * 对内事件发布
     * @param level 等级
     * @param listener 方法
     * @param event 事件
     * @throws Exception 方法异常
     */
    private void Post_Level(EventPriority level,Event event,LinkedList<Pair<Method,Object>> listener) throws Exception {

        for(var o:listener){
            var a = o.left.getAnnotation(ListenEvent.class);

            //被取消
            if(IsCancel()){
                break;
            }

            //是要触发的事件并且优先度相等
            if(a.level() == level && a.value().equals(event.GetName())){
                o.left.invoke(o.right,event);
            }

        }
    }

}
