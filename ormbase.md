etmvc中访问数据可以使用JDBC,HIBERNATE等，鉴于JDBC的烦琐和HIBERNATE的复杂，我们同时提供了一个ORM的简易实现版本ActiveRecord。在大多数中小型WEB系统中，使用ActiveRecord就足够了。

1、使用前须将et-ar.jar, asm.jar, cglib.jar等包引入项目，然后进行配置activerecord.properties：

```
domain_base_class=com.et.ar.ActiveRecordBase

com.et.ar.ActiveRecordBase.driver_class=com.mysql.jdbc.Driver
com.et.ar.ActiveRecordBase.url=jdbc:mysql://localhost/mydb
com.et.ar.ActiveRecordBase.username=root
com.et.ar.ActiveRecordBase.password=soft123456
com.et.ar.ActiveRecordBase.pool_size=2
```
在上面配置中我们配置了MYSQL数据库连接，配置文件activerecord.properties放在CLASSPATH能找到的地方就好。
2、我们来建立一张数据表：
```
create table users(
id int primary key auto_increment,
name varchar(10) default null,
addr varchar(50) default null,
email varchar(50) default null,
remark varchar(50) default null
)
```

然后建立对应的域对象：

```
@Table(name="users")
public class User extends ActiveRecordBase{
    @Id private Integer id;
    @Column private String name;
    @Column private String addr;
    @Column private String email;
    @Column private String remark;
    //get,set...
}
```
我们的域模型对象继承自ActiveRecordBase，到些，ORM就建立完成了，我们看到，不需要复杂的配置文件，仅用几个简单的注解就完成了。

3、基本的CRUD操作

增加记录：
```
        User user = new User();
        user.setName("name1");
        user.setAddr("addr1");
        user.setEmail("name1@gmail.com");
        user.save();
```
修改记录：
```
        User user = User.find(User.class, 3);
        user.setRemark("user remark");
        user.save();
```

删除记录：

```
        User user = User.find(User.class, 3);
        user.destroy();
```

查询记录：
```
        List<User> users = User.findAll(User.class);
        for(User user: users){
            System.out.println(user.getName());
        }
```

条件查询：
```
        List<User> users = User.findAll(User.class, "addr like ?", new Object[]{"%百花路%"});
        for(User user: users){
            System.out.println(user.getName());
        }
```
我们看到，借助ActiveRecord，操作数据是如此容易。
