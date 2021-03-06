package com.mca.yourapp.service.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.mca.yourapp.service.utils.CollectionUtils.splitToListsWithSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CollectionUtilsTest {

    @Test
    void splitToListsWithSize_normal() {
        final List<Integer> initialList = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final int partitionSize = 4;

        final List<List<Integer>> splitLists = splitToListsWithSize(initialList, partitionSize);

        assertNotNull(splitLists, "Output cannot be null");
        final int expectedLength = initialList.size() / partitionSize + 1;
        assertEquals(expectedLength, splitLists.size(), "Split lists size");
        assertEquals(List.of(1, 2, 3, 4), splitLists.get(0), "First list");
        assertEquals(List.of(5, 6, 7, 8), splitLists.get(1), "Second list");
        assertEquals(List.of(9, 10), splitLists.get(2), "Third list");
    }

    @Test
    void splitToListsWithSize_partitionSizeBiggerThanListSize() {
        final List<Integer> initialList = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final int partitionSize = initialList.size() + 2;

        final List<List<Integer>> splitLists = splitToListsWithSize(initialList, partitionSize);

        assertNotNull(splitLists, "Output cannot be null");
        assertEquals(1, splitLists.size(), "Split lists size");
        assertEquals(initialList, splitLists.get(0), "Partitioned list");
    }

    @Test
    void splitToListsWithSize_partitionSizeOne() {
        final List<Integer> initialList = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final int partitionSize = 1;

        final List<List<Integer>> splitLists = splitToListsWithSize(initialList, partitionSize);

        assertNotNull(splitLists, "Output cannot be null");
        assertEquals(initialList.size(), splitLists.size(), "Split lists size");
        for (int i = 0; i < initialList.size(); ++i) {
            assertEquals(List.of(i + 1), splitLists.get(i), "Partitioned list number " + 1);
        }
    }

    @Test
    void splitToListsWithSize_partitionSizeZero() {
        final List<Integer> initialList = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertThrows(IllegalArgumentException.class,
                () -> splitToListsWithSize(initialList, 0));
    }

    @Test
    void splitToListsWithSize_partitionSizeNegative() {
        final List<Integer> initialList = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertThrows(IllegalArgumentException.class,
                () -> splitToListsWithSize(initialList, -1));
    }
}
