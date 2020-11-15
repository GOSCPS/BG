/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    PluginVariable
 *   Content: PluginVariable.java
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.plugin;

import team.goscps.bg.config.RepoConfig;
import java.util.LinkedHashMap;
import java.util.Map;

public class PluginVariable {
    private static Map<String,String> Variables = new LinkedHashMap<>();

    private static RepoConfig config;

    public PluginVariable(RepoConfig c){
        config = c;
        Variables = c.defined;
    }

    public RepoConfig GetVariableConfig(){return config;}

    /**
     * 获取变量
     * @param Key Key
     * @return Value
     */
    public static String GetVariable(String Key){
        return Variables.get(Key);
    }

    /**
     * 查询变量
     * @param Key Key
     * @return 是否存在
     */
    public static boolean ContainsKey(String Key){
        return Variables.containsValue(Key);
    }

    /**
     * 删除变量
     * @param Key Key
     */
    public synchronized static void Remove(String Key){
        Variables.remove(Key);
    }

    /**
     * 设置变量
     * @param Key Key
     * @param Value Value
     */
    public synchronized static void SetVariable(String Key,String Value){
        Variables.put(Key,Value);
    }

    /**
     * 永久写入变量
     * @param Key Key
     * @param Value Value
     */
    public synchronized static void SetVariable_File(String Key,String Value){
        config.defined.put(Key,Value);
    }

    /**
     * 永久删除变量
     * @param Key Key
     */
    public synchronized static void RemoveVariable_File(String Key){
        config.defined.remove(Key);
    }

    /**
     * 查询永久变量是否存在
     * @param Key Key
     * @return 是否存在
     */
    public static boolean ContainsKey_File(String Key){
        return config.defined.containsKey(Key);
    }

    /**
     * 获取永久变量
     * @param Key Key
     * @return Value
     */
    public static String GetVariable_File(String Key){
        return config.defined.get(Key);
    }

}
