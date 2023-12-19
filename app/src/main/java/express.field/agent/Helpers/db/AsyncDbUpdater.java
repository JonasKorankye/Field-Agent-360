package express.field.agent.Helpers.db;

import android.os.AsyncTask;

import com.couchbase.lite.Database;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import express.field.agent.Request.exception.DbException;

public class AsyncDbUpdater extends AsyncTask<HashMap<String, Object>, Void, Void> {

    private Database db;

    private AsyncResultListener handler;

    public AsyncDbUpdater(Database db, AsyncResultListener handler) {
        this.db = db;
        this.handler = handler;
    }

    @Override
    protected Void doInBackground(HashMap<String, Object>... params) {
        try {
            CouchbaseDao.getInstance().saveObjects(db, Arrays.asList(params));
        } catch (DbException e) {
            handler.onException(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        handler.onResult(new QueryIterator(Collections.<QueryRow>emptyList()));
    }
}
