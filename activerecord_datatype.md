etmvc中的ActiveRecord将数据表中的字段映射成模型类的字段，相应的将数据表中的字段类型映射成模型类的字段类型。

在多数情况下，ActiveRecord能够自动处理从JDBC类型到Java Object类型的映射，此种映射如下表所示：

|JDBC 类型 | Java Object 类型  |
|:-------|:----------------|
|CHAR    | String                 |
|VARCHAR | String              |
|LONGVARCHAR | String          |
|NUMERIC | java.math.BigDecimal|
|DECIMAL | java.math.BigDecimal|
|BIT     | Boolean                 |
|TINYINT | Integer             |
|SMALLINT | Integer            |
|INTEGER | Integer             |
|BIGINT  | Long                 |
|REAL    | Float                  |
|FLOAT   | Double                |
|DOUBLE  | Double               |
|BINARY  | byte[.md](.md)               |
|VARBINARY | byte[.md](.md)            |
|LONGVARBINARY | byte[.md](.md)        |
|DATE    | java.sql.Date          |
|TIME    | java.sql.Time          |
|TIMESTAMP | java.sql.Timestamp|

在类型的映射上可能存在一些需要转换的，比如在MSSQL数据库中定义了一个datetime的字段类型，而在模型类中定义了java.sql.Date类型。按照上表中的映射关系，将无法直接从datetime映射成java.sql.Date，所以需要作些转换。

我们需要为上面的这种转换编写一个转换器，转换器必须实现com.et.ar.Converter接口：

```
public class DateConverter implements Converter {
	@Override
	public Object convert(Object value) {
		if (value == null) {
			return null;
		}
		String s = value.toString().substring(0, 10);	//取yyyy-mm-dd
		return java.sql.Date.valueOf(s);
	}
}
```

这个转换器将对象value转换成java.sql.Date类型的对象，在上面的转换实现中，我们仅是简单地取前十个字符串，然后调用valueOf进行转换。

最后将这个转换器进行注册登记：

```
	static{
		ConvertUtil.register(new DateConverter(), java.sql.Date.class);
	}
```

好了，ActiveRecord在处理到需要映射成java.sql.Date类型的字段时会调用我们自定义的转换器进行处理。



