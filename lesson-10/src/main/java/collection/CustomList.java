package collection;

public interface CustomList<E>  extends  CustomIterable<E>{
    boolean contains(Object o);
    int size();
    E get(int index);
    void add(E t);
    void addAll(Iterable<? extends  E> c);
    void addAll(CustomIterable<? extends  E> c);
    boolean remove(Object o);
    boolean remove(int index);

}
