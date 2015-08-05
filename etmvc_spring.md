使用etmvc时必须在web.xml中配置一个Filter，其filter-class是com.et.mvc.DispatcherFilter。如果想集成spring，则必须改成com.et.mvc.SpringDispatcherFilter，看一下集成spring的web.xml配置范例：

```
  <context-param>
  	<param-name>contextConfigLocation</param-name>
  	<param-value>/WEB-INF/applicationContext.xml</param-value>
  </context-param>
  <listener>
  	<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
  </listener>
  <filter>
  	<filter-name>etmvc</filter-name>
  	<filter-class>com.et.mvc.SpringDispatcherFilter</filter-class>
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

下面，我们以用户管理为例子来说明需注意的步骤，首先，编写UserService类：

```
public class UserService {
	public String say(){
		return "say from service";
	}
	
	public List<User> getUsers() throws Exception{
		return User.findAll(User.class);
	}
}
```

我们提供二个方法，一个简单返回一个字符串，一个通过ActiveRecord返回一个用户资料集合。

再来看看控制器中的写法：

```
public class UserController extends ApplicationController{
	private UserService userService;
	
	public String say(){
		return userService.say();
	}
	
	public void show() throws Exception{
		List<User> users = userService.getUsers();
		request.setAttribute("users", users);
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
```

控制器类UserController提供二个action方法，say简单向浏览器输出一个问候信息，show返回一个用户资料页面：

```
	<table border="1">
		<thead>
			<tr>
				<th>用户名称</th>
				<th>地址</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${users }" var="user">
				<tr>
					<td>${user.name }</td>
					<td>${user.address }</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
```

控制器中userService由spring注入，来看看spring配置：

```
    <bean class="controllers.UserController" p:userService-ref="userService" scope="prototype" />
    
    <bean id="userService" class="services.UserService" />
```

至此，程序编写完毕，可以部署运行，试一试执行http://localhost:8080/myweb/user/say和http://localhost:8080/myweb/user/show，将看到问候信息和用户资料列表。