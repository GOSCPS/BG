var http = require("http");
var fs = require("fs")
const util = require('util');

http.createServer(function (request, response) {

    // 发送 HTTP 头部 
    // HTTP 状态值: 200 : OK
    // 内容类型: text/plain
    // 编码：UTF-8
    response.writeHead(200, {'Content-Type': 'text/plain; charset=utf-8'});

    // 发送响应数据Index.html
    response.end(fs.readFileSync('Build/Index.html'));
}).listen(8888);

// 终端打印如下信息
console.log('Server running at http://127.0.0.1:8888/');

