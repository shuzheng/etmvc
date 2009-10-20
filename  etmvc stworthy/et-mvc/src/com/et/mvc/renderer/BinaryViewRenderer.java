package com.et.mvc.renderer;

import com.et.mvc.BinaryView;
import com.et.mvc.ViewContext;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;

public class BinaryViewRenderer extends AbstractViewRenderer<BinaryView>{
    public void renderView(BinaryView view, ViewContext viewContext) throws Exception{
        if (view == null){
            return;
        }
        
        HttpServletResponse response = viewContext.getResponse();
        response.setContentType(view.getContentType());
        response.setContentLength(view.getData().length);
        if (view.getFileName() == null){
            response.setHeader("Content-Disposition", view.getContentDisposition());
        }
        else{
            String filename = new String(view.getFileName().getBytes("GBK"), "ISO8859_1");
            response.setHeader("Content-Disposition", view.getContentDisposition()+";filename="+filename);
        }
        OutputStream out = response.getOutputStream();
        out.write(view.getData());
        out.close();
    }
}
