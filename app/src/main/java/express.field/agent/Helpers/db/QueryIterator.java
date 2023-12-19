package express.field.agent.Helpers.db;


import java.util.Iterator;
import java.util.List;

import express.field.agent.Utils.IteratorImpl;

public class QueryIterator extends IteratorImpl {

    private List<QueryRow> rows;

    private int nextRow;

    public QueryIterator(List<QueryRow> rows) {
        this.rows = rows;
        nextRow = 0;
    }

    @Override
    public Iterator iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return nextRow < rows.size();
    }

    @Override
    public Object next() {
        if (nextRow >= rows.size()) {
            return null;
        }

        QueryRow row = rows.get(nextRow++);
        return row.getValue();
    }

    @Override
    public void remove() {
        throw new IllegalStateException("Method remove() not implemented");
    }

    public void reset() {
        nextRow = 0;
    }

    public int size() {
        if (rows == null)
            return 0;

        return rows.size();
    }
}
