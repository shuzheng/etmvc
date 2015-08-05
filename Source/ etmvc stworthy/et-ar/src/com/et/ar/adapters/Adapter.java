package com.et.ar.adapters;

public abstract class Adapter {
    public abstract String getAdapterName();
    public abstract String getLimitString(String sql, int limit, int offset);
    public abstract boolean supportsLimitOffset();
    public abstract String getIdentitySelectString();
    public abstract String getSequenceNextValString(String sequenceName);
}
