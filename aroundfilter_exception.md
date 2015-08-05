etmvc框架中可以使用环绕过滤来处理异常，在WEB应用程序中如果需要处理全局的异常，比如我们可能需要拦截全局的异常然后集中处理，这时可以使用环绕过滤器。

下面给出一个参考的处理方法：

定义异常过滤器：

```
public class ExceptionFilter implements AroundHandler{

	@Override
	public boolean after(Controller controller) throws Exception {
		Exception ex = controller.getException();
		if (ex != null){
			controller.getSession().setAttribute("error", ex);
			controller.getResponse().sendRedirect("/myweb/application/error");
			return false;
		}
		return true;
	}

	@Override
	public boolean before(Controller controller) throws Exception {
		return true;
	}

}
```

异常过滤器中检测到有异常发生，则重定向到全局的错误页面，为了方便，我们将错误页面显示的方法写在了ApplicationController类中，当然也可以专门写个处理异常的控制器：

```
@AroundFilter(execute=ExceptionFilter.class)
public class ApplicationController extends Controller{
	public void error() throws Exception{
		Exception ex = (Exception)session.getAttribute("error");
		session.setAttribute("error", null);
		ex.printStackTrace();
		request.setAttribute("error", ex);
	}
}
```

最后需要一个页面(/views/applicaion/error.jsp)来显示异常信息：

```
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<p>error message</p>
	<p>${error }</p>
</body>
</html>
```

在下面的myweb例子中执行http://localhost:8080/myweb/test/test1时成功执行，显示一个字符串，执行http://localhost:8080/myweb/test/test2时出现异常，重定向至错误页面。
