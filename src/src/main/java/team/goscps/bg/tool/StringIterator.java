/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    StringIterator.java
 *   Content: StringIterator Source File
 *   Copyright (c) 2020-2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.tool;

public class StringIterator {

    private int It = 0;
    private String str="";

    public StringIterator(String string){
        str = string;
    }

    public String Get(){
        return String.valueOf(str.charAt(It));
    }

    public void Next(){
        It++;
    }

    public boolean IsMax(){
        return It >= str.length();
    }

    public void AfterSpace(){
        while((!this.IsMax()) && this.Get().isBlank()){
            this.Next();
        }
    }

    public int GetIndex(){
        return It;
    }
}
