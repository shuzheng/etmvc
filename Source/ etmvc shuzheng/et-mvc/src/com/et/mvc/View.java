package com.et.mvc;

/**
 * 视图抽象类，建议所有的自定义视图类继承该类，但不是必须的
 * @author stworthy
 */
public abstract class View {
    private String contentType;
    private Class<?> rendererClass = null;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

	public void setRendererClass(Class<?> rendererClass) {
		this.rendererClass = rendererClass;
	}

	public Class<?> getRendererClass() {
		return rendererClass;
	}


    
}
