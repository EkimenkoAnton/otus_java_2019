package ru.otus;

import java.util.*;
import java.util.function.Consumer;


/*
 * DIY ArrayList
 * Написать свою реализацию ArrayList на основе массива.
 * class DIYarrayList<T> implements List<T>{...}
 *
 * Проверить, что на ней работают методы из java.util.Collections:
 * Collections.addAll(Collection<? super T> c, T... elements)
 * Collections.static <T> void copy(List<? super T> dest, List<? extends T> src)
 * Collections.static <T> void sort(List<T> list, Comparator<? super T> c)
 *
 * 1) Проверяйте на коллекциях с 20 и больше элементами.
 * 2) DIYarrayList должен имплементировать ТОЛЬКО ОДИН интерфейс - List.
 * 3) Если метод не имплементирован, то он должен выбрасывать исключение UnsupportedOperationException.
 */

public class DIYarrayList<T> implements List<T> {

    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_INNER_ARRAAY = {};
    private Object[] innerArray;
    private int size;

    public DIYarrayList () {
        innerArray = EMPTY_INNER_ARRAAY;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<T> iterator() {
        return new InnerIterator();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> E[] toArray(E[] a) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    private T getFromInnerArray(int index) {
        return (T) innerArray[index];
    }

    @Override
    public boolean add(T e) {
        if (size() == innerArray.length)
            grow();
        innerArray[size++] = e;
        return true;
    }

    private void grow() {
        innerArray = Arrays.copyOf(innerArray, newCapacity());
    }

    private int newCapacity () {
        if (innerArray == EMPTY_INNER_ARRAAY)
            return DEFAULT_CAPACITY;
        int oldCapacity = innerArray.length;
        return oldCapacity + (oldCapacity >> 1);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(int index) {
        Objects.checkIndex(index,size());
        return getFromInnerArray(index);
    }

    @Override
    public T set(int index, T element) {
        Objects.checkIndex(index,size());
        T oldValue = getFromInnerArray(index);
        innerArray[index] = element;
        return oldValue;
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator() {
        return new InnerListIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super T> comparator) {
        Arrays.sort((T[]) innerArray, 0, size, comparator);
    }

    private class InnerIterator implements Iterator<T> {

        int cursor;

        @Override
        public boolean hasNext() {
            return cursor != size;
        }


        @Override
        public T next() {
            if ( ! hasNext() )
                throw new NoSuchElementException();
            return DIYarrayList.this.getFromInnerArray(cursor++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            throw new UnsupportedOperationException();
        }
    }

    private class InnerListIterator extends InnerIterator implements ListIterator<T> {

        @Override
        public boolean hasPrevious() {
            throw new UnsupportedOperationException();
        }

        @Override
        public T previous() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(T e) {
            DIYarrayList.this.set(cursor-1, e);
        }

        @Override
        public void add(T e) {
            throw new UnsupportedOperationException();
        }
    }
}
