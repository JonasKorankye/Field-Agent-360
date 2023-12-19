package express.field.agent.Helpers;

import android.text.TextUtils;

import com.couchbase.lite.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import express.field.agent.Helpers.db.AsyncResultListener;
import express.field.agent.Helpers.db.CouchbaseDao;
import express.field.agent.Helpers.db.CouchbaseDbManager;
import express.field.agent.Helpers.db.QueryIterator;
import express.field.agent.Helpers.db.QueryRow;
import express.field.agent.Helpers.db.enums.DbObjectModel;
import express.field.agent.Helpers.db.enums.DbObjectSyncStatus;
import express.field.agent.Helpers.db.filters.Filter;
import express.field.agent.Model.db.ModelHistory;
import express.field.agent.Model.db.ModelHistoryEntry;
import express.field.agent.Request.exception.DbException;
import express.field.agent.Utils.Constants;
import express.field.agent.Utils.DateUtils;
import express.field.agent.Utils.ObjectUtils;
import express.field.agent.Utils.OfflineIdGenerator;

public class DbHelper {

    private static DbHelper mInstance = null;

    private CouchbaseDbManager mDbManager;
    private CouchbaseDao mDaoService;

    private DbHelper() {
        mDbManager = CouchbaseDbManager.getInstance();
        mDaoService = CouchbaseDao.getInstance();
    }

    public static DbHelper getInstance() {
        if (mInstance == null) {
            synchronized (DbHelper.class) {
                mInstance = new DbHelper();
            }
        }

        return mInstance;
    }


    public String getDatabaseName() {
        return CouchbaseDbManager.DEFAULT_DATABASE;
    }

    public Database getDatabase() throws DbException {
        return mDbManager.getExistingDatabase(getDatabaseName());
    }

    public void save(String id, Map<String, Object> value) throws DbException {
        mDaoService.save(getDatabase(), id, value);
        updateModelHistory(value);
    }

    public void save(String id, Map<String, Object> value, String message) throws DbException {
        mDaoService.save(getDatabase(), id, value);
        updateModelHistory(value, message);
    }

    public void save(Map<String, Object> value, String message) throws DbException {
        mDaoService.save(getDatabase(), (String) value.get("id"), value);
        updateModelHistory(value, message);
    }

    public void save(HashMap<String, Object> object, String message) throws DbException {
        mDaoService.save(getDatabase(), object);
        updateModelHistory(object, message);
    }

    public void save(HashMap<String, Object> object) throws DbException {
        mDaoService.save(getDatabase(), object);
        updateModelHistory(object);
    }

    public void save(List<HashMap<String, Object>> objects) throws DbException {
        for (int i = 0; i < objects.size(); i++) {
            updateModelHistory(objects.get(i));
        }
        mDaoService.saveObjects(getDatabase(), objects);
    }

    public void saveAsync(HashMap<String, Object> object, AsyncResultListener listener) {
        Database db = null;
        try {
            db = getDatabase();
        } catch (DbException e) {
            listener.onException(e);
        }

        mDaoService.saveObjectsAsync(db, listener, object);
        updateModelHistory(object);
    }

    public void saveDraft(HashMap<String, Object> object) throws DbException {
        if (object.get(Constants.DATABASE_ID) == null) {
            create(object, false);
        } else {
            object.put(Constants.SYNC_STATUS, DbObjectSyncStatus.DRAFT);

            save(object);
        }
    }

    public void saveWithSyncStatus(HashMap<String, Object> object, DbObjectSyncStatus syncStatus) throws DbException {
        if (object.get(Constants.DATABASE_ID) == null) {
            create(object, false);
        } else {
            object.put(Constants.SYNC_STATUS, syncStatus);

            save(object);
        }
    }

    public void saveDraftAsync(HashMap<String, Object> object, AsyncResultListener listener) {
        if (object.get(Constants.DATABASE_ID) == null) {
            createAsync(object, false, listener);
        } else {
            object.put(Constants.SYNC_STATUS, DbObjectSyncStatus.DRAFT);

            saveAsync(object, listener);
        }
    }

    public void create(HashMap<String, Object> object, boolean isConfirmed) throws DbException {
        DbObjectSyncStatus status = isConfirmed ? DbObjectSyncStatus.CONFIRMED : DbObjectSyncStatus.DRAFT;
        object.put(Constants.SYNC_STATUS, status);

        createId(object);

        save(object);
//        FirebaseAnalyticsService.getInstance().logCreateRecord(DbObjectModel.valueOf(object.get(Constants.MODELNAME)));
    }

    public void create(HashMap<String, Object> object) throws DbException {
        createId(object);

        save(object);
//        FirebaseAnalyticsService.getInstance().logCreateRecord(DbObjectModel.valueOf(object.get(Constants.MODELNAME)));
    }

    public void updateModelHistory(final Map<String, Object> object) {
        updateModelHistory(object, null);
    }

    public void updateModelHistory(final Map<String, Object> object, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DbObjectModel objectModel = DbObjectModel.valueOf(object.get(Constants.MODELNAME));

                if (objectModel == DbObjectModel.SYNC_HISTORY) {
                    return;
                }
                String id = (String) object.get(Constants.DATABASE_ID);
                if (id == null) {
                    return;
                }

                try {
                    String status = DbObjectSyncStatus.valueOf(object.get(Constants.SYNC_STATUS)).name();

                    ModelHistory modelHistory = null;
                    Map<String, Object> history = get(id + Constants.SYNC_HISTORY_POSTFIX);
                    if (history != null) {
                        modelHistory = ObjectUtils.mapToObject(history, ModelHistory.class);
                    }
                    if (modelHistory == null) {
                        modelHistory = new ModelHistory();
                        modelHistory.setId(object.get(Constants.DATABASE_ID) + Constants.SYNC_HISTORY_POSTFIX);
                        modelHistory.setHistoryModel(objectModel.name());
                    }

                    if (modelHistory.getHistory().size() > 9) {
                        List<ModelHistoryEntry> prunedHistory = new ArrayList<>();
                        for (int i = modelHistory.getHistory().size() - 1; i > modelHistory.getHistory().size() - 5; i--) {
                            prunedHistory.add(modelHistory.getHistory().get(i));
                        }
                        modelHistory.setHistory(prunedHistory);
                    }

                    if (TextUtils.isEmpty(message)) {
                        modelHistory.getHistory().add(new ModelHistoryEntry(status, DateUtils.format(new Date(), Constants.DB_DATE_FORMAT_STRING_LONG)));
                    } else {
                        modelHistory.getHistory().add(new ModelHistoryEntry(status, DateUtils.format(new Date(), Constants.DB_DATE_FORMAT_STRING_LONG), message));
                    }

                    save(ObjectUtils.objectToMap(modelHistory));
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void saveDownloadHistoryByModel(HashMap<String, Object> history) {
        if (history.get(Constants.DATABASE_ID) == null) {
            String id = Constants.SYNC_HISTORY_MODEL_KEY + history.get(Constants.SYNC_HISTORY_MODEL_KEY);
            history.put(Constants.DATABASE_ID, id);
            history.put(Constants.MODELNAME, DbObjectModel.SYNC_HISTORY);
        }
        try {
            mDaoService.save(getDatabase(), history);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Object> getDownloadHistoryByModel(DbObjectModel model) {
        String id = Constants.SYNC_HISTORY_MODEL_KEY + model.name();
        try {
            return get(id);
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getLastDownloadTimeStamp(DbObjectModel model) {
        HashMap<String, Object> history = getDownloadHistoryByModel(model);
        if (history != null && history.get(Constants.SYNC_HISTORY_LAST_DOWNLOAD_TIMESTAMP_KEY) != null) {
            return (String) history.get(Constants.SYNC_HISTORY_LAST_DOWNLOAD_TIMESTAMP_KEY);
        }

        return Constants.SYNC_HISTORY_DEFAULT_TIMESTMAP;
    }

    public void createWithoutSyncStatus(HashMap<String, Object> object) throws DbException {
        createId(object);

        save(object);
    }


    public void createAsync(HashMap<String, Object> object, boolean isConfirmed, AsyncResultListener listener) {
        DbObjectSyncStatus status = isConfirmed ? DbObjectSyncStatus.CONFIRMED : DbObjectSyncStatus.DRAFT;
        object.put(Constants.SYNC_STATUS, status);

        createId(object);

        saveAsync(object, listener);
    }

    public String createId(HashMap<String, Object> object) {
        String id = OfflineIdGenerator.generateUUID();

        object.put(Constants.DATABASE_ID, id);

        return id;
    }

    public HashMap<String, Object> get(String id) throws DbException {
        return mDaoService.get(getDatabase(), id);
    }

    /**
     * Returns defensive copy of database object, so
     * modifications won't effect original data(in case of couchbase lite Cached Documents won't be affected)
     */
    public HashMap<String, Object> getSafe(String id) throws DbException {
        return mDaoService.get(getDatabase(), id);
    }

    public boolean isDocumentExistWithId(String id) {
        try {
            return get(id) != null;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean delete(String id) {
        try {
            Database db = getDatabase();
            mDaoService.delete(db, id);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveMaps(final List<Map<String, Object>> objects) throws DbException {
        mDaoService.saveMaps(getDatabase(), objects);
    }

    public void saveMapAsync(final Map<String, Object> object, final AsyncResultListener handler) throws DbException {
        mDaoService.saveMapObjectAsync(getDatabase(), object, handler);
    }

}