package com.et.ar;

import java.util.*;
import java.util.Map.Entry;

@Deprecated
public class Json {
    public static String toJson(ActiveRecordBase obj){
        return toJson(obj.getAttributes());
    }
    
    public static String toJson(Map<?,?> map){
        String result = "";
        Iterator<?> it = map.keySet().iterator();
        while (it.hasNext()){
            String name = (String)it.next();
            Object value = map.get(name);
            if (value == null){
                result += "\""+name+"\":null,";
            }
            else if (value instanceof ActiveRecordBase){
                Map<String,Object> attr = ((ActiveRecordBase)value).getAttributes();
                attr = removeListAttr(attr);
                result += "\""+name+"\":"+Json.toJson(attr)+",";
//                result += "\""+name+"\":"+Json.toJson(ActiveRecordBase.getAttributes(value))+",";
            }
            else if (value instanceof Boolean){
                Boolean b = (Boolean)value;
                result += "\""+name+"\":"+b.toString()+",";
            }
            else if (value instanceof Integer ||
                     value instanceof Float ||
                     value instanceof Double ||
                     value instanceof Short ||
                     value instanceof java.math.BigInteger ||
                     value instanceof java.math.BigDecimal){
                result += "\""+name+"\":"+value.toString()+",";
            }
            else if (value instanceof String){
                String v = (String)value;
                v = v.replaceAll("\n", "\\\\n");
                v = v.replaceAll("\r", "\\\\r");
                v = v.replaceAll("\"", "\\\\\"");
                v = v.replaceAll("'", "\\\\\'");
                result += "\""+name+"\":"+"\""+v+"\",";
            }
            else if (value instanceof java.sql.Date){
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.sql.Date v = (java.sql.Date)value;
                String s = df.format(new Date(v.getTime()));
                result += "\""+name+"\":"+"\""+s+"\",";
            }
            else if (value instanceof java.sql.Timestamp){
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.sql.Timestamp v = (java.sql.Timestamp)value;
                String s = df.format(new Date(v.getTime()));
                result += "\""+name+"\":"+"\""+s+"\",";
            }
            else if (value instanceof List<?>){
                String v = toJson((List<?>)value);
                result += "\""+name+"\":"+v+",";
            }
            else{
                result += "\""+name+"\":"+"\""+value.toString()+"\",";
            }
        }
        if (result.length() == 0){
            return "{}";
        }
        else{
            return "{"+result.substring(0,result.length()-1)+"}";
        }
    }
    
    public static String toJson(List<?> ll){
        if (ll.size() == 0){
            return "[]";
        }
        else{
            String result = "";
            for(Object obj: ll){
                if (obj instanceof Map<?,?>){
                    Map<?,?> map = (Map<?,?>)obj;
                    map = removeListAttr(map);
                    result += toJson(map)+",";
                }
                else if (obj instanceof ActiveRecordBase){
                    Map<String,Object> attr = ((ActiveRecordBase)obj).getAttributes();
                    attr = removeListAttr(attr);
                    result += toJson(attr)+",";
//                    result += toJson(ActiveRecordBase.getAttributes(obj))+",";
                }
            }
            return "["+result.substring(0,result.length()-1)+"]";
        }
    }
    
    private static Map<String,Object> removeListAttr(Map<?,?> map){
        Map<String,Object> newMap = new HashMap<String,Object>();
        for(Object enObj: map.entrySet()){
            Entry<?,?> en = (Entry<?,?>)enObj;
            if (!(en.getValue() instanceof List<?>)){
                newMap.put((String)en.getKey(), en.getValue());
            }
        }
        return newMap;
    }
}
