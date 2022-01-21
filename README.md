# 自用工具包

## 如何使用
本工具包已上传到中央仓库，直接引入即可使用
```
<dependency>
    <groupId>cn.xanderye</groupId>
    <artifactId>xander-tool</artifactId>
    <version>1.4</version>
</dependency>
```

## 🤝 特别感谢
特别感谢Jetbrains为本项目赞助License

[![Jetbrains](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg?_gl=1*ng7jek*_ga*NTA3MTc0NTg3LjE2NDEwODQzMDI.*_ga_V0XZL7QHEB*MTY0MjU1NzM4OC40LjEuMTY0MjU1ODI0Mi4w)](https://jb.gg/OpenSourceSupport)

## 工具类详情

### [CodecUtil](src/main/java/cn/xanderye/util/CodecUtil.java)

编码工具类，整合unicode、base64、十六进制编码方法

### [DbUtil](src/main/java/cn/xanderye/util/DbUtil.java)

封装jdbc操作数据库方法

### [EmailUtil](src/main/java/cn/xanderye/util/EmailUtil.java)

邮件工具类，发送邮件

### [FileUtil](src/main/java/cn/xanderye/util/FileUtil.java)

文件操作工具类，支持复制和删除文件/文件夹操作

### [HttpUtil](src/main/java/cn/xanderye/util/HttpUtil.java)

基于Apache HttpClient4.5封装的请求类，支持get请求、post表单请求、post json请求和下载请求，支持携带header、携带cookie和获取cookie。默认不使用连接池，需要请调用 enableConnectionPool

### [IdCardUtil](src/main/java/cn/xanderye/util/IdCardUtil.java)

身份证工具类，计算、校验身份证

### [IoUtil](src/main/java/cn/xanderye/util/IoUtil.java)

流操作工具类

### [LotteryUtil](src/main/java/cn/xanderye/util/LotteryUtil.java)

抽奖工具类 需实例化，支持自定义奖品，按概率抽奖或按奖品总数抽奖

### [MsgPushUtil](src/main/java/cn/xanderye/util/MsgPushUtil.java)

消息推送工具类 依赖HttpUtil，支持Server酱，钉钉机器人和iosBark推送

### [PropertyUtil](src/main/java/cn/xanderye/util/PropertyUtil.java)

配置文件工具类，支持读取配置和写入配置到文件

### [QQUtil](src/main/java/cn/xanderye/util/QQUtil.java)

qq爬虫用到的几个qq加密算法的java版本

### [RSAUtil](src/main/java/cn/xanderye/util/RSAUtil.java)

RSA加密解密工具类

### [StrokeUtil](src/main/java/cn/xanderye/util/StrokeUtil.java)

汉字按笔划排序工具类

### [SystemUtil](src/main/java/cn/xanderye/util/SystemUtil.java)

系统工具类 封装系统的判断和操作，支持cmd命令调用和注册表操作

### [TemplateUtil](src/main/java/cn/xanderye/util/TemplateUtil.java)

模板工具类 基于Freemarker，可根据模板导出文件

### [WechatUtil](src/main/java/cn/xanderye/util/WechatUtil.java)

微信工具类，解密图片

### [ZipUtil](src/main/java/cn/xanderye/util/ZipUtil.java)

zip压缩解压工具类