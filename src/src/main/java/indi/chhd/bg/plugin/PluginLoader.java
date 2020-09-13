package indi.chhd.bg.plugin;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * BSD 3-Clause License
 *
 * Copyright (c) 2020, 彩虹海盗
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 *
 *
 *
 */

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import com.vladsch.flexmark.html.HtmlRenderer;
import indi.chhd.bg.Plugin;

public class PluginLoader {
    public LinkedList<Plugin> PluginList = new LinkedList<>();

    @SuppressWarnings("unchecked")
    public void Load(String Path, LinkedHashMap<String,String> Plugin) throws Exception {
        String Paths = Path + File.separator;
        for (Map.Entry<String, String> stringStringEntry : Plugin.entrySet()) {
            File JarFile = new File(Paths + stringStringEntry.getKey());

            URL url = JarFile.toURI().toURL();
            URL[] urls = new URL[]{url};
            URLClassLoader cl = new URLClassLoader(urls, Thread.currentThread()
                    .getContextClassLoader());

            Class<? extends Plugin> PluginClass;
            if(cl.loadClass(stringStringEntry.getValue()).isInstance(Plugin)){
                        PluginClass = (Class<? extends Plugin>)cl.loadClass(stringStringEntry.getValue());
            }
            else throw new Exception("Load Plugin Error");

            PluginList.add(PluginClass.getDeclaredConstructor().newInstance());
        }
    }


    public String Call(String Type,String Text,String File){
        switch (Type) {
            case "Start":
                for (var s : PluginList) {
                    Text = s.Start(Text,File);
                }
                return Text;
            case "BeforeConversion":
                for (var s : PluginList) {
                    Text = s.BeforeConversion(Text);
                }
                return Text;
            case "AfterConversion":
                for (var s : PluginList) {
                    Text = s.AfterConversion(Text);
                }
                return Text;
            case "BeforeTheming":
                for (var s : PluginList) {
                    Text = s.BeforeTheming(Text);
                }
                return Text;
            case "AfterTheming":
                for (var s : PluginList) {
                    Text = s.AfterTheming(Text);
                }
                return Text;
            case "End":
                for (var s : PluginList) {
                    Text = s.End(Text,File);
                }
                return Text;
            default:
                return Text;
        }
    }

    public HtmlRenderer.HtmlRendererExtension[] GetParserExtension(){
        LinkedList<HtmlRenderer.HtmlRendererExtension> list = new LinkedList<>();
        for(var s:PluginList){
            if(s.GetParserExtension() != null) {
                list.add(s.GetParserExtension());
            }
        }
        HtmlRenderer.HtmlRendererExtension[] out = new HtmlRenderer.HtmlRendererExtension[list.size()];
        list.toArray(out);
        return out;
    }

}
