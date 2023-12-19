package express.field.agent.Helpers.db;

import android.os.AsyncTask;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;


import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import express.field.agent.Helpers.db.enums.DbObjectModel;
import express.field.agent.Helpers.db.enums.DbObjectSyncStatus;
import express.field.agent.Helpers.db.filters.Filter;
import express.field.agent.Request.exception.DbException;
import express.field.agent.Utils.Constants;
import express.field.agent.Utils.MapUtils;
import express.field.agent.Utils.ObjectUtils;
import express.field.agent.security.Decryptor;
import express.field.agent.security.Encryptor;

public class CouchbaseDao {

    private CouchbaseDao() {
    }

    public static CouchbaseDao getInstance() {
        return new CouchbaseDao();
    }

    public void save(Database db, String id, Map<String, Object> newProps) throws DbException {
        MutableDocument mutableDoc = null;
        Document doc = db.getDocument(id);
        boolean isNew = (doc == null);
        if (isNew) {
            //doc = db.getDocument(id);
            mutableDoc = new MutableDocument(id);
        } else {
            newProps.put("_rev", doc.getSequence());
            mutableDoc = doc.toMutable();
        }
        boolean isModified = false;

        final Map<String, Object> currProps;
        if (isNew) {
            currProps = newProps;
        } else {
            Map<String, Object> docProps = doc.toMap();
            currProps = new LinkedHashMap<>(docProps);

            //DO NOT OVERRIDE DRAFT documents with downloaded ones
            Map<String, Object> newSync = (Map) newProps.get("sync");
            boolean isDownloaded = (newSync != null && DbObjectSyncStatus.DOWNLOADED.toString().equals(newSync.get("status")));
            if (isDownloaded) {
                Map<String, Object> currSync = (Map) currProps.get("sync");
                String currStatus = (currSync != null) ? (String) currSync.get("status") : "";

                if (hasNoSavableStatus(currStatus)) {
                    return;
                }
            }

            currProps.remove("cipher");
            isModified = MapUtils.copyProperties(newProps, currProps);
        }

        long startTS = System.currentTimeMillis();

        //MapUtils.removeNullValues(currProps);

        if (isNew || isModified) {
            final DbObjectModel dbObjectModel = checkValidType(currProps);
//            checkSize(id, currProps);
            try {
                //setDocumentExpirationTime(doc, dbObjectModel);

                MapUtils.convertEnumsToStrings(currProps);

                Encryptor encryptor = new Encryptor(currProps);
                Thread thread = new Thread(encryptor);
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Map<String, Object> map = encryptor.getValue();

                mutableDoc.setData(map);
                db.save(mutableDoc);
                //doc.putProperties(currProps);
//                doc.update(new Document.DocumentUpdater()
//                {
//                    @Override
//                    public boolean update(UnsavedRevision newRevision)
//                    {
//                        Map<String, Object> properties = newRevision.getUserProperties();
//                        newRevision.setUserProperties(currProps);
//                        return true;
//                    }
//                });
            } catch (Exception e) {
                throw new DbException(e);
            }
        }
    }

    private void setDocumentExpirationTime(final Document doc, final DbObjectModel dbObjectModel) {
//        long expiryIntervalMillis = Configuration.getInstance().getDocExpiryMillis(dbObjectModel);
//        if(expiryIntervalMillis > 0){
//            Date timeToLive = new Date(System.currentTimeMillis() + expiryIntervalMillis);
//            doc.setExpirationDate(timeToLive);
//        }
//        doc.setExpirationDate(new Date(Long.MAX_VALUE));
    }

    public static boolean hasNoSavableStatus(final String itemStatus) {
        return DbObjectSyncStatus.DRAFT.toString().equals(itemStatus)
            || DbObjectSyncStatus.SCHEDULED.toString().equals(itemStatus)
            || DbObjectSyncStatus.SYNCING.toString().equals(itemStatus)
            || DbObjectSyncStatus.FAILED.toString().equals(itemStatus);
    }

    public void save(Database db, HashMap<String, Object> object) throws DbException {
        String id = (String) object.get(Constants.DATABASE_ID);

        save(db, id, object);
    }

    public void saveMaps(final Database db, final List<Map<String, Object>> objects) throws DbException {
        final StringBuilder errorMessage = new StringBuilder();
        boolean success = true;
        synchronized (this) {
            try {
                for (Map<String, Object> obj : objects) {
                    save(db, getId(obj), obj);
                }
            } catch (Exception e) {
                errorMessage.append(e.getMessage());
                success = false;
            }
        }

        if (!success)
            throw new DbException("Transaction failure: " + errorMessage.toString());
    }


    public void saveObjects(final Database db, final List<HashMap<String, Object>> objects) throws DbException {
        final StringBuilder errorMessage = new StringBuilder();
        boolean success = true;
        synchronized (this) {
            try {
                for (HashMap<String, Object> obj : objects) {
                    save(db, obj);
                }
            } catch (Exception e) {
                errorMessage.append(e.getMessage());
                success = false;
            }
        }

        if (!success)
            throw new DbException("Transaction failure: " + errorMessage.toString());
    }

    void saveMapObject(final Database db, final Map<String, Object> mapObj) throws DbException {
        final StringBuilder errorMessage = new StringBuilder();
        boolean success = true;
        synchronized (this) {
            try {
                save(db, mapObj.get("id").toString(), mapObj);
            } catch (Exception e) {
                errorMessage.append(e.getMessage());
                success = false;
            }
        }

        if (!success)
            throw new DbException("Transaction failure: " + errorMessage.toString());
    }

    public void saveObjectsAsync(final Database db, AsyncResultListener listener, HashMap<String, Object>... objects) {
        AsyncDbUpdater updater = new AsyncDbUpdater(db, listener);
        updater.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, objects);
    }

    public void saveMapObjectAsync(final Database db, Map<String, Object> mapObj, AsyncResultListener listener) {
        AsyncDBMapUpdater updater = new AsyncDBMapUpdater(db, listener);
        updater.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mapObj);
    }

    public HashMap<String, Object> get(Database db, String id) {
        Document doc = db.getDocument(id);
        if (doc == null) {
            return null;
        }

        Decryptor decryptor = new Decryptor(doc.toMap());
        Thread thread = new Thread(decryptor);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, Object> props = decryptor.getValue();


        //return modifiable bind
        return new HashMap<>(props);
    }



    public void delete(Database db, String id) throws DbException {
        try {
            Map<String, List<String>> revs = new HashMap<>();
            List<String> list = Collections.singletonList("*");
            revs.put(id, list);

            db.purge(id);
        } catch (Exception e) {
            // UtLog.E(TAG, String.format("Could not delete document: [%s]", id), e);
            throw new DbException(e);
        }
    }

    public QueryIterator runQuery(Filter filter) throws DbException {
        try {
            List<QueryRow> rows = filter.execQuery();
            QueryIterator iterator = new QueryIterator(rows);

            return iterator;
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    public void runQueryAsync(Filter filter, AsyncResultListener handler) {
        AsyncQueryRunner asyncQueryRunner = new AsyncQueryRunner(filter, handler);
        asyncQueryRunner.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Checks if document exceeds max allowed size
     * <p>
     * https://github.com/couchbase/couchbase-lite-android/issues/256
     * https://github.com/couchbase/couchbase-lite-android/issues/357
     *
     * @param id
     * @param object
     * @throws DbException
     */
    public void checkSize(String id, Map<String, Object> object) throws DbException {
        //va
        try {
            byte[] bytes = ObjectUtils.objectToJson(object);
            if (bytes.length >= 1000 * 1000 * 2) {
                //using 1000 instead of 1024 on purpose to reduce even more the allowed size
                throw new DbException(String.format("Cannot store %s in DB. Size limit exceeded %d", id, bytes.length));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private DbObjectModel checkValidType(Map<String, Object> obj) throws DbException {
        String id = getId(obj);
        id = id != null ? id : "[noid]";

        Object model = obj.get(Constants.MODELNAME);
        if (model == null) {
            throw new DbException(String.format("DbObject %s has no model set", id));
        }
        if (model instanceof DbObjectModel) {
            return (DbObjectModel) model;
        }

        String modelname = (String) model;
        try {
            return DbObjectModel.valueOf(modelname);
        } catch (Exception e) {
            throw new DbException(String.format("Invalid model %s for DbObject %s", modelname, id));
        }
    }

    public String getId(Map<String, Object> obj) {
        String id = obj.get("_id") != null ? (String) obj.get("_id") : (String) obj.get("id");

        return id;
    }
}
