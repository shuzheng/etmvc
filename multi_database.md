我们先来看一下ActiveRecord（下简称AR）的基本配置：

```
domain_base_class=com.et.ar.ActiveRecordBase

com.et.ar.ActiveRecordBase.driver_class=com.mysql.jdbc.Driver
com.et.ar.ActiveRecordBase.url=jdbc:mysql://localhost/mydb
com.et.ar.ActiveRecordBase.username=root
com.et.ar.ActiveRecordBase.password=soft123456
com.et.ar.ActiveRecordBase.pool_size=2
```

其中的配置项domain\_base\_class是我们域模型对象的基类，我们在定义模型类时必须让其继承ActiveRecordBase，AR将根据此找到对应的数据库连接。

如果我们想同时使用多个数据库，这时我们可以先定义二个基类：

```
public class Base1 extends ActiveRecordBase{
}
public class Base2 extends ActiveRecordBase{
}
```

然后进行配置：

```
domain_base_class=models.Base1 models.Base2

models.Base1.driver_class=com.mysql.jdbc.Driver
models.Base1.url=jdbc:mysql://localhost/mydb1
models.Base1.username=root
models.Base1.password=soft123456
models.Base1.pool_size=2

models.Base2.driver_class=com.mysql.jdbc.Driver
models.Base2.url=jdbc:mysql://localhost/mydb2
models.Base2.username=root
models.Base2.password=soft123456
models.Base2.pool_size=2
```

我们只要让我们的模型类继承Base1或Base2，就能正确使用对应的数据库连接。如果那一天又要改回去连接一个数据库了，只要改一下这个activerecord.properties属性文件就OK了。

AR中同时访问多个数据库时是不是很简单。