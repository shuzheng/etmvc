# etmvc
一个简易高效的web开发框架

  etmvc是基于Java技术，为WEB开发提供的一套简易MVC框架。
  简易但不简单，其设计和实现借鉴业界众多的优秀框架，如struts,spring mvc,ror,grails等， 力图为Java开发人员提供一套高效的开发框架。

* 基于java技术开发，继承其强大、稳定、安全、高效、跨平台等多方面的优点
* 约定优于配置，免除开发过程中的配置之苦
* 具有良好的自我扩展能力
* 易于同spring等IOC框架进行集成
* 从实用的角度出发，精益求精，从实际开发经验中提取有用的模型
* 设计专门同EXTJS等框架进行整合的机制

## 下载
  [etmvc-1.0.1-bin.rar](http://shuzheng.github.io/etmvc/Download/etmvc-1.0.1-bin.rar)
  
  **SHA1 Checksum:**2188ae15741e4148423d04626f2589b312c3698a

## 使用教程

* [etmvc框架介绍](https://github.com/shuzheng/etmvc/blob/master/Wiki/about.md)
* [Hello,World经典示例](https://github.com/shuzheng/etmvc/blob/master/Wiki/helloworld.md)
* [关于etmvc的配置](https://github.com/shuzheng/etmvc/blob/master/Wiki/configuration.md)
* [理解并使用控制器](https://github.com/shuzheng/etmvc/blob/master/Wiki/controller.md)
* [Action方法和控制器环境](https://github.com/shuzheng/etmvc/blob/master/Wiki/action.md)
* [关于etmvc的视图](https://github.com/shuzheng/etmvc/blob/master/Wiki/view.md)
* [扩展etmvc的视图](https://github.com/shuzheng/etmvc/blob/master/Wiki/extendview.md)
* [利用etmvc中的模型绑定简化Action方法的编写](https://github.com/shuzheng/etmvc/blob/master/Wiki/binders.md)
* [ORM-ActiveRecord基础](https://github.com/shuzheng/etmvc/blob/master/Wiki/ormbase.md)
* [利用etmvc编写用户管理小例子](https://github.com/shuzheng/etmvc/blob/master/Wiki/user_admin_example.md)
* [ActiveRecord中同时访问多个数据库](https://github.com/shuzheng/etmvc/blob/master/Wiki/multi_database.md)
* [ActiveRecord中的关联](https://github.com/shuzheng/etmvc/blob/master/Wiki/relation.md)
* [etmvc中进行上传和下载](https://github.com/shuzheng/etmvc/blob/master/Wiki/upload_download.md)
* [etmvc和extjs结合分页例子](https://github.com/shuzheng/etmvc/blob/master/Wiki/pagination.md)
* [etmvc的过滤器基础](https://github.com/shuzheng/etmvc/blob/master/Wiki/filters.md)
* [ActiveRecord中集成spring](https://github.com/shuzheng/etmvc/blob/master/Wiki/activerecord_spring.md)
* [ActiveRecord中使用事务](https://github.com/shuzheng/etmvc/blob/master/Wiki/transaction.md)
* [etmvc中使用环绕过滤器](https://github.com/shuzheng/etmvc/blob/master/Wiki/aroundfilter.md)
* [ActiveRecord中的数据类型映射](https://github.com/shuzheng/etmvc/blob/master/Wiki/activerecord_datatype.md)
* [ActiveRecord中的回调方法](https://github.com/shuzheng/etmvc/blob/master/Wiki/activerecord_callback.md)
* [etmvc框架中的插件](https://github.com/shuzheng/etmvc/blob/master/Wiki/plugin.md)
* [etmvc框架对URL路由的支持](https://github.com/shuzheng/etmvc/blob/master/Wiki/route.md)
* [etmvc中使用环绕过滤器处理异常](https://github.com/shuzheng/etmvc/blob/master/Wiki/aroundfilter_exception.md)
* [etmvc中的国际化处理](https://github.com/shuzheng/etmvc/blob/master/Wiki/i18n.md)
* [etmvc框架集成spring](https://github.com/shuzheng/etmvc/blob/master/Wiki/etmvc_spring.md)

## 快速开始

**etmvc框架的组成**

etmvc框架包括mvc和一个可选的orm实现，可选的orm实现是一个ActiveRecord框架，独立于mvc，可以在非WEB的应用程序中使用。

**etmvc框架的安装**

  1. 获取最新的框架：从本站获取最新的etmvc框架。
  1. 建立WEB项目，将下载的压缩文件解压至项目的/WEB-INF/lib目录中。
  1. 配置数据库，在/WEB-INF/classes目录中建立数据库连接配置文件activerecord.properties， 配置示例：
```java
domain_base_class=com.et.ar.ActiveRecordBase  

com.et.ar.ActiveRecordBase.driver_class=com.mysql.jdbc.Driver  
com.et.ar.ActiveRecordBase.url=jdbc:mysql://localhost/mydb  
com.et.ar.ActiveRecordBase.username=root  
com.et.ar.ActiveRecordBase.password=soft123456  
com.et.ar.ActiveRecordBase.pool_size=2 
```
  1. 配置/WEB-INF/web.xml，添加一个过滤器，配置示例：
```xml
<filter>
    <filter-name>etmvc</filter-name>
    <filter-class>com.et.mvc.DispatcherFilter</filter-class>
    <init-param>
        <param-name>controllerBasePackage</param-name>
        <param-value>controllers</param-value>
    </init-param>
    <init-param>
        <param-name>viewBasePath</param-name>
        <param-value>/views</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>etmvc</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```
  1. 建立保存视图模板的目录/views。

**etmvc框架的基本概念**
  1. controller：控制器是属于请求范围的，用于处理请求，创建或者准备响应。每次请求都会创建一个控制器实例，控制器的类名必须以Controller结尾，一般整个应用程序会创建一个控制器的基类
  1. ApplicationController，然后具体的其它控制器再继承之。
  1. action：每个URL操作将映射到一个action上，一个action是一个控制器的方法，一个控制器可以管理彼此相关的多个action。一个控制器中标准的action命名参考：
    * index： 默认的动作
    * show：显示动作
    * create：新建动作
    * save：保存动作
    * edit：修改动作
    * update：更新动作
    * destroy：删除动作
  1. model：模型，是一个数据实体，将对应到具体的数据表中，这种映射关系是通过ActiveRecord实现的。所以数据表中的字段名就是模型对象中的属性名，不再需要用XML配置描述了。
  1. view：视图，etmvc支持多种视图，甚至一个action多视图，最常用的视图是JspView，在AJAX应用中是JsonView，下载处理二进制数据时是BinaryView，等等。

**etmvc实现代码**

  接下来，我们开始编写控制器HelloController，一般我们会编写控制器基类ApplicationController，我们的HelloController会继承它。注意到，控制器的包名是controllers，这就是前面配置中的controllerBasePackage配置值。
```java
package controllers;

import com.et.mvc.Controller;

public class ApplicationController extends Controller{

}
```
```java
package controllers;

import com.et.mvc.TextView;

public class HelloController extends ApplicationController{
    public TextView say(){
        return new TextView("hello,world");
    }
}
```

至此，我们的Hello,World程序编写完毕，部署后在浏览器地址栏输入http://localhost:8080/helloworld/hello/say，将会输出hello,world字样。


## 声明

  首先感谢作者`stworthy`分享的etmvc项目，由于作者已停止更新etmvc且托管的Google Code要于2016年1月25日关闭，故本人将项目转过来接手并加以维护并更新。

## License

  LGPL
