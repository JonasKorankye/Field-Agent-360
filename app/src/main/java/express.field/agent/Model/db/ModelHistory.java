package express.field.agent.Model.db;


import java.util.ArrayList;
import java.util.List;

import express.field.agent.Helpers.db.enums.DbObjectModel;

public class ModelHistory {

    public ModelHistory() {
        setModelname(DbObjectModel.SYNC_HISTORY);
        history = new ArrayList<>();
    }

    private String id;
    private List<ModelHistoryEntry> history;
    private DbObjectModel modelname;
    private String historyModel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ModelHistoryEntry> getHistory() {
        return history != null ? history : new ArrayList<ModelHistoryEntry>();
    }

    public void setHistory(List<ModelHistoryEntry> history) {
        this.history = history;
    }

    public DbObjectModel getModelname() {
        return modelname;
    }

    public void setModelname(DbObjectModel modelname) {
        this.modelname = modelname;
    }

    public String getHistoryModel() {
        return historyModel;
    }

    public void setHistoryModel(String historyModel) {
        this.historyModel = historyModel;
    }
}
