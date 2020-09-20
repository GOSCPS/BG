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

package indi.chhdao.bg.main;

import indi.chhdao.bg.api.Config;
import indi.chhdao.bg.build.BGBuilder;
import indi.chhdao.bg.exception.DetailedException;
import indi.chhdao.bg.server.Server;
import indi.chhdao.bg.tools.Tools;

import java.io.File;

/**
 * @author chhdao
 * @version 0.1.0
 * @since 2020-9-19
 */

public class Main {
    public static final String Version = "0.1.0";
    public static Config config = new Config();

    /***
     * BG主类
     * @param args 命令行参数
     */
    public static void main(String []args){
        try {

            if (args == null || args.length == 0) {
                System.err.println("No Command Input!");
            }

            else {
                //构建
                if (args[0].equals("Build")) {
                    BGBuilder builder = new BGBuilder();
                    //获取Build参数
                    builder.MakeBuildInfo(Tools.DeleteArrays(args,0));
                    builder.Build();
                }
                //帮助
                else if (args[0].equals("Help")) {
                    System.out.println("BG Version " + Version);
                }
                //创建新站点
                else if(args[0].equals("Init") && args.length == 2){
                    Init(args[1]);
                }
                //启动服务器
                else if(args[0].equals("Server")){
                    Server server = new Server();
                    server.Init(Tools.DeleteArrays(args,0));
                    server.StartUp();
                }
                else{
                    System.err.println("Not Found Command");
                }
            }

        }
        catch (Exception err){
            err.printStackTrace();
            System.err.println("Bg error,you cam report to github.com/chhdao/BG/issues");
        }
    }

    /**
     * 创建新站点
     * @param Name 站点名称
     * @throws Exception IO异常
     */
    public static void Init(String Name) throws Exception{
        String BaseDir = Tools.MakeFilePath(System.getProperty("user.dir"),Name);

        //创建根目录
        if(new File(BaseDir).isDirectory())
            throw new DetailedException("Directory is not uncreate",BaseDir,"Directory is not uncreate");
        else if(!new File(BaseDir).mkdir()) throw new DetailedException("Mkdirs Fall Down", BaseDir,"Mkdirs Fall Down");

        File Dir;

        //创建Blog目录
        Dir = new File(Tools.MakeFilePath(BaseDir,"Blog"));
        if(!Dir.mkdir()) throw new DetailedException("Mkdirs Fall Down", Dir.toString(),"Mkdirs Fall Down");

        //创建Source目录
        Dir = new File(Tools.MakeFilePath(BaseDir,"Source"));
        if(!Dir.mkdir()) throw new DetailedException("Mkdirs Fall Down", Dir.toString(),"Mkdirs Fall Down");

        //创建Site目录
        Dir = new File(Tools.MakeFilePath(BaseDir,"Site"));
        if(!Dir.mkdir()) throw new DetailedException("Mkdirs Fall Down", Dir.toString(),"Mkdirs Fall Down");

        //创建Plugin目录
        Dir = new File(Tools.MakeFilePath(BaseDir,"Plugin"));
        if(!Dir.mkdir()) throw new DetailedException("Mkdirs Fall Down", Dir.toString(),"Mkdirs Fall Down");

        //创建Theme目录
        Dir = new File(Tools.MakeFilePath(BaseDir,"Theme"));
        if(!Dir.mkdir()) throw new DetailedException("Mkdirs Fall Down", Dir.toString(),"Mkdirs Fall Down");

        //创建Config目录
        Dir = new File(Tools.MakeFilePath(BaseDir,"Config"));
        if(!Dir.mkdir()) throw new DetailedException("Mkdirs Fall Down", Dir.toString(),"Mkdirs Fall Down");

        //创建配置文件
        config.Name = Name;
        Tools.WriteAllText(Tools.MakeFilePath(BaseDir,"Config.json"),Tools.GetJson(config));

    }




}
