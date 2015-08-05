我们举个简单的例子说明一下从浏览器发送请求到服务器处理完请求返回信息给浏览器的过程。

1、浏览器发出http://localhost:8084/myweb/user/list这个请求，服务器将从这个URL分析出如下信息：

myweb：上下文路径信息

User：控制器信息

list：动作信息

2、服务器根据这个信息查找控制器UserController中的list方法，并执行之。

3、服务器将查找名称是list.jsp的视图并将处理结果传递到视图，完成渲染过程。

整个处理过程简单来说就是这样。

每个请求都会创建新的控制器实例，控制器的类名必须以Controller结尾，必须继承Controller类，比如ApplicationController, HelloController等，控制器的Action方法允许继承，我们一般都会创建一个根控制器，然后让其他控制器统一继承这个根控制器。

每个控制器允许有多个Action操作，这些操作将映射到相应的URL上。比如有如下的控制器：

```
public class UserController extends ApplicationController{
    public void create(){
    }

    public View save(User user) throws Exception{
    }
    
    public void login(String name, String password) throws Exception{
    }

    public void logout() throws Exception{
    }
}
```
将相应的URL 映射到控制器的Action方法上：

|URL|Action方法|
|:--|:-------|
|/user/create |create  |
|/user/save |save    |
|/user/login |login   |
|/user/logout| logout |

至此，我们看到编写控制器处理WEB请求就是这样简单。

控制器的Action方法接受不同的参数，这些参数将自动绑定到Request的参数，方法可以返回不同的类型，比如void, String, JsonView, BinaryView等，etmvc将据此确定处理后以何种视图返回。

下面我们以一个用户登录的例子来说明控制器的一般用法：

1、建立控制器，如下所示：
```
package controllers;

public class UserController extends ApplicationController{
    public void login(){

    }

    public String handleLogin(String username, String password) throws Exception{
        return "你输入的用户：" + username + "密码：" + password;
    }
}
```

我们定义了二个Action方法，一个是login，该方法返回值是void，系统默认将寻找/views/user/login.jsp的视图进行显示，另一个是handleLogin，该方法将简单地将用户登录信息显示出来。

2、我们来建立login.jsp视图：
```
        <form action="<c:url value="/user/handleLogin"/>" method="POST">
            <p>用户名：<input type="text" name="username"></p>
            <p>密码：<input type="password" name="password"></p>
            <p><input type="submit" value="提交"></p>
        </form>
```

我们看到FORM中的action的URL指向，这个URL将映射到我们控制器中的handleLogin方法，而该方法将返回String类型，etmvc将其解释了文本视图，所以将会在浏览器上显示登录的信息。