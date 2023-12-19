package express.field.agent.Helpers.db;

import android.content.Context;

import com.couchbase.lite.Database;

import java.io.IOException;

import express.field.agent.Request.exception.DbException;

public class DbInitializer {

    private static DbInitializer mDbInitializer = null;

    protected Context mApp;

    protected CouchbaseDbManager mDatabaseManager;

    private boolean resetDb;

    private DbInitializer(final Context context) {
        mApp = context;
        mDatabaseManager = CouchbaseDbManager.getInstance();
    }

    public static DbInitializer getInstance(final Context context) {
        if (mDbInitializer == null) {
            mDbInitializer = new DbInitializer(context);
        }

        return mDbInitializer;
    }

    public void init() {
        try {
            runIt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runIt() throws DbException, IOException {
        CouchbaseDbManager dbManager = CouchbaseDbManager.getInstance();

        String name = CouchbaseDbManager.DEFAULT_DATABASE;

        if (resetDb) {
            try {
                mDatabaseManager.delete(name);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        Database db = dbManager.getExistingDatabase(name);

        if (db == null) {
            db = dbManager.getOrCreate(name);
        }
    }

}
