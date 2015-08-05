我们来探讨etmvc如何使用视图，前面关于“Action方法”的介绍中我们提到，每个请求将会映射到一个Action方法。etmvc将根据Action方法的返回类型来决定使用何种视图，大体有以下三种：

  1. 返回void时将使用JSP视图。
  1. 返回String时将字符串直接输出至浏览器。
  1. 返回View或其子类时将使用对应的视图。

下面我们来分别说明，如有如下的action方法：

```
public class UserController extends ApplicationController{
    public void test1(){
        request.setAttribute("hello", "hello,test1");
    }
}
```

action方法“test1”的返回类型是void，这时etmvc将其解释为JSP视图，将会查找/views/user/test1.jsp的文件，文件内容：

```
    <body>
        <h1>Hello World!</h1>
        <p>${hello}</p>
    </body>
```

我们来编写返回类型是String的action方法：

```
public class UserController extends ApplicationController{
    public String test2(){
        return "hello,test2";
    }
}
```

这时会将返回字符串“hello,test2”直接输出至浏览器。

如果返回类型是View或其子类型，则etmvc会使用其定义的视图，如下所示：

```
public class UserController extends ApplicationController{
    public JspView test3(){
        JspView view = new JspView();
        view.setAttribute("hello", "hello,test3");
        return view;
    }

    public JspView test4(){
        JspView view = new JspView("/common/other.jsp");
        view.setAttribute("hello", "hello,test4");
        return view;
    }
}
```

上面例子中test3使用默认的JSP视图位置和目录/user/test3.jsp，而test4使用指定的视图位置和目录/common/other.jsp。

我们现来看个JsonView的例子，JsonView能够处理多种数据结构，能够将其正确地转换成客户端需要的JSON串，这在AJAX 的开发中非常有用，同EXTJS整合时也会很容易，如下所示：

```
public class UserController extends ApplicationController{
    public JsonView test5(){
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        result.put("msg", "hello,test5");
        JsonView view = new JsonView(result);
        view.setContentType("text/html;charset=utf-8");//允许指定ContentType
        return view;
    }
}
```

上面 例子运行结果将向浏览器输出{"msg":"hello,test5","success":true}。

我们来总结一下，etmvc目前支持的视图包括：

  * JspView
  * TextView
  * FreeMarkerView
  * BinaryView
  * JsonView