# QQRobot
 QQRobot是一个基于[Simbot](https://github.com/ForteScarlet/simpler-robot) 框架编写的QQ群群聊机器人。
 ##### 她能做什么？
 ##### 当前，她支持以下功能：
 + \#来点{关键字}：以关键字为目标，在P站搜索图片，并在第一页中随机3-5张返回。
   要使用该功能，您必须：
   + 配置HttpProxy（或者您的网络环境可以直连）
   + 登陆Pixiv账户，并将账户Cookie写入cookie-file指定的文件中。Cookie文件将会自动更新。实际支持的功能取决于您的账户设置，我相信你知道我的意思。
 + \#给我也整一个 {PixivId}：将该PixivId指向的图片全部下载，并将其发送到指令发起人的QQ邮箱。
 + \#搜图{图片1,[图片2,图片3...]}：将信息中的图片进行联网搜索并返回结果。当前仅支持saucenao.com。计划中，她还将支持ascii2d.net。
 + \#自动回复 [{触发}:{回复}]：使用该指令为她添加自动回复，随后@她并发送触发词即可。当前，仅支持纯文本信息。未来，她将会支持@类型、图片类型等。
 + \#还在吗 快速检测机器人是否在线 是否可以接收消息。
 ##### 她将能做到什么？
 在计划中，她将可能会支持但不限于以下功能：
 + 通过帮助指令查看可用指令；
 + 通过指令添加自动回复功能；（*已实现*）
 + 通过指令限定关键字随机查找Pixiv图片功能；（*已实现*）
 + 推送P站排行榜功能；
 + 通过指令设定定时推送信息
 + 对以上功能的开关/管理
 + 其他你想要的功能。请通过邮件联系我：stardust.whc@gmail.com

 #### 部署方法：
 1. 建立application-account.yml文件
 2. 导入以下敏感内容：
    ```
    simbot:
      core:
        bots: 你的机器人QQ账户:你的机器人QQ密码
    spring:
      datasource:
        url: url
        username: username
        password: password
        driver-class-name: com.mysql.cj.jdbc.Driver
    email:
      username: email@email.com
      auth-code: auth-code
    ```
 3. 在你希望部署的地方运行她。Done！

 另，根据Simbot框架的官方要求，你的java版本最好要*高于*1.8.0_131。
 #### 如何反馈Bug？
 通过邮件联系我：stardust.whc@gmail.com，或通过Issues。
 ##### 感谢您的阅读和支持。