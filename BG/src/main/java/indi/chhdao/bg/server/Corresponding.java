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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import indi.chhdao.bg.api.Config;

/**
 * @author chhdao
 * @version 0.1.0
 * @since 2020-09-20
 */

public class Corresponding extends Thread{

    public String ThreadName;
    public final Config config;
    public List<Socket> SocketList = new ArrayList<>();

    @Override
    public void run(){
        try {
            while(Server.Starts) {
                for (ListIterator<Socket> It = SocketList.listIterator(); It.hasNext(); ) {
                    var Now = It.next();
                    if (!Now.isClosed() && Now.isBound()) {
                        System.out.println("Link From " + Now.getLocalAddress());
                        CorrespondingData(Now.getInputStream(),
                                Now.getOutputStream());
                    } else {
                        It.remove();
                    }
                }
                Thread.yield();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 初始化
     * @param TName 线程名称
     * @param config 配置
     */
    public Corresponding(String TName,Config config) {
        super();
        ThreadName = TName;
        this.setName(TName);
        this.config = config;
    }

    /**
     * 解析数据
     * @param In socket输入流
     * @param Out socket输出流
     * @throws IOException IO异常
     */
    public void CorrespondingData(InputStream In, OutputStream Out) throws IOException {
        byte[] b = In.readAllBytes();
        System.out.println(new String(b, StandardCharsets.UTF_8));
    }

    /**
     * 增加socket任务
     * @param socket socket
     * @throws SocketException socket异常
     */
    public void PushSocket(Socket socket) throws SocketException {
        if(!socket.isClosed() && socket.isBound()) {
            socket.setSoTimeout(15000);//超时时间15秒
            SocketList.add(socket);
        }
    }


}
