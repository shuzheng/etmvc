etmvc中ActiveRecord（下称AR）在使用上可以独立使用，其数据库的连接信息通过activerecord.properties进行配置。
AR提供一个简单的连接池实现，如果需要使用更高效的连接池，则可以利用spring来进行配置。AR集成spring分二步进行：

1、配置spring的连接池

```
       <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
           <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
           <property name="url" value="jdbc:mysql://localhost/mydb"/>
           <property name="username" value="root"/>
           <property name="password" value="soft123456"/>
       </bean>
```

2、配置AR的连接工厂
```
       <bean id="ds1" class="com.et.ar.ConnectionFactoryBean">
           <property name="domainBaseClass" value="com.et.ar.ActiveRecordBase"/>
           <property name="adapterClass" value="com.et.ar.adapters.MySqlAdapter"/>
           <property name="dataSource" ref="dataSource"/>
       </bean>
```

这样就完成了切换数据源的操作，下面再给出一个使用多数据库的配置实例

```
    <bean id="dataSource1"
          class="org.springframework.jdbc.datasource.DriverManagerDataSource"
          p:driverClassName="com.microsoft.sqlserver.jdbc.SQLServerDriver"
          p:url="jdbc:sqlserver://192.168.10.21:1433;databaseName=smqxt;user=data1;password=data1qaz"
          p:username="data1"
          p:password="data1qaz" />
    <bean id="dataSource2"
          class="org.springframework.jdbc.datasource.DriverManagerDataSource"
          p:driverClassName="com.mysql.jdbc.Driver"
          p:url="jdbc:mysql://localhost/mydb"
          p:username="root"
          p:password="soft123456" />
          
    <bean id="connection1" 
          class="com.et.ar.ConnectionFactoryBean"
          p:domainBaseClass="com.et.ar.ActiveRecordBase"
          p:dataSource-ref="dataSource1" />
    <bean id="connection2" 
          class="com.et.ar.ConnectionFactoryBean"
          p:domainBaseClass="javaapplication1.User"
          p:dataSource-ref="dataSource2" />
```