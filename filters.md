struts2有拦截器，非常强大，spring mvc也有拦截器机制，但没有环绕的拦截处理机制。

etmvc也有过滤器，etmvc的过滤器类似于ROR，分为前置过滤器，后置过滤器和环绕过滤器。过滤器能够处理所有像安全、日志等的横切关注点。

我们先来看看应用得最多的前置过滤器，前置过滤器是控制器类中的一个方法，方法要求返回一个boolean类型，如果返回false，将停止后续的所有操作，否则继续后面的操作。来举个用户登录认证的例子，要求用户必须通过登录才能访问系统，使用前置过滤器是非常合适的，比如jpetstore中访问订单时要求用户必须登录后才能访问，否则重定向到登录页面让用户登录：

```
@BeforeFilter(execute="auth")
public class OrderController extends ApplicationController{
    protected boolean auth() throws Exception{
        if (session.getAttribute("sessionAccount") == null){
            String url = request.getServletPath();
            String query = request.getQueryString();
            if (query != null){
                url += "?" + query;
            }
            redirect("account/signon?signonForwardAction=" + url);
            return false;
        }
        return true;
    }
}
```

我们通过在控制器上使用@BeforeFilter注解增加了一个前置过滤器，过滤器将执行auth方法，检查用户是否登录，是否能够合法访问订单。

如果想对所有的控制器加上过滤器，则无须在每个控制器类上加上注解，只须在控制器基类ApplicationController中加注解就好。也就是说，过滤器具有继承特性。

过滤器可以有only或except选项，以确定对那些Action方法有效，比如：

```
@BeforeFilters({
    @BeforeFilter(execute="checkAdminPrivilege",except={"showImage","download","addComment"}),
    @BeforeFilter(execute="checkUserPrivilege",only={"addComment"})
})
public class ArticleController extends ApplicationController{
    /**
     * 显示文章附件图片
     */
    public View showImage(int id) throws Exception{
    }

    /**
     * 下载文章附件
     */
    public View download(int id) throws Exception{
    }

    /**
     * 增加评论
     */
    public void addComment(int articleId, String content) throws Exception{
    }
    
    public void create(){
    }

    public void edit(int id) throws Exception{
    }

    public void save(String title, String content, MultipartFile file, String source, Integer flag) throws Exception{
    }

    public void update(int id, String title, String content, MultipartFile file, String source, Integer flag) throws Exception{
    }

    public void destroy(int id) throws Exception{
    }

    public void destroyAttach(int id) throws Exception{
    }
}
```
在文章管理的控制器中加入了二个前置过滤器，以检查管理员和普通用户的操作权限：

  * 管理员权限检查除"showImage","download","addComment"方法外将全部检查
  * 普通用户权限检查只对"addComment"方法进行检查
可以对控制器加上多个过滤器，形成一个过滤器链。

前面介绍的是前置过滤器，这是用得最多的一种，后置过滤器用法相同。

关于环绕过滤器我们将分专门章节进行介绍。
