# etmvc框架介绍 #

如今的Java Web开发对于需求来说已经变得过于复杂。当今众多Java领域的Web开发框架不仅使用复杂，而且并没有很好的遵循Don’t Repeat Yourself（DRY）原则。

**一、什么是etmvc？**

etmvc是一套轻量级简易高效的WEB开发框架，严格遵循MVC的思想。et一词源于1982年斯皮尔伯格执导的一部温馨科幻片《E.T.》(外星人)，意思就是来自外星人的，不受束缚的MVC，开发者可以快乐地做WEB开发，而不受传统的烦杂折磨。

**二、etmvc框架定位**

我们给这个框架的定位如下：

  * 简易：代码要简单，开发要容易。约定优于配置，再也没有XML的配置之苦。
  * 性能：在满足功能的前提下尽量地提高性能。
  * 实用：没有太多花哨的东西，一切从实用的角度考虑。

**三、授权协议**

etmvc框架采用LGPL授权。

**四、etmvc框架的组成**

etmvc框架包括mvc和一个可选的orm实现，可选的orm实现是一个ActiveRecord框架，独立于mvc，可以在非WEB的应用程序中使用。

**五、etmvc框架的安装**

  1. 获取最新的框架：从本站获取最新的etmvc框架。
  1. 建立WEB项目，将下载的压缩文件解压至项目的/WEB-INF/lib目录中。
  1. 配置数据库，在/WEB-INF/classes目录中建立数据库连接配置文件activerecord.properties， 配置示例：
```
domain_base_class=com.et.ar.ActiveRecordBase  
  
com.et.ar.ActiveRecordBase.driver_class=com.mysql.jdbc.Driver  
com.et.ar.ActiveRecordBase.url=jdbc:mysql://localhost/mydb  
com.et.ar.ActiveRecordBase.username=root  
com.et.ar.ActiveRecordBase.password=soft123456  
com.et.ar.ActiveRecordBase.pool_size=2 
```
  1. 配置/WEB-INF/web.xml，添加一个过滤器，配置示例：
```
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

**六、etmvc框架的基本概念**
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