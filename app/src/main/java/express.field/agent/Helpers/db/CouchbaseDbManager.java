package express.field.agent.Helpers.db;

import android.content.Context;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseChangeListener;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.ListenerToken;


import express.field.agent.Helpers.ContextProvider;
import express.field.agent.Request.exception.DbException;


public class CouchbaseDbManager {

    public static final String DEFAULT_DATABASE = "field360";

    private static CouchbaseDbManager mInstance;
    private static Context mContext;

    private Database manager;

    public static CouchbaseDbManager getInstance() {
        if (mInstance == null) {
            synchronized (CouchbaseDbManager.class) {
                mInstance = new CouchbaseDbManager(ContextProvider.getInstance().getContext());
            }
        }

        return mInstance;
    }

    private CouchbaseDbManager(final Context context) {
        mContext = context;
        // Initialize the Couchbase Lite system
        CouchbaseLite.init(mContext);
    }

    public Database getExistingDatabase(String name) throws DbException {
        Database db = null;
        try {
            db = getManager();//.getExistingDatabase(name);
        } catch (Exception e) {
            throw new DbException(e);
        }

        return db;
    }

    /**
     * Get reference to a database. Creates a new database with that name if not already existing
     *
     * @param name
     * @return
     * @throws DbException
     */
    public Database getOrCreate(String name) throws DbException {
        Database db = null;
        try {
            db = getManager();//.getDatabase(name);
        } catch (Exception e) {
            throw new DbException(e);
        }

        return db;
    }

    /**
     * Closes a databases if opened
     *
     * @param name
     */
    public void close(String name) {
        Database db = getManager();//.getExistingDatabase(name);

        if (db != null){// && db.isOpen()) {
            try {
                db.close();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Deletes a database
     *
     * @param name
     * @throws DbException
     */
    public void delete(String name) throws DbException {
        try {
            Database db = getManager();//.getExistingDatabase(name);
            if (db != null) {
                db.close();
                db.delete();
            }
        } catch (CouchbaseLiteException e) {
            throw new DbException(e);
        }
    }

    /**
     * Closes manager and all opened databases
     */
    public void close() {
        try{
            getManager().close();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public ListenerToken addChangeListener(Database db, DatabaseChangeListener listener) {
        return db.addChangeListener(listener);
    }

    public void removeChangeListener(Database db, ListenerToken token) {
        db.removeChangeListener(token);
    }

    private Database getManager() {
        if (manager == null) {
            try {
                DatabaseConfiguration config = new DatabaseConfiguration();
                manager = new Database(DEFAULT_DATABASE, config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return manager;
    }

}
