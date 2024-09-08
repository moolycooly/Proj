package org.fintech.CustomCollections;


import java.util.*;


public class CustomLinkedList<E> implements CustomList<E>{

    private int size = 0;
    private Node<E> first;
    private Node<E> last;

    public CustomLinkedList() {
    }
    public CustomLinkedList(Iterable<? extends E> c) {
        addAll(c);
    }
    @Override
    public boolean contains(Object o) {
        Node<E> tmp = first;
        while(tmp != null) {
            if((o == null && tmp.item == null) || (o != null && o.equals(tmp.item))) {
                return true;
            }
            tmp=tmp.next;
        }
        return false;
    }

    @Override
    public int size() {
       return size;
    }
    @Override
    public E get(int index) {
        checklen(index);
        Node<E> tmp = first;
        int i = 0;
        while(tmp != null) {
            if(i == index) {
                return tmp.item;
            }
            tmp = tmp.next;
            i++;
        }
        return null;

    }

    @Override
    public void add(E e) {
        Node<E> node = new Node<>(last,e,null);
        if(last!=null) {
            last.next = node;
        }
        last = node;
        if(first==null) {
            first = node;
        }
        size++;
    }
    @Override
    public void addAll(Iterable<? extends  E> c) {
        if(c!=null) {
            for (E e : c) {
                this.add(e);
            }
        }

    }

    @Override
    public boolean remove(Object o) {
        Node<E> tmp = first;
        while(tmp != null) {
            if((o == null && tmp.item == null) || (o != null && o.equals(tmp.item))) {
                deleteNode(tmp);
                return true;
            }
            tmp = tmp.next;
        }
        return false;
    }

    @Override
    public boolean remove(int index) {
        checklen(index);
        Node<E> tmp = first;
        int i = 0;
        while(tmp != null) {
            if(i == index) {
                deleteNode(tmp);
                return true;
            }
            tmp = tmp.next;
            i++;
        }
        return false;
    }

    private void deleteNode(Node<E> node){

         if(node == null || first == null ) {
             throw new NoSuchElementException();
         }
        if(node == first) {
            first = node.next;
            if(first!= null) {
                first.prev = null;
            }
        }
        else if(node == last) {
            last = node.prev;
            if(last != null) {
                last.next = null;
            }
        }
        else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        size--;
    }

    void checklen(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Node<E> tmp = first;
        while(tmp != null) {
            stringBuilder.append(tmp.item);
            tmp = tmp.next;
            if(tmp != null) stringBuilder.append(" ");
        }
        return stringBuilder.toString();

    }

    @Override
    public Iterator<E> iterator() {
        return new CustomLinkedListIterator();
    }
    private class CustomLinkedListIterator implements Iterator<E> {

        private Node<E> prev;
        private Node<E> node = first;
        @Override
        public boolean hasNext() {
            return node != null;
        }
        @Override
        public E next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            E item = node.item;
            prev = node;
            node = node.next;
            return item;
        }
        public void remove() {
            if(prev == null) {
                throw new NoSuchElementException();
            }
            deleteNode(prev);
            prev = null;
        }
    }
    private static class Node<E> {
        private E item;
        private Node<E> next;
        private Node<E> prev;
        public Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
        public Node() {

        }
    }
}
