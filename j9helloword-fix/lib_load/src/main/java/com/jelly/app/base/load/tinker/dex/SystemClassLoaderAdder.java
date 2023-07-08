/*
 * Copyright (C) 2016 THL A29 Limited, a Tencent company.
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jelly.app.base.load.tinker.dex;

import android.app.Application;
import android.os.Build;

import com.jelly.app.base.load.tinker.ShareTinkerLog;
import com.jelly.app.base.load.utils.ReflectUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by zhangshaowen on 16/3/18.
 */
public class SystemClassLoaderAdder {
    private static final String TAG = SystemClassLoaderAdder.class.getSimpleName() + "SCLoader";
    private static int sPatchDexCount = 0;
    public static final Pattern CLASS_N_PATTERN = Pattern.compile("classes(?:[2-9]?|[1-9][0-9]+)\\.dex(\\.jar)?");

    public static void installDexes(Application application, ClassLoader loader, File dexOptDir, List<File> files,
                                    boolean isProtectedApp, boolean useDLC) throws Throwable {
        ShareTinkerLog.i(TAG, "installDexes dexOptDir: " + dexOptDir.getAbsolutePath() + ", dex size:" + files.size());

        if (!files.isEmpty()) {
            files = createSortedAdditionalPathEntries(files);
            ClassLoader classLoader = loader;
            if (Build.VERSION.SDK_INT >= 24 && !isProtectedApp) {
                classLoader = NewClassLoaderInjector.inject(application, loader, dexOptDir, useDLC, files);
            } else {
                injectDexesInternal(classLoader, files, dexOptDir);
            }
            //install done
            sPatchDexCount = files.size();
            ShareTinkerLog.i(TAG, "after loaded classloader: " + classLoader + ", dex size:" + sPatchDexCount);
        }
    }

    static void injectDexesInternal(ClassLoader cl, List<File> dexFiles, File optimizeDir) throws Throwable {
        if (Build.VERSION.SDK_INT >= 23) {
            V23.install(cl, dexFiles, optimizeDir);
        } else if (Build.VERSION.SDK_INT >= 19) {
            V19.install(cl, dexFiles, optimizeDir);
        } else if (Build.VERSION.SDK_INT >= 14) {
            V14.install(cl, dexFiles, optimizeDir);
        }
    }

    private static List<File> createSortedAdditionalPathEntries(List<File> additionalPathEntries) {
        final List<File> result = new ArrayList<>(additionalPathEntries);

        final Map<String, Boolean> matchesClassNPatternMemo = new HashMap<>();
        for (File file : result) {
            final String name = file.getName();
            matchesClassNPatternMemo.put(name, CLASS_N_PATTERN.matcher(name).matches());
        }
        Collections.sort(result, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                if (lhs == null && rhs == null) {
                    return 0;
                }
                if (lhs == null) {
                    return -1;
                }
                if (rhs == null) {
                    return 1;
                }

                final String lhsName = lhs.getName();
                final String rhsName = rhs.getName();
                if (lhsName.equals(rhsName)) {
                    return 0;
                }
                final boolean isLhsNameMatchClassN = matchesClassNPatternMemo.get(lhsName);
                final boolean isRhsNameMatchClassN = matchesClassNPatternMemo.get(rhsName);
                if (isLhsNameMatchClassN && isRhsNameMatchClassN) {
                    final int lhsDotPos = lhsName.indexOf('.');
                    final int rhsDotPos = rhsName.indexOf('.');
                    final int lhsId = (lhsDotPos > 7 ? Integer.parseInt(lhsName.substring(7, lhsDotPos)) : 1);
                    final int rhsId = (rhsDotPos > 7 ? Integer.parseInt(rhsName.substring(7, rhsDotPos)) : 1);
                    return (lhsId == rhsId ? 0 : (lhsId < rhsId ? -1 : 1));
                } else if (isLhsNameMatchClassN) {
                    // Dex name that matches class N rules should always be at first.
                    return -1;
                } else if (isRhsNameMatchClassN) {
                    return 1;
                }
                return lhsName.compareTo(rhsName);
            }
        });

        return result;
    }

    /**
     * Installer for platform versions 23.
     */
    private static final class V23 {

        private static void install(ClassLoader loader, List<File> additionalClassPathEntries,
                                    File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IOException {

            Object dexPathList = ReflectUtils.reflect(loader).field("pathList").get();
            ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
            ReflectUtils.expandFieldArray(dexPathList, "dexElements", makePathElements(dexPathList,
                    new ArrayList<File>(additionalClassPathEntries), optimizedDirectory,
                    suppressedExceptions));
            if (suppressedExceptions.size() > 0) {
                for (IOException e : suppressedExceptions) {
                    ShareTinkerLog.w(TAG, "Exception in makePathElement", e);
                    throw e;
                }

            }
        }

        /**
         * A wrapper around
         * {@code private static final dalvik.system.DexPathList#makePathElements}.
         */
        private static Object[] makePathElements(
                Object dexPathList, ArrayList<File> files, File optimizedDirectory,
                ArrayList<IOException> suppressedExceptions)
                throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

            Method makePathElements;
            try {
                makePathElements = ReflectUtils.reflect(dexPathList).getMethod("makePathElements", List.class, File.class, List.class);
            } catch (NoSuchMethodException e) {
                ShareTinkerLog.e(TAG, "NoSuchMethodException: makePathElements(List,File,List) failure");
                try {
                    makePathElements = ReflectUtils.reflect(dexPathList).getMethod("makePathElements", ArrayList.class, File.class, ArrayList.class);
                } catch (NoSuchMethodException e1) {
                    ShareTinkerLog.e(TAG, "NoSuchMethodException: makeDexElements(ArrayList,File,ArrayList) failure");
                    try {
                        ShareTinkerLog.e(TAG, "NoSuchMethodException: try use v19 instead");
                        return V19.makeDexElements(dexPathList, files, optimizedDirectory, suppressedExceptions);
                    } catch (NoSuchMethodException e2) {
                        ShareTinkerLog.e(TAG, "NoSuchMethodException: makeDexElements(List,File,List) failure");
                        throw e2;
                    }
                }
            }
            return (Object[]) makePathElements.invoke(dexPathList, files, optimizedDirectory, suppressedExceptions);
        }
    }

    /**
     * Installer for platform versions 19.
     */
    private static final class V19 {

        private static void install(ClassLoader loader, List<File> additionalClassPathEntries,
                                    File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IOException {
            Object dexPathList = ReflectUtils.reflect(loader).field("pathList").get();
            ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
            ReflectUtils.expandFieldArray(dexPathList, "dexElements", makeDexElements(dexPathList,
                    new ArrayList<File>(additionalClassPathEntries), optimizedDirectory,
                    suppressedExceptions));
            if (suppressedExceptions.size() > 0) {
                for (IOException e : suppressedExceptions) {
                    ShareTinkerLog.w(TAG, "Exception in makeDexElement", e);
                    throw e;
                }
            }
        }

        /**
         * A wrapper around
         * {@code private static final dalvik.system.DexPathList#makeDexElements}.
         */
        private static Object[] makeDexElements(
                Object dexPathList, ArrayList<File> files, File optimizedDirectory,
                ArrayList<IOException> suppressedExceptions)
                throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Method makeDexElements = null;
            try {
                makeDexElements = ReflectUtils.reflect(dexPathList).getMethod("makeDexElements", ArrayList.class, File.class, ArrayList.class);
            } catch (NoSuchMethodException e) {
                ShareTinkerLog.e(TAG, "NoSuchMethodException: makeDexElements(ArrayList,File,ArrayList) failure");
                try {
                    makeDexElements = ReflectUtils.reflect(dexPathList).getMethod("makeDexElements", List.class, File.class, List.class);
                } catch (NoSuchMethodException e1) {
                    ShareTinkerLog.e(TAG, "NoSuchMethodException: makeDexElements(List,File,List) failure");
                    throw e1;
                }
            }
            return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory, suppressedExceptions);
        }
    }

    /**
     * Installer for platform versions 14, 15, 16, 17 and 18.
     */
    private static final class V14 {

        private static void install(ClassLoader loader, List<File> additionalClassPathEntries,
                                    File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
            Object dexPathList = ReflectUtils.reflect(loader).field("pathList").get();
            ReflectUtils.expandFieldArray(dexPathList, "dexElements", makeDexElements(dexPathList,
                    new ArrayList<File>(additionalClassPathEntries), optimizedDirectory));
        }

        /**
         * A wrapper around
         * {@code private static final dalvik.system.DexPathList#makeDexElements}.
         */
        private static Object[] makeDexElements(
                Object dexPathList, ArrayList<File> files, File optimizedDirectory)
                throws IllegalAccessException, InvocationTargetException,
                NoSuchMethodException {
            Method makeDexElements = ReflectUtils.reflect(dexPathList).getMethod("makeDexElements", ArrayList.class, File.class);
            return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory);
        }
    }
}
