package express.field.agent.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Util class to be used across the app for all operator involving unknown depth level for Maps
 * Edit : Overload methods to accept FormFieldName as keys to the values - useful for Form Renderer
 * Edit : Add depth level support for Lists
 */
public class MapUtils {

    private static Pattern indexPattern = Pattern.compile("^(\\$?\\w+)\\[(\\d+)\\]$");

    /**
     * Use this to safely get any value from a map at any depth or index in list
     * @param key - Location inside the map to search recursively - with infinite depth level and precise index
     *            example - customer.images[7].imageType - if null returns dataMap
     * @param dataMap - Source Data Map
     * @return - returns the object in the specified location. Can return null. If key is null
     * return the dataMap
     */
    public static Object getValue(String key, Map<String, Object> dataMap) {
        if (key == null) return dataMap;

        if (!key.contains(".")) {
            if (!key.contains("[")) {
                return dataMap.get(key);
            }
        }

        // the key contains '.', e.g.: customer.address.street, so we must walk through the data map to extract the value.
        String[] props = key.split("\\.");
        for (int i = 0; i < props.length; i++) {
            if (dataMap == null) break;

            String prop = props[i];
            Object stepValue;

            Matcher indexMatcher = indexPattern.matcher(prop);
            //indexed property e.g. documents[0]; group[1]='documents', group[2]='0'
            if (indexMatcher.matches()) {
                stepValue = getIndexedPropValue(indexMatcher, dataMap);
            } else {
                // if 'prop' is not indexed type -> just go 1 level down the data tree
                stepValue = dataMap.get(prop);
            }

            if (i == props.length-1)
                return stepValue;

            dataMap = (Map<String, Object>) stepValue;
        }

        return dataMap;
    }

    private static Object getIndexedPropValue(Matcher indexMatcher, Map<String, Object> dataMap) {
        // e.g. documents[3] is patterned into the following array by the 'indexPattern':
        // { "documents[3]", "documents", "3" }

        // get the list corresponding to the prop name.
        List<Map> selectedList = (List) dataMap.get(indexMatcher.group(1));

        // parse the list index from the prop name
        int index = Integer.parseInt(indexMatcher.group(2));

        // make sure list is initialized
        if (selectedList == null) selectedList = new LinkedList<>();

        while (selectedList.size() <= index) {
            //items in list are less then needed
            selectedList.add(new HashMap(0));
        }

        return selectedList.get(index);
    }



    /**
     * Use this to set any value to a map at any depth or index in list
     * @param key - Specific location to set the object. If an inner object or array don't exist
     *            they will be created recursively.
     * @param value - Value to set
     * @param map - Target Object
     * @return - false if the specified object is already present at specified key
     */
    public static boolean setValue(String key, Object value, Map<String, Object> map) {
        boolean modified = false;

        if (key.contains(".")) {
            String[] props = key.split("\\.");

            for (int i = 0; i < props.length - 1; i++) {
                String prop = props[i];
                Matcher m = indexPattern.matcher(prop);
                if (m.matches()) {
                    //indexed property e.g. documents[0]; group[1]='documents', group[2]='0'
                    prop = m.group(1);
                    int index = Integer.parseInt(m.group(2));
                    List<Map> list = (List) map.get(prop);
                    if (list == null) {
                        //create the list if not there
                        list = new LinkedList<>();
                        map.put(prop, list);
                        modified = true;
                    }
                    while (list.size() <= index) {
                        //items in list are less then needed
                        Map<String, Object> item = new LinkedHashMap<>();
                        list.add(item);
                        modified = true;
                    }
                    map = list.get(index);
                } else {
                    Map<String, Object> item = (Map) map.get(prop);
                    if (item == null) {
                        item = new LinkedHashMap<>();
                        map.put(prop, item);
                        modified = true;
                    }
                    map = item;
                }
            }
            String propertyName = props[props.length - 1];
            modified = innerSetValue(propertyName, value, map) || modified;
        } else if(key.endsWith("]")) {
            modified = innerSetListMember(key, value, map);
        } else {
            modified = innerSetValue(key, value, map) || modified;
        }

        return modified;
    }

    private static boolean innerSetListMember(String key, Object value, Map<String, Object> target) {
        Matcher m = indexPattern.matcher(key);
        if (!m.matches()) {
            return false;
        }

        boolean modified = false;
        String arrayKey = m.group(1);
        int index = Integer.parseInt(m.group(2));

        List<Object> list = (List<Object>) target.get(arrayKey);

        if (list == null && value == null) {
            return false;
        } else if (list == null) {
            list = new ArrayList<>();
        }

        if (list.size() <= index) {
            for (int i = list.size(); i <= index; i++) {
                list.add(null);
            }
        }

        list.set(index, value);
        modified = innerSetValue(arrayKey, list, target);

        return modified;
    }

    private static boolean innerSetValue(String key, Object value, Map<String, Object> target) {
        boolean modified = false;
        Object targetValue = target.get(key);

        if (value == null && targetValue == null) {
            modified = false;
        } else if(key.endsWith("]")) {
            modified = innerSetListMember(key, value, target);
        } else if (targetValue == null || !targetValue.equals(value)) {
            target.put(key, value);
            modified = true;
        }

        return modified;
    }

    /**
     * Helper function used to recursively copy all the properties from one map into another
     * @param source - Source Data Map
     * @param target - Target Data Map
     * @return - false if no value in the target map was updated.
     */
    public static boolean copyProperties(Map<String, Object> source, Map<String, Object> target) {
        boolean modified = false;

        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Object targetValue = target.get(key);

            if (!target.containsKey(key) && value != null) {
                target.put(key, value);
                modified = true;
            } else if (isSimpleType(value)) {
                modified = innerSetValue(key, value, target) || modified;
            } else if (value instanceof List) {
                // TODO: this could be handled better - going through the list
                target.put(key, value);
                modified = true;
            } else if (value instanceof Map) {
                if (!(targetValue instanceof Map)) {
                    targetValue = new HashMap<>();
                    target.put(key, targetValue);
                    modified = true;
                }

                modified = copyProperties((Map<String, Object>) value, (Map<String, Object>) targetValue) || modified;
            } else {
                if (value != null) {
                    System.out.println(String.format("Unexpected object type: [%s] with value: [%s]", value.getClass(), value.toString()));
                    modified = innerSetValue(key, value, target) || modified;
                }
            }
        }

        return modified;
    }

    /**
     * Copy properties from source map to target map for provided certain keys
     * @param source - Source Data Map
     * @param target - Target Data Map
     * @param keys - Map Keys
     */
    public static void copyExistingProperties(HashMap<String, Object> source, HashMap<String, Object> target, String[] keys) {
        for (String key : keys) {
            setValue(key, getValue(key, source), target);
        }
    }

    /**
     * Helper function to establish whether an object is an end value (not traversible).
     * @param object - Value to be evaluated
     * @return - False - when the object can be traversed e.g. Collection. True when an end value
     */
    public static boolean isSimpleType(Object object) {
        if (object == null || object instanceof String || object instanceof Boolean || object instanceof Date || object instanceof Number || object instanceof Enum) {
            return true;
        }

        return false;
    }

    /**
     * Helper function used to recursively traverse a collection and convert all found Enum to String
     * @param source - data source
     */
    public static void convertEnumsToStrings(Map<String, Object> source) {
        if (source != null) {
            for (Map.Entry<String, Object> entry : source.entrySet()) {
                if (entry.getValue() instanceof Enum<?>) {
                    entry.setValue(((Enum) entry.getValue()).name());
                } else if (entry.getValue() instanceof Map) {
                    convertEnumsToStrings((Map<String, Object>) entry.getValue());
                } else if (entry.getValue() instanceof List) {
                    convertEnumsToStrings((List<Object>) entry.getValue());
                }
            }
        }
    }

    /**
     * Helper function used to recursively traverse a collection and convert all found Enum to String
     * @param source - data source
     */
    public static void convertEnumsToStrings(List<Object> source){
        if (source != null) {
            for (Object entry : source) {
                if (entry instanceof Enum<?>) {
                    entry = ((Enum) entry).name();
                } else if (entry instanceof Map) {
                    convertEnumsToStrings((Map<String, Object>) entry);
                } else if (entry instanceof List) {
                    convertEnumsToStrings((List<Object>) entry);
                }
            }
        }
    }

}
