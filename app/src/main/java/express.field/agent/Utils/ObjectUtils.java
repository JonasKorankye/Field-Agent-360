package express.field.agent.Utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Util class and default implementation of Jackson Serializer
 */
public class ObjectUtils {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Map<String, Object> constantsMap = new HashMap<>();

    private ObjectUtils() {
    }

    static {
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        /**
         * ALLOW COMMENTS IN JSON FILES
         * Two types of comments can be used  /* *\/ and //
         */
        mapper.getFactory().enable(JsonParser.Feature.ALLOW_COMMENTS);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Cache constants key/value for later usage when deserialize json
        Field[] fields = Constants.class.getDeclaredFields();

        for (Field field : fields) {
            try {
                constantsMap.put(field.getName(), field.get(Constants.class));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T extends Object> HashMap<String, Object> objectToMap(T object) {
        byte[] json = objectToJson(object);
        HashMap<String, Object> map = jsonToMap(json);

        return map;
    }

    public static <T extends Object> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        byte[] json = objectToJson(map);
        T result = jsonToObject(json, clazz);

        return result;
    }

    public static HashMap<String, Object> jsonToMap(byte[] json) {
        return jsonToObject(json, HashMap.class);
    }

    public static List<Map<String, Object>> jsonBytesToList(byte[] json) {
        List<Map<String, Object>> myObjects = null;
        try {
            myObjects =
                mapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {
                });
        } catch (IOException e) {
            e.printStackTrace();
        }


        return myObjects;
    }

    public static <T extends Object> T jsonToObject(byte[] json, Class<T> clazz) {
        T result = null;
        try {
            result = mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public static <T extends Object> T jsonToObject(String json, Class<T> clazz) {
        T result = null;
        try {
            result = mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    public static String toJsonString(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    public static <T> T fromJsonString(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] objectToJson(Object object) {
        byte[] json = null;
        try {
            json = mapper.writeValueAsBytes(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return json;
    }

    public static String objectToJsonString(Object object) {

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }


    public static <T extends Object> T changeObjectType(Object object, Class<T> clazz) {
        return jsonToObject(objectToJson(object), clazz);
    }

    /**
     * Check if provided string is valid json
     *
     * @param jsonText
     * @return
     */
    public static boolean isJsonValid(String jsonText) {
        try {
            new JSONObject(jsonText);
        } catch (JSONException ex) {
            try {
                new JSONArray(jsonText);
            } catch (JSONException e) {
                return false;
            }
        }

        return true;
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }



    public static <T> List<T> changeListObjectType(Object list, Class<T> itemClass) {
        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, itemClass);

        return mapper.convertValue(list, type);
    }
}
