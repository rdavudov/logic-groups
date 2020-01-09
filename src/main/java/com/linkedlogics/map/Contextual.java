package com.linkedlogics.map;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface Contextual {
    boolean containsKey(Object key) ;

    Object get(Object key) ;

    Object get(String key, Object defaultValue) ;

    Boolean getBoolean(String key) ;

    Boolean getBoolean(String key, boolean defaultValue) ;

    String getString(String key) ;

    String getString(String key, String defaultValue) ;

    List getList(String key) ;

    List getList(String key, List defaultValue) ;

    Map<String, Object> getMap(String key) ;

    Map<String, Object> getMap(String key, Map<String, Object> defaultValue) ;

    Byte getByte(String key) ;

    Byte getByte(String key, byte defaultValue) ;

    Short getShort(String key) ;

    Short getShort(String key, short defaultValue) ;

    Integer getInt(String key) ;

    Integer getInt(String key, int defaultValue) ;

    Long getLong(String key) ;

    Long getLong(String key, long defaultValue) ;

    Float getFloat(String key) ;

    Float getFloat(String key, float defaultValue) ;

    Double getDouble(String key) ;

    Double getDouble(String key, double defaultValue) ;

    Class getClass(String key) ;

    Class getClass(String key, Class defaultValue) ;

    Date getDate(String key) ;

    Date getDate(String key, Date defaultValue) ;

    <T> T getEnum(String key, Class enumClass) ;

    <T> T getEnum(String key, Class enumClass, Object defaultValue) ;

    <T> T getObject(String key, Class<? extends ContextMap> valueClass) ;

    <T> List<T> getList(String key, Class<? extends ContextMap> valueClass) ;
}
