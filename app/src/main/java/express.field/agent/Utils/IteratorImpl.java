package express.field.agent.Utils;

import java.util.Iterator;

public abstract class IteratorImpl<T> implements Iterator<T>, Iterable<T> {
    public abstract int size();
}
