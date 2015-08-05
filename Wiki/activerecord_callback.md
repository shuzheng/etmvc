etmvc中ActiveRecord模型对象拥有很多的操作方法，其中有一类称为回调方法，在ActiveRecord模型对象的生命周期内，回调给予你更多的、更灵活控制能力。回调方法就象一个钩子，它允许在模型对象操作数据的前后执行一段逻辑，这实际就是ActiveRecord模型对象的AOP编程。

ActiveRecord模型对象支持的回调方法有：

|回调方法 |执行时机 |
|:----|:----|
|beforeCreate| 对象创建前执行 |
|afterCreate |对象创建后执行 |
|beforeUpdate |对象更新前执行 |
|afterUpdate| 对象更新后执行 |
|beforeSave |对象保存前执行 |
|afterSave |对象保存后执行 |
|beforeDestroy| 对象删除前执行 |
|afterDestroy |对象删除后执行 |

回调方法签名：public void callbackMethodName() throws ActiveRecordException

我们举个回调方法应用的典型例子，在用户资料管理中，用户的信息除基本的信息外，还包括照片，而照片以文件的形式被保存在某个地方。为保证数据的完整性，在用户资料删除时必须同时删除其关联的照片文件。

我们来看一下用户的模型类定义：

```
@Table(name="users")
public class User extends ActiveRecordBase{
	@Id private Integer id;
	@Column private String name;
	@Column private String phone;
	@Column private String filename;
	
	public void afterDestroy() throws ActiveRecordException{
		String path = getImagePath();
		File file = new File(path);
		if (file.exists() && file.isFile()){
			file.delete();
		}
	}
	
	public String getImagePath(){
		return "d:/temp/" + id + "_" + filename;	//获取照片文件存放路径
	}
	
	//get,set...
}
```

我们重载了afterDestroy，告诉ActiveRecord框架，在记录删除后将相关的照片文件删除。现在来看调用代码：

```
User user = User.find(User.class, 1);
user.destroy();
```

我们无须在调用时编写删除照片文件的代码，仅仅将User对象删除就好，User对象会做相应的回调，执行相关的逻辑。

另外，在ActiveRecord模型对象执行回调方法时是有事务保证的，所以一旦照片文件删除失败，整个对象将执行回滚操作。如此，对于模型对象的调用者来说，这将变得更清晰。