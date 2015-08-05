etmvc遵循“约定优于配置”的原则，通过文件的命名及存放位置来代替显式的配置，避免编写烦杂的XML配置文件。

etmvc的配置只有一处，即在web.xml中配置一个filter，如下所示：

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
        <init-param>
            <param-name>plugin</param-name>
            <param-value>plugin.OcrServer</param-value>
        </init-param>
        </filter>
    <filter-mapping>
        <filter-name>etmvc</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
```

其中，filter的初始参数有三个：controllerBasePackage, viewBasePath, plugin，说明如下：

1 controllerBasePackage是控制器的基包名称，如controllers，所有的控制器类必须在controllers包中，或者在controllers的子包中。控制器类必须以Controller结尾，必须继承Controller，比如有如下的控制器类：
```
package controllers;

public class ArticleController extends ApplicationController{
    public View showImage(int id) throws Exception{
        //...
    }

    public View download(int id) throws Exception{
       //...
    }
    
    public void create(){

    }

}
```
控制器包名是controllers，控制器类名是ArticleController，有showImage等Action方法。

2 viewBasePath是存放视图模板的位置，如下所示：
视图模板的目录结构有一定的规则，在［viewBasePath］目录下是控制器名称（小写），再往下是对应每个Action方法的视图文件。如ArticleController控制器中的方法create对应到/article/create.jsp视图文件，即执行控制器的create方法后，etmvc根据执行的结果找到对应的视图进行渲染。

3 plugin是插件的配置，一般情况下无须用到，所以不用配置该项，关于插件的使用留到后面的章节再作介绍。