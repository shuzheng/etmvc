我们以一个用户注册的例子来说明模型绑定问题。首先，建立一个用户注册表单：

```
        <h1>用户注册</h1>
        <form action="<c:url value="/user/save"/>" method="POST">
            <p>名称：<input type="text" name="name"></p>
            <p>密码：<input type="password" name="password"></p>
            <p>确认密码：<input type="password" name="confirmPassword"></p>
            <p>邮箱：<input type="text" name="email"></p>
            <p>电话：<input type="text" name="phone"></p>
            <p><input type="submit" value="提交"> <input type="reset" value="重置"></p>
        </form>
```
现在编写控制器用以处理表单：

```
public class UserController extends ApplicationController{
    /**
     * 用户注册页面
     */
    public void register(){
    }
    
    public String save(){
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        if (!password.equals(confirmPassword)){
            return "确认密码不对。";
        }
        String info = "name:"+name+"<br/>"
                + "password:"+password+"<br/>"
                + "email:"+email+"<br/>"
                + "phone:"+phone;
        return info;
    }
}
```
我们可以通过控制器环境提供的request对象获取表单数据，进而进行处理。

但我们有更好的处理方法，我们建立一个User对象来描述表单，这个相当于struts中的ActionForm，

```
public class User {
    private String name;
    private String password;
    private String email;
    private String phone;
    private String confirmPassword;
    //get set...
}
```
现在，我们改写控制器中的save方法：

```
public class UserController extends ApplicationController{
    /**
     * 用户注册页面
     */
    public void register(){
    }
    
    public String save(User user){
        if (!user.getPassword().equals(user.getConfirmPassword())){
            
        }
        String info = "name:"+user.getName()+"<br/>"
                + "password:"+user.getPassword()+"<br/>"
                + "email:"+user.getEmail()+"<br/>"
                + "phone:"+user.getPhone();
        return info;
    }
}
```
我们看到etmvc自动将表单数据绑定到Action方法的参数中。我们把定义的User称作模型，etmvc 会根据Action方法的参数自动将请求参数绑定进来，Action方法的参数类型和顺序可以是任意的，对于复杂的对象类型，可以用@Bind注解说明绑定的前缀，如@Bind(prefix="user")User user，这时页面表单项的名称就不能是name,password，而应是user.name, user.password。

etmvc处理大部分常用的数据类型绑定，如果想自已处理数据转换绑定，可以实现DataBinder接口，如：

```
public class DateBinder implements DataBinder{
    public Object bind(BindingContext ctx) throws Exception{
        //...
        return null;
    }
}
```
然后进行注册：

```
DataBinders.register(java.util.Date.class, new DateBinder());
```
这时候就可以在Action方法中使用java.util.Date参数类型了。