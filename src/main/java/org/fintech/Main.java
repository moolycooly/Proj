package org.fintech;

import org.fintech.CustomCollections.CustomLinkedList;
import org.fintech.CustomCollections.CustomList;

import java.util.LinkedList;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {

        CustomList<Integer> test = new CustomLinkedList<>();
        CustomLinkedList<Integer> customList = Stream.of(1, 2, 3, 4, 5)
                .reduce(
                        new CustomLinkedList<>(),
                        (list, value) -> {
                            list.add(value);
                            return list;
                        },
                        (list1, list2) -> list1
                );

        System.out.println(customList);

    }

}