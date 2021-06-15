# QQRobot
 QQRobot是一个基于[Simbot](https://github.com/ForteScarlet/simpler-robot) 框架编写的QQ群群聊机器人。
 ##### 它能做什么？
 它目前仅支持基于saucenao.com的搜图功能，并且不完善。
 ##### 它将能做到什么？
 在计划中，它将可能会支持但不限于以下功能：
 + 通过帮助指令查看可用指令；
 + 通过指令添加自动回复功能；
 + 通过指令限定关键字随机查找Pixiv图片功能；
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
    ```
 3. 在你希望部署的地方运行它。Done！

 另，根据Simbot框架的官方要求，你的java版本最好要*高于*1.8.0_131。
 #### 如何反馈Bug？
 通过邮件联系我：stardust.whc@gmail.com，或通过Issues。
 ##### 感谢您的阅读和支持。