et-mvc上传文件是对Commons-fileupload组件的封装，所以使用时需要引入commons-fileupload.jar, commons-io.jar, commons-logging.jar三个包。

上传文件的第一步就是象下面一样创建一个multipart/form-data表单：

```
        <form action="<c:url value="/upload/doUpload"/>" method="POST" enctype="multipart/form-data">
            <input type="file" name="file">
            <input type="submit" value="提交">
        </form>
```

然后编写控制器，定义上传的Action方法：
```
public class UploadController extends ApplicationController{
    public String doUpload(MultipartFile file) throws Exception{
        file.transferTo(new File("e:/temp/" + file.getOriginalFilename()));
        return file.getOriginalFilename()+":"+file.getSize();
    }
}
```

需要下载文件时，可以使用BinaryView，如下所示：

```
    public BinaryView download() throws Exception{
        BinaryView view = BinaryView.loadFromFile("e:/temp/arrow.gif");
        view.setContentType("image/gif");
        //view.setContentDisposition("attachment"); //下载
        return view;
    }
```

上传下载就介绍这些，看起来应该是比较简单的。


