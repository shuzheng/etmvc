我们利用etmvc来建立一个Hello,World的WEB应用程序。

一、首先，建立新的WEB项目，引入et-mvc.jar和paranamer-1.3.jar，配置web.xml，加入一个过滤器，如下所示：

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
我们看到，过滤器com.et.mvc.DispatcherFilter目前只有二个参数，controllerBasePackage指的是控制器的包名，viewBasePath指的是视图模板的存放目录。

二、接下来，我们开始编写控制器HelloController，一般我们会编写控制器基类ApplicationController，我们的HelloController会继承它。注意到，控制器的包名是controllers，这就是前面配置中的controllerBasePackage配置值。
```
        package controllers;

        import com.et.mvc.Controller;

        public class ApplicationController extends Controller{

        }
```
```
        package controllers;

        import com.et.mvc.TextView;

        public class HelloController extends ApplicationController{
            public TextView say(){
                return new TextView("hello,world");
            }
        }
```

三、至些，我们的Hello,World程序编写完毕，部署后在浏览器地址栏输入http://localhost:8080/helloworld/hello/say，将会输出hello,world字样。
