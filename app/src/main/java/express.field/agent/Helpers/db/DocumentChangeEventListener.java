package express.field.agent.Helpers.db;

import android.os.Handler;
import android.os.Looper;

import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseChange;
import com.couchbase.lite.DatabaseChangeListener;
import com.couchbase.lite.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import express.field.agent.Utils.ObjectUtils;

public abstract class DocumentChangeEventListener<T> implements DatabaseChangeListener {
    private Class<T> clazz;
    private Set<String> ids;

    public DocumentChangeEventListener(Class<T> clazz) {
        this.clazz = clazz;
    }

    public DocumentChangeEventListener(Class<T> clazz, String... ids) {
        this.clazz = clazz;
        this.ids = new HashSet<>();
        for (String  id: ids) {
            this.ids.add(id);
        }
    }

    @Override
    public void changed(DatabaseChange change) {
        for (String currChange: change.getDocumentIDs()) {
            final String docId = currChange;
            if (ids == null || ids.contains(docId)) {
                final T result = getDocument(change.getDatabase(), docId);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onChange(docId, result);
                    }
                });
            }
        }
    }

    private T getDocument(Database db, String changedDocID) {
        Map<String, Object> doc = null;
        Document cbDoc = db.getDocument(changedDocID);//CouchbaseDaoService.getInstance().get(db, change.getDocumentId());
        if (cbDoc != null) {
            doc = cbDoc.toMap();
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return (T) doc;
        } else {
            return ObjectUtils.mapToObject(doc, clazz);
        }
    }

    public abstract void onChange(String documentId, T object);
}
