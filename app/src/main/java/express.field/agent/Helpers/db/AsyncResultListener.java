package express.field.agent.Helpers.db;


import express.field.agent.Utils.IteratorImpl;

public interface AsyncResultListener<T> {
    void onResult(IteratorImpl<T> iterator);
    void onStart();
    void onException(Exception e);
    Class<T> getBindClass();
}