package org.adjective.x5.util;

import java.util.function.IntFunction;

public class ArrayBuilder<E> {
    private final IntFunction<E[]> newArray;
    private E[] array;
    private int index;

    public ArrayBuilder(IntFunction<E[]> newArray, int size) {
        this.newArray = newArray;
        this.array = newArray.apply(size);
        this.index = 0;
    }

    public E[] toArray() {
        final E[] result = array;
        array = null;
        index = 0;
        return result;
    }

    public void append(E element) {
        if (this.array == null) {
            this.array = newArray.apply(1);
            this.index = 0;
        } else if (index >= this.array.length) {
            final E[] tmp = this.array;
            this.array = newArray.apply(index + 1);
            System.arraycopy(tmp, 0, array, 0, tmp.length);
        }
        array[index] = element;
        index++;
    }
}
