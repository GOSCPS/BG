/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    DefaultEvent.java
 *   Content: DefaultEvent API Source File
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.event;

public class DefaultEvent extends Event{

    /**
     * 事件设置
     * @param name 事件名称
     * @param CanCancel 能否撤销
     * @param message 事件参数
     */
    public DefaultEvent(String name,boolean CanCancel,Object[] message) {
        super(message);
        Cancel = CanCancel;
        Name = name;
    }

    private final boolean Cancel;
    private final String Name;

    @Override
    public boolean CanCancel() {
        return Cancel;
    }

    @Override
    public String GetName() {
        return Name;
    }


}
