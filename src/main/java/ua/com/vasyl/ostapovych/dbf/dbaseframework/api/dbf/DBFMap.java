package ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;

import java.util.*;


public class DBFMap implements Map<DBFField,Object>{
    private final LinkedHashMap<DBFField,Object> map;
    private final Map<String,DBFField> mapFields;
    private final Map<Integer,DBFField> mapIndexFields;
    private int currentIndex;

    public DBFMap(){
        map = new LinkedHashMap<>();
        mapFields = new HashMap<>();
        mapIndexFields = new HashMap<>();
        currentIndex = 0;
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Object get(Object key) {
        return map.get(key);
    }

    public void clear() {
        map.clear();
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public Object put(DBFField key, Object dbfValue) {
        mapFields.put(key.getName(), key);
        mapIndexFields.put(currentIndex++,key);
        return map.put(key, dbfValue);
    }

    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends DBFField, ?> m) {
        throw new RuntimeException("Not supported");
    }

    public Set<DBFField> keySet() {
        return map.keySet();
    }

    public Collection<Object> values() {
        return map.values();
    }

    public Set<Map.Entry<DBFField, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public DBFField getFieldByName(String fieldName) {
        DBFField res = mapFields.get(fieldName);
        if (res != null) return res;
        else {
            res = mapFields.get(fieldName.toLowerCase());
            if (res == null)
                res = mapFields.get(fieldName.toUpperCase());
        }
        return res;
    }

    public DBFField getFieldByIndex(int index) {
        return mapIndexFields.get(index);
    }

    public Object[] generateRawRow(){
        Object[] res = new Object[this.mapIndexFields.keySet().size()];
        for (int i=0;i<mapIndexFields.keySet().size();i++){
            DBFField field = mapIndexFields.get(i);
            res[i] = get(field);
        }
        return res;
    }
}
