package com.et.ar.orm;

import com.et.ar.annotations.HasMany;

public class HasManyField {
    private String name;
    private HasMany annotation;
    private Class<?> targetType;
    private String foreignKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HasMany getAnnotation() {
        return annotation;
    }

    public void setAnnotation(HasMany annotation) {
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
