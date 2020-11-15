/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
  *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
  *   使用 GOSCPS 许可证
  *   File:    YourFileNameHere
  *   Content: YourFileContentHere
  *   Copyright (c) 2020 GOSCPS 保留所有权利.
  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//监听事件注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListenEvent {
    public String value();//要监听的事件名称
    public EventPriority level() default EventPriority.common;//优先级
}