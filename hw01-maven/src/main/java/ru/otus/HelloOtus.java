package ru.otus;

import com.google.common.collect.Lists;
import java.util.Collection;

public class HelloOtus {

    public static void main(String[] args) {
        print(Lists.newArrayList("hello","world","!"));
    }

    public static void print(Collection collection) {
        System.out.println( String.join(" ", collection) );
    }
}
