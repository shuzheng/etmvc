当请求到达时，etmvc将创建控制器对象，控制器对象会查找与“被请求的action”同名的public实例方法。如此看来，控制器的Action方法是允许被继承的。如果你希望某些方法不被作为action调用，可以将其声明为protected或者private。比如有如下的控制器：

```
public class BlogController extends ApplicationController{
    public String show(){
        return "show method";
    }

    protected String create(){
        return "create method";
    }
}
```

当访问/blog/show时将输入框“show method” ，而访问/blog/create时将有“The requested resource (/test1/blog/create) is not available”的信息。

Action方法允许使用控制器环境提供的一些对象：

  * request
  * response
  * session
  * servletContext
  * controllerPath
  * controllerName
  * actionPath
  * flash
  * exception
他们的作用应该不言自明，其中flash对象的使用方法我们将分出一个主题专门作介绍。