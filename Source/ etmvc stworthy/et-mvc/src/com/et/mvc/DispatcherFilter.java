package com.et.mvc;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.et.mvc.routing.RouteResult;
import com.et.mvc.routing.RouteTable;

/**
 * 分发器过滤器类，在web.xml中进行配置，具有如下参数：<br/>
 * <ul>
 * <li>controllerBasePackage 控制器类所属包的基包</li>
 * <li>viewBasePath 放置视图模板的目录</li>
 * <li>plugin 插件，多个插件以“,”分开</li>
 * </ul>
 * @author stworthy
 */
public class DispatcherFilter implements Filter {
    private FilterConfig filterConfig = null;
    private Map<String,String> configParams = null;
    private Dispatcher dispatcher = null;
    private List<PlugIn> plugins = new ArrayList<PlugIn>();

    public DispatcherFilter() {
    }

    private void initControllerPaths(){
        List<String> paths = new ArrayList<String>();
        try{
            List<String> classes = new ArrayList<String>();
            String packagePath = Dispatcher.getControllerBasePackage();
            packagePath = packagePath.replace(".", "/");
            
            getControllerClasses(packagePath, classes);
            for(String cp: classes){
                String path = cp.substring(packagePath.length());
                path = path.replace("Controller.class", "");
                if (!path.startsWith("/")){
                    path = "/" + path;
                }
                int pos = path.lastIndexOf("/");
                String subPackage = path.substring(0, pos);
                String controllerName = path.substring(pos + 1);
                String controllerFirst = controllerName.substring(0, 1);
                String controllerSecond = controllerName.substring(1);
                path = subPackage + "/" + controllerFirst.toLowerCase() + controllerSecond;

                paths.add(path);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        Dispatcher.setControllerPaths(paths);
    }

    /**
     * 获取指定包路径的所有类
     * @param path 包路径，如controllers/qo
     * @param classes 所有类集合，每个类包括路径，如controllers/qo/ApplicationController.class
     * @throws java.lang.Exception
     */
    private void getControllerClasses(String path, List<String> classes) throws Exception{
        Enumeration<URL> urls = getClass().getClassLoader().getResources(path);
        if (!urls.hasMoreElements()){
            urls = getClass().getClassLoader().getResources("/" + path);
        }
        while(urls.hasMoreElements()){
            URL url = urls.nextElement();
            String protocol = url.getProtocol();
            if (protocol.equalsIgnoreCase("file") ||
            		protocol.equalsIgnoreCase("vfsfile")){
            	
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                File file = new File(filePath);
                File[] ff = file.listFiles();
                for(File f: ff){
                    String name = f.getName();
                    if (f.isDirectory()){
                        getControllerClasses(path + "/" + name, classes);
                    }
                    else if (name.endsWith(".class")){
                        classes.add(path + "/" + name);
                    }
                }
            }
            else if (protocol.equalsIgnoreCase("jar") ||
            		protocol.equalsIgnoreCase("zip") ||
            		protocol.equalsIgnoreCase("wsjar")){
            	
                JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
                Enumeration<JarEntry> entries = jar.entries();
                while(entries.hasMoreElements()){
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith("/")){
                        name = name.substring(1);
                    }
                    if (name.startsWith(path) && name.endsWith(".class")){
                        classes.add(name);
                    }
                }
            }
        }
    }
    
    
    private void initPlugIns(){
        PlugInContext context = new PlugInContext();
        context.setServletContext(this.filterConfig.getServletContext());
        context.setConfigParams(configParams);
        
        try{
            String plugInClasses = filterConfig.getInitParameter("plugin");
            if (plugInClasses != null){
                String[] classes = plugInClasses.split(",");
                for(String className: classes){
                	className = className.trim();
                	if (!className.equals("")) {
	                    PlugIn plugin = (PlugIn)Class.forName(className).newInstance();
	                    plugin.init(context);
	                    plugins.add(plugin);
                	}
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    /**
     * Init method for this filter 
     *
     */
    public void init(FilterConfig filterConfig) { 
	this.filterConfig = filterConfig;
        configParams = new HashMap<String,String>();
        for(Enumeration<?> e = filterConfig.getInitParameterNames(); e.hasMoreElements(); ){
            String name = (String)e.nextElement();
            String value = filterConfig.getInitParameter(name);
            configParams.put(name, value);
        }
        
        String controllerBasePackage = filterConfig.getInitParameter("controllerBasePackage");
        String viewBasePath = filterConfig.getInitParameter("viewBasePath");
        if (viewBasePath.endsWith("/")){
            viewBasePath = viewBasePath.substring(0,viewBasePath.length()-1);
        }
        
        Dispatcher.setControllerBasePackage(controllerBasePackage);
        Dispatcher.setViewBasePath(viewBasePath);
        initControllerPaths();
        initPlugIns();
        
        dispatcher = new Dispatcher();
//        dispatcher = new Dispatcher(filterConfig.getServletContext(), configParams);
    }

    protected HttpServletRequest wrapRequest(HttpServletRequest request, Map<String,String> params) throws ServletException{
        String type = request.getHeader("Content-Type");    //请求类型
        if (type == null || !type.startsWith("multipart/form-data")){
            return new NormalRequest(request, params);
        }
        else{
            try{
                MultipartRequest mreq = new MultipartRequest(request, params);
                return mreq;
            }
            catch(Exception ex){
                throw new ServletException(ex);
            }
        }
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
	throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        
        // 设置默认编码为UTF-8
        req.setCharacterEncoding("UTF-8");
        res.setContentType("text/html;charset=UTF-8");
        
        RequestContext requestContext = new RequestContext();
        requestContext.setControllerBasePackage(Dispatcher.getControllerBasePackage());
        requestContext.setControllerPaths(Dispatcher.getControllerPaths());
        requestContext.setRequest(req);
        
        RouteResult routeResult = null;
        try{
            routeResult = RouteTable.selectRoute(requestContext);
        }
        catch(Exception ex){
            throw new ServletException(ex);
        }
        
        if (routeResult == null){
            chain.doFilter(request, response);
        }
        else{
            String controllerPackage = Dispatcher.getControllerBasePackage();
            if (!routeResult.getSubPackageName().equals("")){
                controllerPackage += "." + routeResult.getSubPackageName();
            }
            String controllerClassName = controllerPackage + "." + toControllerClassName(routeResult.getControllerName());
            
            Controller controller;
            try{
                controller = (Controller)getController(controllerClassName);
                req = wrapRequest(req, routeResult.getParams());
                controller.init(this.filterConfig.getServletContext(), req, res, routeResult);
            }
            catch(Exception ex){
//                ex.printStackTrace();
                chain.doFilter(request, response);
                return;
            }
            
            try{
                dispatcher.service(controller);
            }
            catch(Exception ex){
                throw new ServletException(ex);
            }
        }
    }

    protected Object getController(String controllerClassName) throws Exception{
        return Class.forName(controllerClassName).newInstance();
    }
    
    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
	return (this.filterConfig);
    }


    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {

	this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter 
     *
     */
    public void destroy() { 
        for(PlugIn plugin: plugins){
            plugin.destroy();
        }
    }

    /**
     * 请控制器名称转换成具体的控制器类名称，首字母变成大写，后缀加上Controller。
     * @param controllerName
     * @return
     */
    protected String toControllerClassName(String controllerName){
        if (controllerName == null){
            return null;
        }
        String first = controllerName.substring(0,1);
        String second = controllerName.substring(1);
        return first.toUpperCase()+second+"Controller";
    }

}