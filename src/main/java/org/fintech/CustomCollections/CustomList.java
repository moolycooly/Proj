package org.fintech.CustomCollections;

public interface CustomList<E> extends Iterable<E>{
    boolean contains(Object o);
    int size();
    E get(int index);
    void add(E t);
    void addAll(Iterable<? extends  E> c);
    boolean remove(Object o);
    boolean remove(int index);



}
