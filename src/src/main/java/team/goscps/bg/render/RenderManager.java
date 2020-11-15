/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    RenderManager.java
 *   Content: RenderManager API Source File
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.render;

import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.misc.Extension;
import java.util.Arrays;
import java.util.LinkedList;

public class RenderManager {
    private static MutableDataHolder OPTIONS = new MutableDataSet();
    private static Parser parser;
    private static HtmlRenderer renderer;

    private static final LinkedList<Class<? extends Extension>> ExtensionsPlugins = new LinkedList<>();

    /**
     * 增加扩展
     * @param ext 扩展
     */
    public static void SetOptionsExtension(Class<? extends Extension> ext){
        ExtensionsPlugins.add(ext);
    }
    /*public static void SetOptionsExtension(Class<? extends Parser.ParserExtension> ext){
        ExtensionsPlugins.add(ext);
    }
    public static void SetOptionsExtension(Class<? extends HtmlRenderer.HtmlRendererExtension> ext){
        ExtensionsPlugins.add(ext);
    }
    public static void SetOptionsExtension(Class<? extends Formatter.FormatterExtension> ext){
        ExtensionsPlugins.add(ext);
    }*/

    /**
     * 使扩展生效
     * @throws Exception 扩展
     */
    public static void ExtensionTakeEffect() throws Exception{
        Extension[] Extensions = new Extension[ExtensionsPlugins.size()];
        for(int a=0;a != ExtensionsPlugins.size();a++){
            Extensions[a] = ExtensionsPlugins.get(a).getDeclaredConstructor().newInstance();
        }

        OPTIONS.set(Parser.EXTENSIONS, Arrays.asList(Extensions));
    }

    /**
     * 获取设置
     * @return 设置
     */
    public static MutableDataHolder GetOptions(){
        return OPTIONS;
    }

    /**
     * 设置
     * @param Options 设置
     */
    public static void SetOptions(MutableDataHolder Options){
        OPTIONS = Options;
    }

    /**
     * 使用当前设置构建渲染器
     */
    public static void BuildRenderer(){
        parser = Parser.builder(OPTIONS).build();
        renderer = HtmlRenderer.builder(OPTIONS).build();
    }

    /**
     * 渲染
     * @param markdown markdown
     * @return html
     */
    public static String Parse(String markdown){
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }

    public static Parser GetParser(){
        return parser;
    }
    public static HtmlRenderer GetHtmlRenderer(){
        return renderer;
    }
}
