etmvc框架拥有一套插件体系结构，如果需要扩展某些功能，就可以通过插件来完成。etmvc框架在初始化时会扫描安装进来的插件，如果发现有插件就进行加载。

举个例子，在WEB应用程序中如若需要一个后台服务进程处理一些事情，则使用插件提供的机制可能很合适，又如，在需要整个WEB应用程序加载之前执行一些代码时，也可以利用插件来完成，因为插件的启动执行是在etmvc框架初始化时完成的。

编写插件分二个步骤：

1、编写插件实现代码，要求实现com.et.mvc.PlugIn接口，如：

```
public class RouteLoader implements PlugIn{

	@Override
	public void destroy() {
	}

	@Override
	public void init(PlugInContext ctx) {
		Route route = new Route("blog/$year/$month/$day", DefaultRouteHandler.class);
		route.setController("blog");
		route.setAction("show");
		RouteTable.addRoute(0, route);
	}

}
```

其中init方法做一些初始化的工作，destroy作一些销毁的工作。

2、注册插件，在web.xml中增加一个plugin的参数：

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
  		<param-value>utils.RouteLoader</param-value>
  	</init-param>
  </filter>
  <filter-mapping>
  	<filter-name>etmvc</filter-name>
  	<url-pattern>/*</url-pattern>
  </filter-mapping>
```

plugin参数值为插件实现类的名称，如果有多个插件，则以“,”分开。

3、重启WEB容器，这样就能正确加载插件了。
