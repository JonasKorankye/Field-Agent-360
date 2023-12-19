package express.field.agent.Utils;

import java.util.Iterator;
import java.util.List;

public class ListIterator<T> extends IteratorImpl<T> {
    private List<T> list;

    private int nextRow;

    public ListIterator(List<T> list) {
        this.list = list;
    }

    @Override
    public Iterator<T> iterator()
    {
        return this;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean hasNext()
    {
        return nextRow < list.size();
    }

    @Override
    public T next()
    {
        if (nextRow >= list.size()) {
            return null;
        }

        T value = list.get(nextRow++);
        return value;
    }

    public void reset()
    {
        nextRow = 0;
    }

    @Override
    public void remove() {
        throw new IllegalStateException("Method remove() not implemented");
    }
}
