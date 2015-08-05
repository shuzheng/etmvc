我们在ActiveRecord（下简称AR）中提供一对一、一对多、多对一等关联，分述如下：

1、一对多：一对多关联是指一个类（比如Author）拥有另一个类（比如Book）的多个实例，用@HashMany注解描述：

```
@Table(name="authors")
public class Author extends ActiveRecordBase{
    @Id private Integer id;
    @Column private String name;

    @HasMany(foreignKey="authorId", dependent=DependentType.DELETE, order="id")
    private List<Book> books;
    //get,set...
}
```

```
@Table(name="books")
public class Book extends ActiveRecordBase{
    @Id private Integer id;
    @Column private Integer authorId;
    @Column private String name;

    @BelongsTo(foreignKey="authorId")
    private Author author;
    //get,set...
}
```

@HasMany有几个属性：

foreignKey指定多方的外键，必须指定。
dependent指定在删除主表时做何操作，可选。有DELETE，DESTROY，NULLIFY，DELETE是简单的删除从表记录；DESTROY是再以级联的方式销毁从表对应的对象，如果这种级联关系有二级以上，则使用DESTROY会比较合适；NULLIFY是将从表的外键置为NULL值而并不删除。
order指定获取从表对应的记录时的排序字段，可选。

2、一对一：一对一是一对多的特例，使用@HasOne描述，如：
```
@Table(name="authors")
public class Author extends ActiveRecordBase{
    @Id private Integer id;
    @Column private String name;
    
    @HasOne(foreignKey="authorId")
    private Book book;
    //get,set...
}
```

@HasOne注解的其他用法同@HasMany。

3、多对一：是指一个类属于另一个类，比如上面的Book类，使用@BelongsTo注解描述多对一关系。

好了，定义了模型对象之间的关联之后，我们的编码简单了很多，比如我们要访问某个作者所拥有的图书，可以这样写：
```
        Author author = Author.find(Author.class, 1);
        List<Book> books = author.getBooks();
        for(Book book: books){
            System.out.println(book.getName());
        }
```

AR会维护对象之间的关联，在级联保存、删除及更新操作中都能保证在一个事务中，要么全部成功，要么全部失败。

比如我们想删除某个作者及其拥有的图书记录，可以这样写：

```
        Author author = Author.find(Author.class, 1);
        author.destroy();
```
建立一个新的Author对象及其对应的Book对象集合，并同时保存进数据库，可以这样写：

```
        Author author = new Author();
        author.setName("author1");
        List<Book> books = new ArrayList<Book>();
        for(int i=0; i<3; i++){
            Book book = new Book();
            book.setName("book" + i);
            books.add(book);
        }
        author.setBooks(books);
        author.save();
```

我们看到，AR帮我们做了大部分工作，她会记住对象之间的关联并试图维护这种关系。

当然，实际情况很复杂，但AR能够完成大多数的工作。

