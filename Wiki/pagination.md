分页是每个WEB应用程序必不可少的一环，数据量一大，就需要分页。分页需要前后台的配合才能完成，extjs的分页是由分页工具栏PagingToolbar完成的，而etmvc中的分页由ActiveRecord完成。

我们以显示用户资料为例子来看分页是怎么做的，建立一个分页的UserGrid，我们选择继承Ext.grid.GridPanel，这样形成的Component是可以复用的。来看看代码：

```
UserGrid = Ext.extend(Ext.grid.GridPanel, {
    initComponent:function(){
        var ds = new Ext.data.JsonStore({
            url:'user/getUsers',
            fields:['id','name','addr','email','remark'],
            root:'users',
            id:'id',
            totalProperty:'total'
        });
        ds.load({
            params:{start:0,limit:20}
        });
        Ext.apply(this, {
            title:'用户资料管理',
            store:ds,
            columns:[
                {header:'名称',dataIndex:'name',width:100},
                {header:'地址',dataIndex:'addr',width:200},
                {header:'邮箱',dataIndex:'email',width:200},
                {header:'备注',dataIndex:'remark',width:300}
            ],
            bbar:new Ext.PagingToolbar({
                store:ds,
                pageSize:20
            })
        });
        UserGrid.superclass.initComponent.call(this);
    }
});
```

我们看到，分页时的JsonStore比不分页时增加了三个属性：root, id, totalProperty。

现在来看看后台的控制器代码：

```
public class UserController extends ApplicationController{
    public JsonView getUsers(int start, int limit) throws Exception{
        long total = User.count(User.class, null, null);
        List<User> users = User.findAll(User.class, null, null, "id", limit, start);

        Map<String,Object> result = new HashMap<String,Object>();
        result.put("total", total);
        result.put("users", users);
        return new JsonView(result);
    }
}
```

我们看到，Action方法接受二个参数：start和limit，由ActiveRecord完成分页查询操作，然后返回JsonView，由etmvc将处理好的JSON串输出至客户端。

分页操作实际上并不复杂。