# BG使用手册


## 创建一个BG站点  
要创建一个BG站点，只需要在工作目录输入:   
```
$ java -jar BG.jar new NAME
```
NAME是你要创建的站点名称     

创建后的目录结构如下:   
```
java -jar BG.jar new HelloBG
HelloBG:
├─Config.json
├─Config
├─Pages
├─Plugin
├─Site
└─Source
```

## Config.json
这是BG的全局配置文件 

## Template
这是存放生成模板的目录

## Config
这是BG的配置文件目录，一般供插件所使用

## Pages
这是存放Markdown的目录

## Plugin
这是存放插件的目录

## Site
这是Pages的Markdown生成之后的Html

## Source 
这是存档非Markdown的文本及二进制文件的目录