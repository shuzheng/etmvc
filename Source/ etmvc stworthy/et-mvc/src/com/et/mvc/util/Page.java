package com.et.mvc.util;

/**
 * 分页实用类
 * @author stworthy
 */
public class Page {
    /**
     * 生成简单的分页页面内容
     * @param totalCount 总记录数 
     * @param pageSize 每页记录数
     * @param listStep 最多显示分页页数
     * @param start 当前记录位置
     * @param url 操作的URL链接
     * @return 分页的HTML代码
     */
    public static String getSimplePages(int totalCount, int pageSize, int listStep, int start, String url){
        String s = "";
        
        int currPage = start / pageSize;    //当前页，从0开始计
        
        int pageCount = (int) Math.ceil((double) totalCount / pageSize);//求总页数
        if (pageCount == 1){
            return s;
        }
        if (currPage > pageCount - 1) {
            currPage = pageCount - 1;//如果分页变量大总页数，则将分页变量设计为总页数
        }
        if (currPage < 0) {
            currPage = 0;//如果分页变量小于１,则将分页变量设为１
        }
        
        int listBegin = (currPage - (int) Math.ceil((double) listStep / 2));//从第几页开始显示分页信息
        if (listBegin < 0) {
            listBegin = 0;
        }
        int listEnd = currPage + listStep/2;//分页信息显示到第几页
        if (listEnd > pageCount){
            listEnd = pageCount;
        }
        
        //显示上一页
        if (currPage > 0){
            if (url.contains("?")){
                s += "<a class='prev' href=" + url + "&start=" + (currPage - 1)*pageSize + ">上一页</a> ";
            }
            else{
                s += "<a class='prev' href=" + url + "?start=" + (currPage - 1)*pageSize + ">上一页</a> ";
            }
        }
        
        //显示分页码
        for (int i = listBegin; i < listEnd; i++) {
            if (i != currPage) {
                if (url.contains("?")){
                    s += " <a href=" + url + "&start=" + i*pageSize + ">" + (i+1) + "</a> ";
                }
                else{
                    s += " <a href=" + url + "?start=" + i*pageSize + ">" + (i+1) + "</a> ";
                }
            } else {
                s += " <span class='current'>" + (i+1) + "</span> ";
            }
        }
        
        //显示下一页
        if (currPage < pageCount - 1){
            if (url.contains("?")){
                s += " <a class='next' href=" + url + "&start=" + (currPage + 1)*pageSize + ">下一页</a>";
            }
            else{
                s += " <a class='next' href=" + url + "?start=" + (currPage + 1)*pageSize + ">下一页</a>";
            }
        }
        
        return s;
    }
}
