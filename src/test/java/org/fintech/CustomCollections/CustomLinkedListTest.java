package org.fintech.CustomCollections;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CustomLinkedListTest {
    Random random = new Random();
    int MAX_SIZE = 50;

    List<Integer> list = random.ints()
            .distinct()
            .limit(MAX_SIZE)
            .boxed()
            .collect(Collectors.toList());
    @Test
    void testAdd() {
        int num = random.nextInt();
        CustomList<Integer> customList = new CustomLinkedList<>();
        customList.add(num);
        assertEquals(num,customList.get(0));
        assertEquals(1,customList.size());
    }
    @Test
    void testAddAll() {
        CustomList<Integer> customList = new CustomLinkedList<>();
        customList.addAll(list);
        assertEquals(MAX_SIZE,customList.size());
        for(int i = 0; i < MAX_SIZE; i++) {
            assertEquals(list.get(i),customList.get(i));
        }
    }
    @Test
    void testContainsTrue() {
        CustomList<Integer> customList = new CustomLinkedList<>(list);
        int index = MAX_SIZE/2;
        assertTrue(customList.contains(list.get(index)));

    }

    @Test
    void testContainsFalse() {
        CustomList<Integer> customList = new CustomLinkedList<>(list);
        assertFalse(list.contains(null));
    }

    @Test
    void testRemoveExistingElement() {
        CustomList<Integer> customList = new CustomLinkedList<>(list);
        Integer value = list.get(MAX_SIZE/2);
        assertTrue(customList.remove(value));
        assertEquals(MAX_SIZE-1, customList.size());
        assertFalse(customList.contains(value));
    }

    @Test
    void testRemoveNoExistingElement() {
        CustomList<Integer> customList = new CustomLinkedList<>(list);
        assertFalse(customList.remove(null));
        assertEquals(MAX_SIZE, customList.size());
    }

    @Test
    void testRemoveByIndexValid() {
        CustomList<Integer> customList = new CustomLinkedList<>(list);
        int index = MAX_SIZE/2;
        assertTrue(customList.remove(index));
        assertEquals(MAX_SIZE-1,customList.size());
        assertFalse(customList.contains(list.get(index)));
    }

    @Test
    void testRemoveAll() {
        CustomList<Integer> customList = new CustomLinkedList<>(list);
        Iterator<Integer> iterator = customList.iterator();
        while(iterator.hasNext()){
            iterator.next();
            iterator.remove();
        }
        assertEquals(0,customList.size());
    }


}
