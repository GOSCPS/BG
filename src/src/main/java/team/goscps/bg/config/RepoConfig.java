/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    RepoConfig.java
 *   Content: Bg RepoConfig Source File
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.config;

import java.util.LinkedHashMap;

public class RepoConfig {
    //仓库名字
    public String name = "";

    //定义的变量
    public LinkedHashMap<String,String> defined = new LinkedHashMap<>();

    //Scripter变量
    public LinkedHashMap<String,String> ScripterDefined = new LinkedHashMap<>();


    //url
    public String URL = "";
}