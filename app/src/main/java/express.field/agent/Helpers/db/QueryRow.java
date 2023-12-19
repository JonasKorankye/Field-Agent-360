package express.field.agent.Helpers.db;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueryRow extends ArrayList<Pair<String, Object>> {

    public Object getValue(){
        Map<String, Object> result = new HashMap<String, Object>();

        for (Pair<String, Object> pair: this) {
            result.put(pair.first, pair.second);
        }

        return result;
    }

    public void addField(String name, Object value){
        Pair field = new Pair(name, value);
        this.add(field);
    }
}
