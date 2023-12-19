package express.field.agent.Helpers.db.filters;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import express.field.agent.Helpers.db.CouchbaseDbManager;
import express.field.agent.Helpers.db.QueryRow;
import express.field.agent.Utils.Constants;

public abstract class Filter {

    public Database database;

    public Filter() {
        database = null;
        try {
            database = CouchbaseDbManager.getInstance().getExistingDatabase("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected final int validLimitSize(int limit) {
        return limit <= 0 ? Constants.DEFAULT_FILTER_LIMIT_SIZE : limit;
    }

    protected final SelectResult.As[] getProperties(String[] selectFields) {
        SelectResult.As[] defaultProps = getDefaultProps();

        int selectFieldsSize = selectFields == null ? 0 : selectFields.length;
        int selectSize = selectFieldsSize + defaultProps.length;
        SelectResult.As[] props = new SelectResult.As[selectSize];

        if (selectFields != null && selectFields.length > 0) {
            for (int i = 0; i < selectFields.length; i++) {
                props[i] = (SelectResult.property(selectFields[i]));
            }
        }

        for (int i = 0; i < defaultProps.length; i++) {
            props[selectFieldsSize + i] = defaultProps[i];
        }

        return props;
    }

    protected SelectResult.As[] getDefaultProps() {
        return new SelectResult.As[] {SelectResult.expression(Meta.id),
            SelectResult.property(Constants.CBS_ID),
            SelectResult.property(Constants.SYNC_STATUS),
            SelectResult.property(Constants.PROCESS_STATUS),
            SelectResult.property(Constants.MODELNAME),
            SelectResult.property(Constants.RECORD_ID)};
    }

    public final List<QueryRow> execQuery(){

        ArrayList<QueryRow> rows = new ArrayList<>();

        Query query = getQuery();

        try {
            ResultSet rs = query.execute();
            for (Result result : rs) {
                QueryRow currRow = new QueryRow();
                Map<String, Object> data;

                if((result.getKeys()).contains(database.getName())) {
                    Dictionary all = result.getDictionary(database.getName());
                    data = all.toMap();
                } else {
                    data = result.toMap();
                }

                Iterator it = data.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    currRow.addField(pair.getKey().toString(), pair.getValue());
                    it.remove();
                }

                rows.add(currRow);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return rows;
    }

    protected abstract Query getQuery();

}
