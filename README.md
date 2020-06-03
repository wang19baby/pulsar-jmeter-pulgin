# pulsar-jmeter-plugin

#### 介绍
次项目基于jmeter压力测试工具的插件模板。开发针对apache pulsar消息队列的压力测试插件

#### 软件架构
jmeter 3.2
pulsar-client 2.5.0


#### 安装教程

1.  将 extJar 放到 jmeter_home\lib\下
2.  将生成的 jar 放到 jmeter_home\lib\ext\下
3.  启动jmeter3.2

#### 使用说明
可以添加CSV Data Set Config使用 [字段] 替换发布者中的对应变量值 ${变量名}
偶发会有lz4-1.3.0.jar找不到情况


添加发布者
1.  添加 Thread Group
2.  在其下添加 Sampler 下 Pulsar Producer
    
3.  pulsar brokers 
        填写 [pulsar://192.168.1.2:6650,192.168.1.3:6650]  or [http://192.168.1.2:8080,192.168.1.3:8080]
    pulsar topic 
        填写需要发布消息的主题 my-topic-test
    request data 
        填写需要发布的消息主体，会使用getByte("UTF-8"")发送

添加消费者(Shared 模式)
1.  添加 Thread Group
2.  在其下添加 Sampler 下 Pulsar Consumer
    
3.  pulsar brokers 
        填写 [pulsar://192.168.1.2:6650,192.168.1.3:6650]  or [http://192.168.1.2:8080,192.168.1.3:8080]
    pulsar topic 
        填写需要发布消息的主题 my-topic-test
    subscription 
        子标题，子组，用户分组

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
