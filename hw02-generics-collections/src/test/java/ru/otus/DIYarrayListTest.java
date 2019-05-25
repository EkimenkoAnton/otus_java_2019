package ru.otus;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

class DIYarrayListTest {

    private static final int SEED = 11;

    @Test
    void addAll() {
        List<Integer> list = new DIYarrayList<>();
        Integer[] arr = getRandomIntArray(50,0,500);
        Collections.addAll(list,arr);
        assertEquals(list.size(),arr.length);
        assertTrue(Arrays.asList(arr).equals(list));
    }

    @Test
    void copy() {
        List<Integer> srcList = Arrays.asList(getRandomIntArray(50,0,500));
        List<Integer> trgList = new DIYarrayList<>();
        fillArrayWithZero(trgList,50);

        Collections.copy(trgList,srcList);

        assertTrue(srcList.equals(trgList));
    }

    @Test
    void sort() {
        List<Integer> expectedList = Arrays.asList(
                496,482,480,448,442,419,415,404,389,381,
                360,353,351,336,334,333,329,327,326,316,
                314,310,299,296,291,287,278,269,266,265,
                255,220,210,203,201,194,177,177,170,169,
                143,104,91,69,60,60,40,31,30,26
        );
        List<Integer> list = new DIYarrayList<>();
        fillArrrayRandomInt(list,50,0,500);

        Collections.sort(list,Collections.reverseOrder());

        assertTrue(expectedList.equals(list));
    }

    private Integer[] getRandomIntArray(int size , int downBound, int upBound) {
        Random randomizer = new Random(SEED);
        Integer[] arr = new Integer[size];
        for (int i = 0; i < size; i++)
            arr[i] = downBound +  randomizer.nextInt(upBound - downBound + 1);
        return arr;
    }

    private void fillArrayWithZero(List<Integer> list, int size) {
        for (int i = 0; i < size; i++)
            list.add(0);
    }

    private void fillArrrayRandomInt(List<Integer> list, int size , int downBound, int upBound) {
        Integer[] arr = getRandomIntArray(size,downBound,upBound);
        for (int e: arr){
            list.add(e);
        }
    }
}