package indi.chhd.bg.parser;
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
 * Markdown解析器
 */
import com.vladsch.flexmark.parser.*;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.misc.Extension;
import indi.chhd.bg.Plugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;


public class Parser {
        public ArrayList<HtmlRenderer.HtmlRendererExtension> ParserPluginList = new ArrayList<>();
        public LinkedList<Plugin> OptionsPlugin = new LinkedList<>();
        public MutableDataHolder options = new MutableDataSet(PegdownOptionsAdapter.flexmarkOptions(
                true,
                // 所有的特性
                Extensions.ALL
        ));
        com.vladsch.flexmark.parser.Parser HParser;
        HtmlRenderer HtmlRender;
        Node Nodes;

        public String MarkdownToHtml(String Markdown){
                Nodes = HParser.parse(Markdown);
                return HtmlRender.render(Nodes);
        }

        public void Init(){
                //插件设置属性
                for(var op:OptionsPlugin){
                        options = op.SetOptions(options);
                }

                Extension[] Extension = new Extension[ParserPluginList.size()];
                ParserPluginList.toArray(Extension);
                        //设置插件
                        options.set(com.vladsch.flexmark.parser.Parser.EXTENSIONS,
                                Arrays.asList(Extension));


                options.setFrom(ParserEmulationProfile.MARKDOWN);

                HParser = com.vladsch.flexmark.parser.Parser.builder(options).build();
                HtmlRender = HtmlRenderer.builder(options).build();
        }





}
