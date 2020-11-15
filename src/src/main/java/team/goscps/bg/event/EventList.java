/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
  *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
  *   使用 GOSCPS 许可证
  *   File:    YourFileNameHere
  *   Content: YourFileContentHere
  *   Copyright (c) 2020 GOSCPS 保留所有权利.
  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.event;

public enum EventList {
    BeforeRead("BeforeRead"),//文件读取前
    AfterRead("AfterRead"),//读取文件后，渲染前
    MarkdownRenderInit("MarkdownRenderInit"),//初始化Markdown渲染器
    Markdown("Markdown"),//渲染前
    Html("Html"),//渲染成Markdown后
    BeforeWrite("BeforeWrite"),//写入前
    AfterWrite("AfterWrite");//写入后

    public final String value;

    private EventList(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }


}
