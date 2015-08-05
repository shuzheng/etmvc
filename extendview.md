etmvc内置了多种常用的视图，每种视图都有对应的renderer对象，视图对象中通过加上@ViewRendererClass注解将renderer对象关联起来。比如JspView有相应的JspViewRenderer，JsonView有对应的JsonViewRenderer。

如果要使用自定义视图，则可以扩展视图，扩展视图是很简单的事情，我们需要做二件事情：一是定义视图对象，二是定义renderer对象。

假如我们要来定义一个JavaScriptView视图，用于向页面输出并执行一段JavaScript代码。首先定义视图对象，视图对象建议继承View对象：

```
@ViewRendererClass(JavaScriptViewRenderer.class)
public class JavaScriptView extends View{
    private String js;

    public JavaScriptView(String js){
        this.js = js;
    }

    public String getJs() {
        return js;
    }

    public void setJs(String js) {
        this.js = js;
    }
}
```

我们看到，视图对象仅是一个定义了需要的数据载体，其渲染输出是通过renderer对象完成的。下面看来一下renderer对象的定义：
```
public class JavaScriptViewRenderer extends AbstractViewRenderer<JavaScriptView>{
    public void renderView(JavaScriptView view, ViewContext context) throws Exception{
        PrintWriter out = context.getResponse().getWriter();
        out.print("<script>"+view.getJs()+"</script>");
        out.close();
    }
}
```

renderer对象扩展了AbstractViewRenderer ，视图的渲染就是通过renderView方法实现的。

好了，自定义的JavaScriptView扩展完成了，我们来看看怎样使用：

```
public class TestController extends ApplicationController{
    public View jstest(){
        JavaScriptView view = new JavaScriptView("alert(&apos;abc&apos;);");
        return view;
    }
}
```

我们在控制器中定义一个Action方法，只要将该方法的返回类型设为JavaScriptView就行了。