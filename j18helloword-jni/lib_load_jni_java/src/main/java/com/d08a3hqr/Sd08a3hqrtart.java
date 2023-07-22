package com.d08a3hqr;

import android.app.Application;

import androidx.annotation.Keep;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Sd08a3hqrtart {

    public static native void load(Application context, List<File> libFiles, List<File> pluginFiles,
                                   File oatDir, String pluginPath);

    @Keep
    public static <T> List<T> getd08a3hqrNewList(List<T> origList, List<T> nowList, List<T> otherList) {
        if (origList == null) {
            origList = new ArrayList<>(2);
        }
        final Iterator<T> iterator = origList.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            for (T t : nowList) {
                if (t.equals(next)) {
                    iterator.remove();
                    break;
                }
            }
        }
        origList.addAll(0, nowList);
        if (otherList == null) {
            otherList = new ArrayList<>(2);
        }
        final List<T> newList = new ArrayList<>(origList.size() + otherList.size() + 1);
        newList.addAll(origList);
        newList.addAll(otherList);
        return newList;
    }

    @Keep
    public static String getd08a3hqrPath(Object nativeLibraryDirectories) {
        List<File> oldNativeLibraryDirectories = null;
        if (nativeLibraryDirectories instanceof List) {
            oldNativeLibraryDirectories = (List<File>) nativeLibraryDirectories;
        } else {
            Arrays.asList((File[]) nativeLibraryDirectories);
        }
        return getString(oldNativeLibraryDirectories);
    }

    @Keep
    public static String getd08a3hqrPath2(List<File> dexList) {
        return getString(dexList);
    }

    private static String getString(List<File> list) {
        if (list == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); ++i) {
            if (i > 0) {
                stringBuilder.append(File.pathSeparator);
            }
            stringBuilder.append(list.get(i).getAbsolutePath());
        }
        return stringBuilder.toString();
    }
}
