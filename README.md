### 原项目地址
https://github.com/JesusFreke/smali

注：目前上面地址并未更新，可以看看下面这个

https://github.com/google/smali

## 说明
在 baksmali 的基础上，添加了修复methodCodeItem的功能，然后通过smali回编译。主要是在原来的基础上增加了一个方法的buff，修复的方法可以不通过dexbuff,通过传入的这个buff去修复。
剩下就是一些强制编译，去掉某些可能导致运行退出的问题，这样也导致了编译出来的dex文件不是非常标准，没法运行，但是这部分代码是可以查看的。

可以看完整的使用项目
https://github.com/Thehepta/androidGRPC
