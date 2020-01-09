package com.linkedlogics.map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedlogics.LogicContext;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContextMap extends HashMap<String, Object> implements Contextual {
    private boolean isImmutable ;

    public ContextMap() {

    }

    public ContextMap(Map<String, Object> map) {
        putAll(map);
    }

    public void setImmutable() {
        setImmutable(true);
    }

    protected void setImmutable(boolean isImmutable) {
        this.isImmutable = isImmutable ;
        this.values().forEach(v -> {
            if (v instanceof ContextMap) {
                ((ContextMap) v).setImmutable(isImmutable);
            } else if (v instanceof List) {
                ((List) v).forEach(i -> {
                    if (i instanceof ContextMap) {
                        ((ContextMap) i).setImmutable(isImmutable);
                    }
                });
            }
        });
    }

    @Override
    public boolean containsKey(Object key) {
        String[] keys = key.toString().split("\\.") ;
        return findKey(keys, 0);
    }

    private boolean findKey(String[] keys, int keysIndex) {
        if (super.containsKey(keys[keysIndex])) {
            if (keysIndex < keys.length - 1) {
                return ((ContextMap) super.get(keys[keysIndex])).findKey(keys, keysIndex + 1);
            } else {
                return true ;
            }
        }

        return false ;
    }

    private Object findValue(String[] keys, int keysIndex) {
        if (super.containsKey(keys[keysIndex])) {
            if (keysIndex < keys.length - 1) {
                return ((ContextMap) super.get(keys[keysIndex] )).findValue(keys, keysIndex + 1) ;
            } else {
                return get(keys[keysIndex]) ;
            }
        }

        return null ;
    }

    private Object putValue(String[] keys, int keysIndex, Object value) {
        if (isImmutable) {
            throw new IllegalStateException("map is immutable") ;
        }

        if (keysIndex == keys.length - 1) {
            return super.put(keys[keysIndex], value) ;
        } else {
            if (!containsKey(keys[keysIndex])) {
                super.put(keys[keysIndex], new ContextMap()) ;
            }

            ContextMap inner = (ContextMap) get(keys[keysIndex]) ;
            return inner.putValue(keys, keysIndex + 1, value) ;
        }
    }

    private Object removeValue(String[] keys, int keysIndex) {
        if (isImmutable) {
            throw new IllegalStateException("map is immutable") ;
        }

        if (keysIndex == keys.length - 1) {
            return remove(keys[keysIndex]) ;
        } else {
            if (containsKey(keys[keysIndex])) {
                ContextMap inner = (ContextMap) get(keys[keysIndex]) ;
                return inner.removeValue(keys, keysIndex + 1) ;
            }
            return null ;
        }
    }

    @Override
    public Object get(Object key) {
        String[] keys = key.toString().split("\\.") ;
        if (keys.length == 1) {
            return super.get(key) ;
        } else {
            return findValue(keys, 0);
        }
    }

    public Object get(String key, Object defaultValue) {
        Object value = get(key) ;
        if (value == null) {
            return defaultValue ;
        }
        return value ;
    }

    protected ContextMap create(ContextMap map) {
        try {
            Constructor noArgs = map.getClass().getConstructor() ;
            if (noArgs != null) {
                return (ContextMap) noArgs.newInstance() ;
            }
        } catch (NoSuchMethodException e) {

        } catch (Throwable e) {
            throw new RuntimeException(e) ;
        }

        try {
            Constructor mapArgs = map.getClass().getConstructor(ContextMap.class) ;
            if (mapArgs != null) {
                return (ContextMap) mapArgs.newInstance(this) ;
            }
        } catch (NoSuchMethodException e) {

        } catch (Throwable e) {
            throw new RuntimeException(e) ;
        }

        throw new RuntimeException("no suitable constructor " + map.getClass()) ;
    }

    public Map<String, Object> fill(LogicContext context) {
        ContextMap filled = create(this) ;

        this.entrySet().forEach(e -> {
            if (e.getValue() instanceof ContextMap) {
                filled.put(e.getKey(), ((ContextMap) e.getValue()).fill(context)) ;
            } else if (isExpression(e.getValue().toString())) {
                filled.put(e.getKey(), context.evaluate(e.getValue().toString()));
            } else if (e.getValue() instanceof List) {
                filled.put(e.getKey(), ((List) e.getValue()).stream().map(i -> {
                    if (i instanceof ContextMap) {
                        return ((ContextMap) i).fill(context) ;
                    } else {
                        return i ;
                    }
                }).collect(Collectors.toList())) ;
            } else {
                filled.put(e.getKey(), e.getValue()) ;
            }
        });

        return filled ;
    }

    @Override
    public Object put(String key, Object value) {
        if (isImmutable) {
            throw new IllegalStateException("map is immutable") ;
        }

        String[] keys = key.split("\\.") ;
        if (keys.length == 1) {
            return super.put(key, value) ;
        } else {
            return putValue(keys, 0, value);
        }
    }

    @Override
    public Object remove(Object key) {
        if (isImmutable) {
            throw new IllegalStateException("map is immutable") ;
        }

        String[] keys = key.toString().split("\\.") ;
        if (keys.length == 1) {
            return super.remove(key) ;
        } else {
            return removeValue(keys, 0);
        }
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        if (isImmutable) {
            throw new IllegalStateException("map is immutable") ;
        }

        map.entrySet().forEach(e -> {
            if (e.getValue() instanceof ContextMap) {
                this.put(e.getKey(), e.getValue()) ;
            } else if (e.getValue() instanceof Map) {
                this.put(e.getKey(), new ContextMap((Map<String, Object>) e.getValue()));
            } else if (e.getValue() instanceof List) {
                this.put(e.getKey(), ((List) e.getValue()).stream().map(i -> {
                    if (i instanceof ContextMap) {
                        return i ;
                    } else if (i instanceof Map) {
                        return new ContextMap((Map<String, Object>) i) ;
                    } else {
                        return i ;
                    }
                }).collect(Collectors.toList())) ;
            } else {
                this.put(e.getKey(), e.getValue()) ;
            }
        });
    }

    @Override
    public void clear() {
        if (isImmutable) {
            throw new IllegalStateException("map is immutable") ;
        }
        super.clear();
    }

    public Boolean getBoolean(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        if (object instanceof Boolean) {
            return (Boolean) object ;
        } else {
            return Boolean.parseBoolean(object.toString());
        }
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        Boolean object = getBoolean(key);
        if (object == null) {
            return defaultValue;
        }
        return object ;
    }

    public Class getClass(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        if (object instanceof Class) {
            return (Class) object ;
        } else {
            try {
                return Class.forName(object.toString()) ;
            } catch (ClassNotFoundException e) {
                return null ;
            }
        }
    }

    public Class getClass(String key, Class defaultValue) {
        Class object = getClass(key);
        if (object == null) {
            return defaultValue;
        }
        return object ;
    }

    public String getString(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        if (object instanceof String) {
            return (String) object ;
        } else {
            return object.toString();
        }
    }

    public String getString(String key, String defaultValue) {
        String object = getString(key);
        if (object == null) {
            return defaultValue;
        }
        return object ;
    }

    public List getList(String key) {
        return (List) get(key);
    }

    public List getList(String key, List defaultValue) {
        List value = (List) get(key) ;
        // if we only return defaultValue and any value is put, still we will not have defaultValue inside bio object
        // therefore, we also need to put default value into bio object
        if (value == null) {
            put(key, defaultValue) ;
            return defaultValue ;
        }
        return value ;
    }

    public Map<String, Object> getMap(String key) {
        return (Map<String, Object>) get(key);
    }

    public Map<String, Object> getMap(String key, Map<String, Object> defaultValue) {
        Map<String, Object> value = (Map<String, Object>) get(key) ;
        // if we only return defaultValue and any value is put, still we will not have defaultValue inside bio object
        // therefore, we also need to put default value into bio object
        if (value == null) {
            put(key, defaultValue) ;
            return defaultValue ;
        }
        return value ;
    }

    public Byte getByte(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        if (object instanceof Byte) {
            return (Byte) object ;
        } else if (object instanceof Number) {
            return ((Number) object).byteValue() ;
        } else {
            return Byte.parseByte(object.toString());
        }
    }

    public Byte getByte(String key, byte defaultValue) {
        Byte object = getByte(key);
        if (object == null) {
            return defaultValue;
        }
        return object ;
    }

    public Short getShort(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        if (object instanceof Short) {
            return (Short) object ;
        } else if (object instanceof Number) {
            return ((Number) object).shortValue() ;
        } else {
            return Short.parseShort(object.toString());
        }
    }

    public Short getShort(String key, short defaultValue) {
        Short object = getShort(key);
        if (object == null) {
            return defaultValue;
        }
        return object ;
    }

    public Integer getInt(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        if (object instanceof Integer) {
            return (Integer) object ;
        } else if (object instanceof Number) {
            return ((Number) object).intValue() ;
        } else {
            return Integer.parseInt(object.toString()) ;
        }
    }

    public Integer getInt(String key, int defaultValue) {
        Integer object = getInt(key);
        if (object == null) {
            return defaultValue;
        }
        return object ;
    }

    public Long getLong(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        if (object instanceof Long) {
            return (Long) object ;
        } else if (object instanceof Number) {
            return ((Number) object).longValue() ;
        } else {
            return Long.parseLong(object.toString());
        }
    }

    public Long getLong(String key, long defaultValue) {
        Long object = getLong(key);
        if (object == null) {
            return defaultValue;
        }
        return object ;
    }

    public Float getFloat(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        if (object instanceof Float) {
            return (Float) object ;
        } else if (object instanceof Number) {
            return ((Number) object).floatValue() ;
        } else {
            return Float.parseFloat(object.toString());
        }
    }

    public Float getFloat(String key, float defaultValue) {
        Float object = getFloat(key);
        if (object == null) {
            return defaultValue;
        }
        return object ;
    }

    public Double getDouble(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        if (object instanceof Double) {
            return (Double) object ;
        } else if (object instanceof Number) {
            return ((Number) object).doubleValue() ;
        } else {
            return Double.parseDouble(object.toString());
        }
    }

    public Double getDouble(String key, double defaultValue) {
        Double object = getDouble(key);
        if (object == null) {
            return defaultValue;
        }
        return object ;
    }

    public Date getDate(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        if (object instanceof Long) {
            return new Date((Long) object);
        } else if (object instanceof Number) {
            return new Date(((Number) object).longValue()) ;
        } else {
            return new Date(Long.parseLong(object.toString()));
        }
    }

    public Date getDate(String key, Date defaultValue) {
        Date object = getDate(key);
        if (object == null) {
            return defaultValue;
        }
        return object ;
    }

    public <T> T getEnum(String key, Class enumClass) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        if (enumClass.isAssignableFrom(object.getClass())) {
            return (T) object;
        } else {
            return (T) Enum.valueOf(enumClass, object.toString()) ;
        }
    }

    public <T> T getEnum(String key, Class enumClass, Object defaultValue) {
        Object object = getEnum(key, enumClass);
        if (object == null) {
            return (T) defaultValue;
        }
        return (T) object ;
    }

    public <T> T getObject(String key, Class<? extends ContextMap> valueClass) {
        try {
            Object value = get(key) ;
            if (value != null) {
                if (valueClass.isAssignableFrom(value.getClass())) {
                    return (T) value;
                } else {
                    T typedValue = (T) findConstructor(valueClass).newInstance(value);
                    setImmutable(false);
                    put(key, typedValue);
                    setImmutable(true);
                    return typedValue;
                }
            } else {
                return null ;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e) ;
        }
    }

    private Constructor findConstructor(Class valueClass) throws NoSuchMethodException {
        try {
            return valueClass.getConstructor(Map.class) ;
        } catch (NoSuchMethodException e) {

        }

        try {
            return valueClass.getConstructor(HashMap.class) ;
        } catch (NoSuchMethodException e) {

        }

        try {
            return valueClass.getConstructor(ContextMap.class) ;
        } catch (NoSuchMethodException e) {

        }

        throw new NoSuchMethodException("no constructor") ;
    }

    public <T> List<T> getList(String key, Class<? extends ContextMap> valueClass) {
        try {
            List<ContextMap> list = (List<ContextMap>) get(key) ;
            boolean isNotAssignable = false ;
            for (ContextMap value : list) {
                if (!valueClass.isAssignableFrom(value.getClass())) {
                    isNotAssignable = true ;
                    break;
                }
            }

            if (isNotAssignable) {
                List<ContextMap> newList = list.getClass().getConstructor().newInstance() ;
                for (ContextMap value : list) {
                    if (!valueClass.isAssignableFrom(value.getClass())) {
                        newList.add((ContextMap) findConstructor(valueClass).newInstance(value)) ;
                    } else {
                        newList.add(value) ;
                    }
                }
                setImmutable(false);
                put(key, newList) ;
                setImmutable(true);
                return  (List<T>) newList ;
            } else {
                return (List<T>) list ;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e) ;
        }
    }

    public static ContextMap fromYaml(String yaml) {
        return new ContextMap(new Yaml().load(yaml)) ;
    }

    public static <T extends ContextMap> T fromYaml(String yaml, Class<T> valueClass) {
        return (T) create(fromYaml(yaml), valueClass) ;
    }

    public static ContextMap fromJson(String json) {
        return new ContextMap(new JSONObject(json).toMap()) ;
    }

    public static <T extends ContextMap> T fromJson(String yaml, Class<T> valueClass) {
        return (T) create(fromJson(yaml), valueClass) ;
    }

    public static ContextMap fromObject(Object object) {
        return new ContextMap(new ObjectMapper().convertValue(object, new TypeReference<Map<String, Object>>() {}));
    }

    public static <T extends ContextMap> T fromObject(String yaml, Class<T> valueClass) {
        return (T) create(fromObject(yaml), valueClass) ;
    }

    public static ContextMap create(ContextMap map, Class<? extends ContextMap> valueClass) {
        try {
            Constructor noArgs = valueClass.getConstructor() ;
            if (noArgs != null) {
                return (ContextMap) noArgs.newInstance() ;
            }
        } catch (NoSuchMethodException e) {

        } catch (Throwable e) {
            throw new RuntimeException(e) ;
        }

        try {
            Constructor mapArgs = valueClass.getConstructor(ContextMap.class) ;
            if (mapArgs != null) {
                return (ContextMap) mapArgs.newInstance(map) ;
            }
        } catch (NoSuchMethodException e) {

        } catch (Throwable e) {
            throw new RuntimeException(e) ;
        }

        throw new RuntimeException("no suitable constructor") ;
    }

    private boolean isExpression(Object expression) {
        return expression instanceof String && expression.toString().startsWith("${") && expression.toString().endsWith("}") ;
    }
}
