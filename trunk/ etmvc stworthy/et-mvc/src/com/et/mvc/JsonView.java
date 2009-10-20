package com.et.mvc;

import com.et.mvc.renderer.JsonViewRenderer;
import com.et.mvc.util.Json;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 以JSON串返回的视图类，多用于AJAX应用中<br/>
 * <pre>
 * return new JsonView("success:true");//返回{"success":true}
 * return new JsonView("failure:true,msg:error info");//返回{"failure":true,"msg":"error info"}
 * </pre>
 * 对于复杂的数据结构，可以构造Map,List后再使用
 * <pre>
 * List&lt;User&gt; items = new User.findAll(User.class);
 * Map<String, Object> result = new HashMap<String,Object>();
 * result.put("total", 200);
 * result.put("items", items);
 * return new JsonView(result);
 * </pre>
 * @author stworthy
 */
@ViewRendererClass(JsonViewRenderer.class)
public class JsonView extends View{
    private String json;
    
    public JsonView(){
        json = "{}";
    }
    
    public JsonView(Map<String,Object> map){
        json = Json.toJson(map);
    }
    
    public JsonView(List<?> list){
        json = Json.toJson(list);
    }
    
    public JsonView(Object[] objects){
        json = Json.toJson(objects);
    }
    
    public JsonView(Object obj){
        json = Json.toJson(obj);
    }
    
    /**
     * 用字符串构造JSON视图
     * @param str 字符串表示的JSON表达式，如"success:true,age:32,salary:2000.50,name:'名称'"
     */
    public JsonView(String str){
        Map<String,Object> map = parseStr(str);
        json = Json.toJson(map);
    }
    
    @Override
    public String toString(){
        return json;
    }

    private Map<String,Object> parseStr(String str){
        Map<String,Object> map = new HashMap<String,Object>();
        for(String strPart: str.split(",")){
            String[] ss = strPart.split(":");
            if (ss == null || ss.length != 2){
                continue;
            }
            String key = ss[0];
            String value = ss[1].trim();
            if (value.startsWith("'") && value.endsWith("'")){
                map.put(key, value.substring(1, value.length()-1));
            }
            else if (value.startsWith("\"") && value.endsWith("\"")){
                map.put(key, value.substring(1, value.length()-1));
            }
            else if (value.equals("true") || value.equals("false")){
                map.put(key, Boolean.valueOf(value));
            }
            else if (value.indexOf(".") == -1){
                try{
                    int val = Integer.parseInt(value);
                    map.put(key, val);
                }
                catch(Exception e){
                    map.put(key, value);
                }
            }
            else{
                try{
                    BigDecimal val = new BigDecimal(value);
                    map.put(key, val);
                }
                catch(Exception e){
                    map.put(key, value);
                }
            }
        }
        return map;
    }
}
