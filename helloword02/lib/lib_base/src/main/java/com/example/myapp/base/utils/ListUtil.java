package com.example.myapp.base.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListUtil {

    public static <T> boolean isEmpty(List<T> list) {
        if (list == null) {
            return true;
        }
        return list.isEmpty();
    }

//    public static boolean isEmpty(Object... objects) {
//        if (objects == null) {
//            return true;
//        }
//        if (objects.length < 1) {
//            return true;
//        }
//        return false;
//    }

    public static boolean isEmpty(Object[] strings) {
        if (strings == null) {
            return true;
        }
        return strings.length < 1;
    }

    public static <T> boolean isEmpty(Set<T> set) {
        if (set == null) {
            return true;
        }
        return set.size() < 1;
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        if (map == null) {
            return true;
        }
        return map.size() < 1;
    }

    public static boolean isEmpty(int[] ints) {
        if (ints == null) {
            return true;
        }
        return ints.length < 1;
    }

    public static <T> boolean isIndexOut(List<T> list, int position) {
        if (isEmpty(list)) {
            return true;
        }
        return position < 0 || position >= list.size();
    }
}
