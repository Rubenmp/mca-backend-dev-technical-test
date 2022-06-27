package com.mca.yourapp.service.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mca.yourapp.service.utils.PreconditionUtils.require;

public class CollectionUtils {
    private CollectionUtils() {}

    public static <T> List<List<T>> splitToListsWithSize(final List<T> collection, final int partitionSize) {
        require(partitionSize > 0, "Size to split lists must be provided");
        if (collection == null || collection.isEmpty()) {
            return Collections.emptyList();
        }

        final List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < collection.size(); i += partitionSize) {
            partitions.add(collection.subList(i, Math.min(i + partitionSize, collection.size())));
        }

        return partitions;
    }

}
