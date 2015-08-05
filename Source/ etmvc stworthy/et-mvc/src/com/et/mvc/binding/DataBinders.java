package com.et.mvc.binding;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.et.mvc.MultipartFile;
import com.et.mvc.binding.binders.BigDecimalBinder;
import com.et.mvc.binding.binders.BigIntegerBinder;
import com.et.mvc.binding.binders.BooleanArrayBinder;
import com.et.mvc.binding.binders.BooleanBinder;
import com.et.mvc.binding.binders.DoubleArrayBinder;
import com.et.mvc.binding.binders.DoubleBinder;
import com.et.mvc.binding.binders.FloatArrayBinder;
import com.et.mvc.binding.binders.FloatBinder;
import com.et.mvc.binding.binders.IntegerArrayBinder;
import com.et.mvc.binding.binders.IntegerBinder;
import com.et.mvc.binding.binders.LongArrayBinder;
import com.et.mvc.binding.binders.LongBinder;
import com.et.mvc.binding.binders.MapBinder;
import com.et.mvc.binding.binders.MultipartFileBinder;
import com.et.mvc.binding.binders.ShortArrayBinder;
import com.et.mvc.binding.binders.ShortBinder;
import com.et.mvc.binding.binders.SqlDateBinder;
import com.et.mvc.binding.binders.SqlTimeBinder;
import com.et.mvc.binding.binders.SqlTimestampBinder;
import com.et.mvc.binding.binders.StringArrayBinder;
import com.et.mvc.binding.binders.StringBinder;

/**
 * 数据绑定管理类，允许加入自已的绑定类
 * @author stworthy
 */
public class DataBinders {
    private static Map<Class<?>,DataBinder> binders = new HashMap<Class<?>,DataBinder>();
    
    private static boolean allowEmpty = false;

    static{
        binders.put(boolean.class, new BooleanBinder());
        binders.put(Boolean.class, new BooleanBinder());
        binders.put(int.class, new IntegerBinder());
        binders.put(Integer.class, new IntegerBinder());
        binders.put(short.class, new ShortBinder());
        binders.put(Short.class, new ShortBinder());
        binders.put(long.class, new LongBinder());
        binders.put(Long.class, new LongBinder());
        binders.put(double.class, new DoubleBinder());
        binders.put(Double.class, new DoubleBinder());
        binders.put(float.class, new FloatBinder());
        binders.put(Float.class, new FloatBinder());
        binders.put(BigInteger.class, new BigIntegerBinder());
        binders.put(BigDecimal.class, new BigDecimalBinder());
        binders.put(java.sql.Date.class, new SqlDateBinder());
        binders.put(java.sql.Time.class, new SqlTimeBinder());
        binders.put(java.sql.Timestamp.class, new SqlTimestampBinder());
        binders.put(String.class, new StringBinder());
        binders.put(MultipartFile.class, new MultipartFileBinder());
        binders.put(Map.class, new MapBinder());
        binders.put(boolean[].class, new BooleanArrayBinder());
        binders.put(Boolean[].class, new BooleanArrayBinder());
        binders.put(short[].class, new ShortArrayBinder());
        binders.put(Short[].class, new ShortArrayBinder());
        binders.put(int[].class, new IntegerArrayBinder());
        binders.put(Integer[].class, new IntegerArrayBinder());
        binders.put(long[].class, new LongArrayBinder());
        binders.put(Long[].class, new LongArrayBinder());
        binders.put(float[].class, new FloatArrayBinder());
        binders.put(Float[].class, new FloatArrayBinder());
        binders.put(double[].class, new DoubleArrayBinder());
        binders.put(Double[].class, new DoubleArrayBinder());
        binders.put(String[].class, new StringArrayBinder());
    }

    public static void register(Class<?> clasz, DataBinder binder){
        binders.put(clasz, binder);
    }

    public static void unregister(Class<?> clasz){
        binders.remove(clasz);
    }

    public static DataBinder getDataBinder(Class<?> clasz){
        return binders.get(clasz);
    }

	public static boolean isAllowEmpty() {
		return allowEmpty;
	}

	public static void setAllowEmpty(boolean allowEmpty) {
		DataBinders.allowEmpty = allowEmpty;
	}
}
