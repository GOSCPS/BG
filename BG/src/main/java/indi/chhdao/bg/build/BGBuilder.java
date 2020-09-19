//* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// BSD 3-Clause License
//
// Copyright (c) 2020, 彩虹海盗
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// 3. Neither the name of the copyright holder nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

package indi.chhdao.bg.build;

import indi.chhdao.bg.api.Config;
import indi.chhdao.bg.exception.DetailedException;
import indi.chhdao.bg.markdown.MarkdownParser;
import indi.chhdao.bg.tools.Tools;

import java.lang.reflect.Field;

/**
 * @author chhdao
 * @version 0.1.0
 * @since 2020-09-19
 */

public class BGBuilder {

    //是否编译所有文件
    public boolean BuildAll = false;
    //工作目录
    public String WorkDir = System.getProperty("user.dir");
    //解析器
    public MarkdownParser parser = new MarkdownParser();
    //配置文件
    public Config config = new Config();

    /**
     * 构建
     */
    public void Build(){
        try {
            String BlogPath = Tools.MakeFilePath(WorkDir, "Blog");
            String SitePath = Tools.MakeFilePath(WorkDir, "Site");
            String[] FileList = Tools.GetFileList(BlogPath);

            for (var s : FileList) {
                String Out = SitePath + s.substring(BlogPath.length());
                Out = Tools.ChangeEnds(Out,".html");

                //不BuildAll && 不需要更改
                //则跳过
                if (!BuildAll && !Tools.NeedChange(s, Out)) {
                    System.out.println("Jump Over Compile " + s + " To " + Out);
                    continue;
                }

                Tools.CreateFileAndDirs(Out);

                String Html = parser.MarkdownToHtml(Tools.ReadAllText(s));

                Tools.WriteAllText(Out,Html);

                System.out.println("Compile " + s + " To " + Out);
            }

        }
        catch(Exception err){
            err.printStackTrace();
        }
    }


    /**
     * 通过命令行参数构建Build信息
     * @param args 命令行参数传参
     * @throws Exception 构建错误
     */
    public void MakeBuildInfo(String []args) throws Exception {
        var ThisClass = this.getClass();

        for(var Param:args){

            if(Param.startsWith("-D")){
                var s = Param.substring(2);

            var VulanName = s.substring(0,s.indexOf("="));
            var Vulan = s.substring(s.indexOf("=")+1);

            Field f = ThisClass.getField(VulanName);
            f.set(this, Tools.GetFieldVulan(Vulan,f));
            }
            else throw new DetailedException("Param","Param:" + Param,"Not -D");
        }

        //读取配置文件
        config = Tools.ClassToJson(Tools.ReadAllText(Tools.MakeFilePath(WorkDir,"Config.json")),Config.class);
        if (config == null) {
            throw new DetailedException("Config Null",Tools.MakeFilePath(WorkDir,"Config.json"),"Config Null");
        }
        //初始化解析器
        parser.Init();
    }


}
