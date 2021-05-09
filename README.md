# 如何破nike akamai高并发问题;canvas;akami cancas;akamai webgl;nike 破盾;snkrs 破盾|snkrs bot



## 如何通过sensor过盾；sensor怎么用；如何验证sensor是否可用；



**联系方式**：->QQ 微信同号:80258153



​	写到这里，如果abck和bmsz，什么是sensor是什么都还不清楚的，可以参考往期文章。

1、那么能看到这行的基本上都是行家了，我们知道他的作用域，无非目前就是三个地方比较重要

分别是web端的协议登录&下单；app端的协议登录部分；

2、而能看到这里的相信已经都能通过某些方式gen cookie了。

但是接下来，就会发现他的并发会是个麻烦事儿；

1、通过N次测试，我们发现一个问题；我们模拟出来的sensor在过盾的时候经常发现并发上不去。

web端来讲，我自己的经验就是他的插件、什么ua，这些基本上不太重要。

而真正在并发中起到关键性影响的还是以下几个；WEB GL ,CANVAS,字体；

在通过研究我们发现web gl 容易，字体也不难。

但是canvas通过cac看来貌似很傻瓜就是个int值，无脑的看似。

但是通过多次测试发现小丑是自己，这个canvas极难模拟。或者说他极端容易识别是错误的。

2、那么怎么办呢？我这边给出的解决方案是，通过在绘制前在背景加噪声。

这样生成出来的基本上是勉强可用。而且多次观察下来看，canvas是会发生变化的。

这个很容易识别，只要观察sensor的dis前面的就是2次canvas指纹；

3、至于显卡，字体，webGL、插件，相信对本文读者来说很容易就不细说了。 



再说另外一个app的

我们都清楚app的sensor除了协议方式登录以外，意义不大。

主要还是PUT请求的时候会用，而且他用的是acf-sensor-data;

这个基本上分为2部分、1个来讲主要是你的sensor串儿的拼接，另一个是rsa的加密。

第二部分相信很容易就不说了。

第一部分非常麻烦，他的各项参数均为加密的，那么这个只能采用破解的方式来处理。

也就是要破掉他的加密算法使得产生，多个。









在谈谈有效期；

1、之前app的有效期非常非常非常非常长；但是目前目测下来看，基本上6小时；

2、之前web有效期，abck、bmsz、60分钟，但是通过sensor的方式可以做到6个小时；



联系方式：->QQ 微信同号:80258153



往期文章:

《nike app 协议登录|how to login nike app》

《如何破429问题|how to create x-kpsdk-ct x-kpsdk-cd》

《akamai如何生成|how to create akamai sensor data》

《nike web 协议登录|how to login nike web》



效果展示:



[![gY3awD.png](https://z3.ax1x.com/2021/05/09/gY3awD.png)](https://imgtu.com/i/gY3awD)
[![gY3dTe.png](https://z3.ax1x.com/2021/05/09/gY3dTe.png)](https://imgtu.com/i/gY3dTe)
[![gY3UeO.png](https://z3.ax1x.com/2021/05/09/gY3UeO.png)](https://imgtu.com/i/gY3UeO)
