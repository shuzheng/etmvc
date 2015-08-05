package com.et.mvc;

import com.et.mvc.renderer.BinaryViewRenderer;
import java.io.File;
import java.io.FileInputStream;

/**
 * 二进制视图，提供图像显示、附件下载等功能<br/>
 * 默认设置Content-Type=application/octet-stream, Content-Disposition=inline<br/>
 * 比如显示一个图像时可以这样<br/>
 * <pre>
 * byte[] data = getDate();
 * BinaryView view = new BinaryView(data);
 * return view;
 * </pre>
 * 如果需要下载附件可以这样<br/>
 * <pre>
 * BinaryView view = BinaryView(data);
 * view.setContentDisposition("attachment");
 * view.setFileName("download.txt");
 * return view;
 * </pre>
 * @author stworthy
 */
@ViewRendererClass(BinaryViewRenderer.class)
public class BinaryView extends View{
    private byte[] data;
    private String contentDisposition;
    private String fileName;
    
    /**
     * 建立BinaryView对象
     */
    public BinaryView(){
        this.setContentType("application/octet-stream");
        this.contentDisposition = "inline";
    }
    
    public BinaryView(byte[] data){
        this.data = data;
        this.setContentType("application/octet-stream");
        this.contentDisposition = "inline";
    }
    
    public static BinaryView loadFromFile(String fileName) throws Exception{
        File file = new File(fileName);
        if (file.exists() && file.isFile()){
            FileInputStream fis = new FileInputStream(file);
            int length = (int)file.length();
            byte[] data = new byte[length];
            fis.read(data);
            fis.close();
            
            BinaryView view = new BinaryView(data);
            view.setFileName(file.getName());
            return view;
        }
        else{
            return null;
        }
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getContentDisposition() {
        return contentDisposition;
    }

    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
