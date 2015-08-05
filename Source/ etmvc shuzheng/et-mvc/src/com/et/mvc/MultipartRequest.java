package com.et.mvc;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * 针对表单是multipart/form-data的请求包装类
 * @author stworthy
 */
public class MultipartRequest extends HttpServletRequestWrapper{
    private HttpServletRequest request;
    private int maxMemorySize = -1;
    private String tempDirectory = null;
    private long maxFileSize = -1;

    private Map<String,String[]> parameters = new HashMap<String,String[]>();
    private Map<String,MultipartFile[]> multipartFiles = new HashMap<String,MultipartFile[]>();
    
    public MultipartRequest(HttpServletRequest request, Map<String,String> params) throws FileUploadException{
        super(request);
        this.request = request;
        parseRequest();
        for(Entry<String,String> param: params.entrySet()){
            parameters.put(param.getKey(), new String[]{param.getValue()});
        }
    }
    
    public void parseRequest() throws FileUploadException{
        DiskFileItemFactory factory = new DiskFileItemFactory();
        if (maxMemorySize > 0){
            factory.setSizeThreshold(maxMemorySize);
        }
        if (tempDirectory != null){
            File tmpDir = new File(tempDirectory);
            if (tmpDir.exists() && tmpDir.isDirectory()){
                factory.setRepository(tmpDir);
            }
        }
        ServletFileUpload upload = new ServletFileUpload(factory);
        if (maxFileSize > 0){
            upload.setFileSizeMax(maxFileSize);
        }
        
        Map<String,List<String>> tmpParams = new HashMap<String,List<String>>();
        Map<String,List<MultipartFile>> tmpFiles = new HashMap<String,List<MultipartFile>>();
        
        List<?> items = upload.parseRequest(request);
        for(Object itemObject: items){
            FileItem item = (FileItem)itemObject;
            if (item.isFormField()){
                List<String> list1 = tmpParams.get(item.getFieldName());
                if (list1 == null){
                    list1 = new ArrayList<String>();
                    tmpParams.put(item.getFieldName(), list1);
                }
                try{
                    String encoding = request.getCharacterEncoding();
                    String value1 = encoding == null ? item.getString() : item.getString(encoding);
                    list1.add(value1);
                }
                catch(UnsupportedEncodingException e){
                    throw new FileUploadException(e.getMessage());
                }
            }
            else{
                List<MultipartFile> list2 = tmpFiles.get(item.getFieldName());
                if (list2 == null){
                    list2 = new ArrayList<MultipartFile>();
                    tmpFiles.put(item.getFieldName(), list2);
                }
                list2.add(new MultipartFile(item));
            }
        }
        
        for(Entry<String,List<String>> entry: tmpParams.entrySet()){
            String key = entry.getKey();
            List<String> value = entry.getValue();
            parameters.put(key, value.toArray(new String[value.size()]));
        }
        for(Entry<String,List<MultipartFile>> entry: tmpFiles.entrySet()){
            String key = entry.getKey();
            List<MultipartFile> value = entry.getValue();
            multipartFiles.put(key, value.toArray(new MultipartFile[value.size()]));
        }
    }
    
    @Override
    public String getParameter(String name){
        String[] params = parameters.get(name);
        return params == null ? null : params[0];
    }
    
    @Override
    public String[] getParameterValues(String name){
        return parameters.get(name);
    }
    
    @Override
    public Map<?,?> getParameterMap(){
//        return parameters;
        return Collections.unmodifiableMap(parameters);
    }
    
    @Override
    public Enumeration<?> getParameterNames(){
        Enumeration<?> names = new Enumeration<?>(){
            private Iterator<String> it = parameters.keySet().iterator();
            public boolean hasMoreElements(){
                return it.hasNext();
            }
            public Object nextElement(){
                return it.next();
            }
        };
        return names;
    }
    
    public MultipartFile getFile(String name){
        MultipartFile[] files = multipartFiles.get(name);
        return files == null ? null : files[0];
    }
    
    public MultipartFile[] getFiles(String name){
        return multipartFiles.get(name);
    }
}
