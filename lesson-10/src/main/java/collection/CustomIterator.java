package collection;

import java.util.function.Consumer;

public interface CustomIterator<E>{

    boolean hasNext();
    E next();
    void forEachRemaining(Consumer<? super E> action);
    void remove();
}
