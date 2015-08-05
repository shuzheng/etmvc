我们来编写一个用户资料管理的小程序，旨在说明etmvc的基本用法。需具备的基础：

  * 理解基本控制器
  * ActiveRecord操作基础
现在建立一个测试用的表结构：

```
create table users(
id int primary key auto_increment,
name varchar(10) default null,
addr varchar(50) default null,
email varchar(50) default null,
remark varchar(50) default null
)
```
接下来建立对应的模型对象：
```
@Table(name="users")
public class User extends ActiveRecordBase{
    @Id private Integer id;
    @Column
    @NotEmpty(message="用户名称必须填写")
    private String name;
    @Column private String addr;
    @Column
    @Email(message="邮箱格式不对")
    private String email;
    @Column private String remark;
    //get,set...
}
```

注意到我们对name, email进行了有效性约束。

现在，我们来编写控制器代码，包括了用户管理的CRUD操作：

```
public class UserController extends ApplicationController{
    /**
     * 显示用户列表页面
     */
    public void index() throws Exception{
        List<User> users = User.findAll(User.class);
        request.setAttribute("users", users);
    }

    /**
     * 新建用户页面
     */
    public void create(){
    }

    /**
     * 保存新增用户
     */
    public JspView save(User user) throws Exception{
        if (user.save()){
            redirect("index");  //重定向到列表页面
            return null;
        }
        else{
            return new JspView("create.jsp", "user", user);
        }
    }
    
    /**
     * 修改指定的用户资料页面
     */
    public void edit(int id) throws Exception{
        User user = User.find(User.class, id);
        request.setAttribute("user", user);
    }
    
    /**
     * 更新指定的用户资料
     */
    public JspView update(int id) throws Exception{
        User user = User.find(User.class, id);
        user = User.updateModel(user, request.getParameterMap());
        if (user.save()){
            redirect("index");  //重定向到列表页面
            return null;
        }
        else{
            return new JspView("edit.jsp", "user", user);
        }
    }
    
    public void destroy(int id) throws Exception{
        User user = User.find(User.class, id);
        user.destroy();
        redirect("index");  //重定向到列表页面
    }
}
```

最后，是JSP视图，先来看用户列表的视图index.jsp：

```
        <h1>用户列表</h1>
        <table border="1">
            <tr>
                <th>名称</th>
                <th>地址</th>
                <th>邮箱</th>
                <th>备注</th>
                <th style="width:80px;">操作</th>
            </tr>
            <c:forEach items="${users}" var="user">
                <tr>
                    <td>${user.name}</td>
                    <td>${user.addr}</td>
                    <td>${user.email}</td>
                    <td>${user.remark}</td>
                    <td>
                        <a href="<c:url value="/user/edit/${user.id}"/>">修改</a>
                        <a href="<c:url value="/user/destroy/${user.id}"/>">删除</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <a href="<c:url value="/user/create"/>">增加</a>
```

新建用户资料页面create.jsp：

```
        <h1>新增用户资料</h1>
        <form action="<c:url value="/user/save"/>" method="POST">
            <jsp:include page="form.jsp"/>
            <input type="submit" value="提交">
            <input type="reset" value="重置">
        </form>
```

修改用户资料页面edit.jsp：

```
        <h1>修改用户资料</h1>
        <form action="<c:url value="/user/update/${user.id}"/>" method="POST">
            <jsp:include page="form.jsp"/>
            <input type="submit" value="提交">
            <input type="reset" value="重置">
        </form>
```

新增和修改页面共用了一个表单form.jsp：

```
<div>
    <c:if test="${fn:length(user.errors)>0}">
        <ul>
            <c:forEach items="${user.errors}" var="err">
                <li style="color:red;">${err}</li>
            </c:forEach>
        </ul>
    </c:if>
</div>
<table>
    <tr>
        <th>名称</th>
        <td><input type="text" name="name" value="${user.name}"></td>
    </tr>
    <tr>
        <th>地址</th>
        <td><input type="text" name="addr" value="${user.addr}"></td>
    </tr>
    <tr>
        <th>邮箱</th>
        <td><input type="text" name="email" value="${user.email}"></td>
    </tr>
    <tr>
        <th>备注</th>
        <td><input type="text" name="remark" value="${user.remark}"></td>
    </tr>
</table>
```

至此，程序编写完毕。
