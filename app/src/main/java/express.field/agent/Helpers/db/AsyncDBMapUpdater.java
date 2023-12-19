package express.field.agent.Helpers.db;

import android.os.AsyncTask;

import com.couchbase.lite.Database;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import express.field.agent.Request.exception.DbException;
import express.field.agent.Utils.ListIterator;

public class AsyncDBMapUpdater extends AsyncTask<Map<String, Object>, Void, Void> {

    private Database db;
    private AsyncResultListener handler;
    private String mDocId;

    public AsyncDBMapUpdater(Database db, AsyncResultListener handler) {
        this.db = db;
        this.handler = handler;
    }

    @Override
    protected Void doInBackground(Map<String, Object>... params) {

        mDocId = (String) params[0].get("id");
        try {
            CouchbaseDao.getInstance().saveMapObject(db, params[0]);
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        List<Map<String, Object>> list= new ArrayList<>();
        list.add(CouchbaseDao.getInstance().get(db, mDocId));
        handler.onResult(new ListIterator(list) {

        });
    }
}
