package com.jelly.app.base.load;

import android.app.Application;
import android.util.Log;

import androidx.annotation.Keep;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Start {

    public static void init(Application app, List<File> libFiles, List<File> dexFiles,
                            File oatDir, String pluginPath, String soPath) {
        try {
            System.load(soPath);
            // 开始加载
            load(app, libFiles, dexFiles, oatDir, pluginPath);
        } catch (Throwable t) {
            Log.e("Start", "init", t);
        }
    }

    public static native void load(Application context, List<File> libFiles, List<File> pluginFiles,
                                   File oatDir, String pluginPath);

    @Keep
    public static <T> List<T> getNewList(List<T> origList, List<T> nowList, List<T> otherList) {
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
    public static String getPath(Object nativeLibraryDirectories) {
        List<File> oldNativeLibraryDirectories = null;
        if (nativeLibraryDirectories instanceof List) {
            oldNativeLibraryDirectories = (List<File>) nativeLibraryDirectories;
        } else {
            Arrays.asList((File[]) nativeLibraryDirectories);
        }
        return getString(oldNativeLibraryDirectories);
    }

    @Keep
    public static String getPath2(List<File> dexList) {
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
