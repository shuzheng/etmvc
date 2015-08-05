package com.et.ar.adapters;

public class DB2Adapter extends Adapter {

	public String getAdapterName() {
		return "db2";
	}

	public String getLimitString(String sql, int limit, int offset) {
		return sql + " fetch first " + Integer.toString(offset + limit)
				+ " rows only";
	}

	public boolean supportsLimitOffset() {
		return false;
	}

	public String getIdentitySelectString() {
		return "VALUES IDENTITY_VAL_LOCAL()";
	}

	public String getSequenceNextValString(String sequenceName) {
		return null;
	}

}
