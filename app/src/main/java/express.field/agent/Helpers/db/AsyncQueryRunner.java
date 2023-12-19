package express.field.agent.Helpers.db;

import android.os.AsyncTask;

import java.util.List;

import express.field.agent.Helpers.db.filters.Filter;

public class AsyncQueryRunner extends AsyncTask<Object, Void, QueryIterator>
{

    private Filter filter;
    private AsyncResultListener handler;

    public AsyncQueryRunner(Filter filter, AsyncResultListener handler)
    {
        this.filter = filter;
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        handler.onStart();
    }

    @Override
    protected QueryIterator doInBackground(Object... params) {
        try
        {
            List<QueryRow> rows = filter.execQuery();
            QueryIterator iterator = new QueryIterator(rows);

            return iterator;
        }
        catch (Exception e)
        {
            handler.onException(e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(QueryIterator queryIterator) {
        handler.onResult(queryIterator);
    }
}
