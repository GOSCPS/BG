# Script编写指南

Script是BG中的渲染脚本。  

让我们先看看他的语法:    
```
{{{ #这是一个注释 }}}

{{{ $URL = "github.com/GOSCPS/BG" }}}
{{{ $PagesList = ["Hello","about"] }}}

<p>{{{ $URL }}}</p>

{{{ for $forVar in $PagesList }}}
<p>$forVar</p>
{{{ EndFor }}}

```
将会输出:
```
<p>github.com/GOSCPS/BG</p>
<p>Hello</p>
<p>about</p>
```

**最高指示：所有和Script有关的东西，全部定义在{{{}}}内，即三层大括号**   
本指南将从解释器角度解释Script  

## 定义变量
首先，Script最先读取Config内的ScriptDefine选项里的变量，这些变量是全局的。   
之后，还有一些变量是Script自己推导出来的(例如文件列表)   

变量只在定义的文件生效，成为局部变量。  
定义一个变量很简单，你需要确保一个没有被使用过不包含任何空白字符的字符串然后:   
```
{{{ $你的变量名称_数字 = 0 }}}
{{{ $你的变量名字_浮点数 = 3.14 }}}
{{{ $你的变量名称_字符串 = "Hello World" }}}
{{{ $你的变量名称_数组 = ["Hello World",0] }}}
```
之后即可定义一个变量。
如果变量定义重复了，那么新定义的将会覆盖原有的，局部定义的将会覆盖全局定义。   


