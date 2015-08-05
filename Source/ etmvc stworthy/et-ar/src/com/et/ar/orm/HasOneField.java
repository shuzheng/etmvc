package com.et.ar.orm;

import com.et.ar.annotations.HasOne;

public class HasOneField {
    private String name;
    private HasOne annotation;
    private Class<?> targetType;
    private String foreignKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HasOne getAnnotation() {
        return annotation;
    }

    public void setAnnotation(HasOne annotation) {
        this.annotation = annotation;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public void setTargetType(Class<?> targetType) {
        this.targetType = targetType;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }
}
