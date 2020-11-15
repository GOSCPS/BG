/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    GFMSupports.java
 *   Content: GFMSupports Plugin
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.plugin;

import com.vladsch.flexmark.ext.tables.TablesExtension;
import team.goscps.bg.api.Plugin;
import team.goscps.bg.event.Event;
import team.goscps.bg.event.ListenEvent;
import team.goscps.bg.render.RenderManager;

@Plugin(PluginVersion = 1, name = "Gfm Supports", Owner = "GOSCPS")
public class GFMSupports{

    @ListenEvent("MarkdownRenderInit")
    public static void SetGfmSupports(Event event){

        if(PluginVariable.ContainsKey("GFMSupports")
        || PluginVariable.ContainsKey_File("GFMSupports")){

            if(PluginVariable.GetVariable("GFMSupports").equals("false"))
                return;
        }
        else{
            PluginVariable.SetVariable("GFMSupports","true");
            PluginVariable.SetVariable_File("GFMSupports","true");
        }

        var set = RenderManager.GetOptions();
        set = set.set(TablesExtension.COLUMN_SPANS, false)
                .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
                .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
                .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true);
        RenderManager.SetOptions(set);
        RenderManager.SetOptionsExtension(TablesExtension.class);
    }

}
