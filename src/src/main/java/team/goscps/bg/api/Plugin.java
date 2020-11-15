/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    Plugin.java
 *   Content: Bg Plugin Module
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.api;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Plugin {
    //插件API适用版本
    public int ApiVersion = 1;

    //插件版本
    public int PluginVersion()default 1;

    //插件名称
    public String name();

    //插件Web主页
    public String WebSite()default "";

    //贡献者
    public String Owner();

    //许可证
    public String License()default "GOSCPS License";
}
