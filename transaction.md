ActiveRecord中模型对象调用的每个方法如save, destroy, create, update等都是原子的，都受事务保护。

如果需要保证多个数据操作在一个事务中，则需要使用事务控制，如下所示：

```
try {
	ActiveRecordBase.beginTransaction();
	//do something
	ActiveRecordBase.commit();
	
} catch (Exception ex) {
	ActiveRecordBase.rollback();
}
```

如上所示，ActiveRecord中的事务是通过beginTransaction, commit, rollback等方法进行控制的。