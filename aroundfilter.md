etmvc中支持前置过滤器，后置过滤器和环绕过滤器，前面介绍过前置过滤器了，请参阅《etmvc的过滤器基础》。

环绕过滤器是功能最强的一类过滤器，允许拦截action方法的执行前和执行后，这实际上就是一种AOP。所以通过环绕过滤器，我们可以在action方法执行前和执行后处理一些逻辑，甚至中断action的执行，可以用它做日志处理、异常处理等。

etmvc中创建一个环绕过滤器同前置过滤器有些不同，前置过滤器只要简单在控制器中编写一个方法就行了，环绕过滤器必须是单独的一个类，这个类要求实现AroundHandler接口，或者继承AbstractAroundHandler。我们先来看个简单的环绕过滤器例子：

```
public class TestAroundFilter implements AroundHandler{
	
	@Override
	public boolean before(Controller controller) throws Exception {
		System.out.println("begin invoke:" + controller.getActionName());
		return true;
	}

	@Override
	public boolean after(Controller controller) throws Exception {
		System.out.println("after invoke:" + controller.getActionName());
		return true;
	}

}
```

其中before和after分别是在action方法执行之前和之后执行，如果返回true则继续后续代码执行，如果返回false则中断后续代码执行，所以如果before返回false将中止action方法的执行。

如此，利用环绕过滤器，我们完全能够控制action方法之前和之后的逻辑，只要在before和after中编写处理逻辑的代码就行了。如若要记录日志，只要在before中记录action开始执行的时间，在after中记录action执行完成的时间，就能够清楚执行那个action，什么时间开始执行，什么时间结束执行，执行了多长时间等。

好了，我们来看看这个环绕过滤器怎样安装到控制器上，看下面的示例：

```
@AroundFilter(execute=TestAroundFilter.class)
public class HelloController extends ApplicationController{
	public String say() throws Exception{
		return "hello,world";
	}
}
```

用@AroundFilter注解就能将环绕过滤器安到控制器上，注意到这里的execute是类，而前置过滤器和后置过滤器是方法名称。如果需要安多个环绕过滤器，用@AroundFilters就好了。

在上面例子中，我们执行http://localhost:8080/xxx/hello/say时，将在TOMCAT控制台输出：

begin invoke:say

after invoke:say


