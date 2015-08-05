package com.et.ar.orm;

import com.et.ar.annotations.BelongsTo;

public class BelongsToField {
    private String name;
    private BelongsTo annotation;
    private Class<?> targetType;
    private String foreignKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BelongsTo getAnnotation() {
        return annotation;
    }

    public void setAnnotation(BelongsTo annotation) {
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
