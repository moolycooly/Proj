package org.fintech.collection;

import collection.CustomLinkedList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomLinkedListTest {
    @Test
    void add__Success() {
        //given
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        //when
        list.add(1);
        //then
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(1);
    }
    @Test
    void addAll__Success() {
        //given
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        //when
        list.addAll(Arrays.asList(1,2,3,4));

        //then
        assertThat(list.size()).isEqualTo(4);
        for(int i = 1; i < 5; i++) {
            assertThat(list.get(i-1)).isEqualTo(i);
        }

    }
    @Test
    void contains_ElementExists_ReturnIndex() {
        //given
        CustomLinkedList<Integer> list = new CustomLinkedList<>(Arrays.asList(1,2));
        //when
        var result = list.contains(1);
        //then
        assertThat(result).isEqualTo(true);
    }
    @Test
    void contains_ElementNotExists_ReturnNull() {
        //given
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        //when
        var result = list.contains(1);
        //then
        assertThat(result).isEqualTo(false);
    }
    @Test
    void remove_ArgumentIsObjectElementExist_ReturnsTrue() {
        //given
        Integer toRemove = 1;
        CustomLinkedList<Integer> list = new CustomLinkedList<>(Arrays.asList(1,2));
        //when
        var result = list.remove(toRemove);
        //then
        assertThat(result).isEqualTo(true);
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains(1)).isEqualTo(false);

    }
    @Test
    void remove_ArgumentIsObjectElementNotExists_ReturnFalse() {
        //given
        Integer toRemove = 1;
        CustomLinkedList<Integer> list = new CustomLinkedList<>(Arrays.asList(1,2));
        //when
        var result = list.remove(toRemove);
        //then
        assertThat(result).isEqualTo(true);
    }
    @Test
    void remove_ArgumentIsIndexElementExist_ReturnTrue() {
        //given
        int toRemove = 1;
        CustomLinkedList<Integer> list = new CustomLinkedList<>(Arrays.asList(1,2));
        //when
        var result = list.remove(toRemove);
        //then
        assertThat(result).isEqualTo(true);
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains(2)).isEqualTo(false);

    }
    @Test
    void remove_ArgumentIsIndexElementNotExists_ThrowsIndexOutBounds() {
        //given
        int toRemove = 1;
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        //then
        assertThrows(IndexOutOfBoundsException.class,()->list.remove(toRemove));

    }



}
