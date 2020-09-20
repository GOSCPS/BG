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

package indi.chhdao.bg.server;

import com.google.gson.annotations.Expose;
import indi.chhdao.bg.api.Config;
import indi.chhdao.bg.exception.DetailedException;
import indi.chhdao.bg.ssl.Ssl;
import indi.chhdao.bg.tools.Tools;

import javax.net.ssl.SSLServerSocket;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;


/**
 * @author chhdao
 * @version 0.1.0
 * @since 2020-09-19
 */

public class Server {
    //配置文件
    @Expose(serialize = false, deserialize = false)
    public Config config = new Config();
    //工作目录
    public String WorkDir = System.getProperty("user.dir");
    //线程数量
    public final int Threads = Runtime.getRuntime().availableProcessors();
    /* 数据<=0
     * ==null
     * 则读取config内数据
     */
    //服务器端口
    public int Port=-1;
    //密匙类型
    public String KeyStoreInstance = null;
    public String KeyManagerFactoryInstance = null;
    //密匙密码
    public String CertificatePassword = null;
    //密匙地址
    public String CertificatePath = null;

    //安全套接字
    @Expose(serialize = false, deserialize = false)
    SSLServerSocket SSocket = null;

    //线程池
    @Expose(serialize = false,deserialize = false)
    Corresponding []ThreadPoll;

    //下一个线程索引
    @Expose(serialize = false, deserialize = false)
    int NextThread;

    //控制服务器开关
    @Expose(serialize = false, deserialize = false)
    public static boolean Starts = true;

    /**
     * 启动服务器
     */
    public void StartUp() {
        try {
            System.out.println("Server Config");
            System.out.println(
                    "WorkDir: " + WorkDir +
                            "\nKeyStoreInstance: " + KeyStoreInstance +
                    "\nKeyManagerFactoryInstance: " + KeyManagerFactoryInstance +
                            "\nCertificatePassword: " + CertificatePassword +
                            "\nCertificatePath: " + new File(CertificatePath).getCanonicalPath()
            );

            ServerConsole ServerC = new ServerConsole("ServerConsoleThread");
            ServerC.start();

            Ssl ssl = new Ssl(new FileInputStream(new File(
                    new File(CertificatePath).getCanonicalPath())),
                    CertificatePassword
            );
            ssl.KeyManagerFactoryInstance = KeyManagerFactoryInstance;
            ssl.KeyStoreInstance = KeyStoreInstance;
            SSocket= ssl.GetSslSocket(Port);
            SSocket.setNeedClientAuth(true);

            while(Starts){
                   ProcessLink(SSocket.accept());
            }

            //进入清理工作
            for(var t:ThreadPoll){
                if(t != null)
                    t.join();
            }
        }
        catch(Exception err){
            err.printStackTrace();
        }
    }

    /**
     * 初始化服务器
     * @param args 初始化参数
     * @throws Exception 解析异常
     */
    public void Init(String []args) throws Exception{

        //定义
        for(var Param:args){
            Tools.ReflectionAssignment(this.getClass(),this,Param);
        }

        //读取配置文件
        config = Tools.ClassToJson(Tools.ReadAllText(Tools.MakeFilePath(WorkDir,"Config.json")),Config.class);
        if (config == null) {
            throw new DetailedException("Config Null",Tools.MakeFilePath(WorkDir,"Config.json"),"Config Null");
        }

        //如果用户未指定，则读取配置文件内的信息
        if(Port <= 0) Port = config.Port;
        if(KeyStoreInstance == null)KeyStoreInstance = config.KeyStoreInstance;
        if(KeyManagerFactoryInstance == null)KeyManagerFactoryInstance = config.KeyManagerFactoryInstance;
        if(CertificatePassword == null)CertificatePassword = config.CertificatePassword;
        if(CertificatePath == null) CertificatePath = config.CertificatePath;
        ThreadPoll = new Corresponding[Threads];
    }

    /**
     * 多线程处理套接字
     * @param socket 套接字
     */
    public void ProcessLink(Socket socket) throws IOException {
        if(ThreadPoll[NextThread] == null || !ThreadPoll[NextThread].isAlive()){
            ThreadPoll[NextThread] = new Corresponding("ServerT:" + NextThread,config);
        }

        ThreadPoll[NextThread].PushSocket(socket);

        if(!ThreadPoll[NextThread].isAlive()) {
            ThreadPoll[NextThread].start();
        }

        if(NextThread < (Threads-1)){
            NextThread++;
        }
        else
        {
            NextThread = 0;
        }
    }


}