etmvc框架使用路由技术实现把URL映射到控制器类中的action方法上，典型的http://localhost:8080/xxx/user/show将映射到UserController类中的show方法上，实际上这个规则是允许改变的。etmvc框架将允许你自定义自已的匹配规则来映射你的控制器类及其行为，这就需要定义路由。

一个路由的定义由一些占位符组成，占位符由美元符后面跟着字母组成，如“$controller/$action/$id”，这是框架采用的默认路由。根据这个路由，下面的这些例子将被匹配：

|URL| CONTROLLER| ACTION |ID |
|:--|:----------|:-------|:--|
|/user| UserController| index  |   |
|/user/show| UserController| show   |   |
|/blog/show/123| BlogController| show   |123 |


如果没匹配到$action，则将默认使用index方法。

定义一个新的路由时，必须实例化Route，如下面的这个例子：

```
		Route route = new Route("blog/$year/$month/$day", DefaultRouteHandler.class);
		route.setController("blog");
		route.setAction("show");
		RouteTable.addRoute(0, route);
```

其中我们定义了嵌入式变量$year,$month,$day，这个路由规划将能够映射到BlogController类中的方法：

```
	public String show(int year, int month, int day) {
		return year + "-" + month + "-" + day;
	}
```

嵌入式变量将自动映射成方法的参数。

可以定义多个路由规则，匹配是顺序进行的，也将是在路由表中从第一个规则开始进行匹配，找到就按照这个路由查找控制器类和方法。

在上面的例子中，这些URL将会有如下的映射：

|URL| CONTROLLER| ACTION |
|:--|:----------|:-------|
|/blog/2009/07/10| BlogController| show   |
|/user/list| UserController| list   |
|/product/show| ProductController| show   |


利用路由技术可以提供非常优雅的URL，一看URL就知道是那个控制器类和方法在处理。

最后有一点需要注意的是：定义一个路由后必须将它加入路由表中，并且确保在应用程序启动时是可用的。
