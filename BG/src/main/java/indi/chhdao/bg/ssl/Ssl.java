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

package indi.chhdao.bg.ssl;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * @author chhdao
 * @version 0.1.0
 * @since 2020-09-19
 */

public class Ssl {

    public String CertificatePassword;
    public InputStream Certificate;
    public String KeyStoreInstance = "PKCS12";
    public String KeyManagerFactoryInstance = "SunX509";

    /**
     * 初始化
     * @param certificate 证书输入流
     * @param CertificatePassword 证书密码
     */
    public Ssl(InputStream certificate,String CertificatePassword){
        this.CertificatePassword = CertificatePassword;
        this.Certificate = certificate;
    }

    /**
     * 创建SSL上下文
     * @return SSL上下文
     * @throws Exception 创建异常
     */
    public SSLContext SslContext() throws Exception {
        //加载密匙
        KeyStore keyStore = KeyStore.getInstance(KeyStoreInstance);
        char[] certificatePassword = CertificatePassword.toCharArray();
        keyStore.load(Certificate,certificatePassword);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactoryInstance);
        //使用秘钥库和证书密码初始化秘钥管理器工厂
        keyManagerFactory.init(keyStore, certificatePassword);
        //获取SSL上下文实例
        SSLContext sslContext = SSLContext.getInstance("SSL");
        //初始化SSL上下文
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
        return sslContext;
    }

    /**
     * 获取SSL套接字
     * @param port 端口
     * @return SSL套接字
     * @throws Exception 创建异常
     */
    public SSLServerSocket GetSslSocket(int port) throws Exception{
        //SSL上下文
        SSLContext sslContext = SslContext();
        //获取服务器安全套接字工厂
        SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
        //在指定的端口创建服务器安全套接字
        return (SSLServerSocket) serverSocketFactory.createServerSocket(port);
    }


}
