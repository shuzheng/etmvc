package com.et.mvc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.fileupload.FileItem;

/**
 * 文件上传支持类
 * @author stworthy
 */
public class MultipartFile {
    private FileItem fileItem;
    private long size;
    
    public MultipartFile(FileItem fileItem){
        this.fileItem = fileItem;
        this.size = this.fileItem.getSize();
    }
    
    public String getName(){
        return fileItem.getFieldName();
    }
    
    public String getOriginalFilename(){
        String filename = this.fileItem.getName();
        if (filename == null){
            return "";
        }
        int pos = filename.lastIndexOf("/");
        if (pos == -1){
            pos = filename.lastIndexOf("\\");
        }
        if (pos != -1){
            return filename.substring(pos+1);
        }
        else{
            return filename;
        }
    }
    
    public String getContentType(){
        return this.fileItem.getContentType();
    }
    
    public boolean isEmpty(){
        return size==0;
    }
    
    public long getSize(){
        return size;
    }
    
    public byte[] getBytes(){
        byte[] bytes = this.fileItem.get();
        return bytes == null ? new byte[0] : bytes;
    }
    
    public InputStream getInputStream() throws IOException{
        InputStream inputStream = this.fileItem.getInputStream();
        return inputStream != null ? inputStream : new ByteArrayInputStream(new byte[0]);
    }
    
    public void transferTo(File dest) throws Exception{
        this.fileItem.write(dest);
    }
}
