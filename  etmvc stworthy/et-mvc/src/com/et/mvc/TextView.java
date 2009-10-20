/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.et.mvc;

import com.et.mvc.renderer.TextViewRenderer;

/**
 * 文本视图，用于向浏览器返回一段文本
 * @author stworthy
 */
@ViewRendererClass(TextViewRenderer.class)
public class TextView extends View{
    private String text;
    
    public TextView(){
        text = "";
    }
    
    public TextView(String text){
        this.text = text;
    }
    
    public TextView(String text, String contentType){
        this.text = text;
        setContentType(contentType);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    @Override
    public String toString(){
        return text;
    }
}
