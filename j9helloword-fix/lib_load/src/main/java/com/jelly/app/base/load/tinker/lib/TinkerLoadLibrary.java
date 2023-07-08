/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jelly.app.base.load.tinker.lib;

import android.os.Build;

import com.jelly.app.base.load.tinker.ShareTinkerLog;
import com.jelly.app.base.load.utils.ReflectUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhangshaowen on 17/1/5.
 * Thanks for Android Fragmentation
 */
public class TinkerLoadLibrary {
    private static final String TAG = TinkerLoadLibrary.class.getSimpleName() + "LoadLib";

    public static void installNativeLibraryPath(ClassLoader classLoader, File folder)
            throws Throwable {
        if (folder == null || !folder.exists()) {
            ShareTinkerLog.e(TAG, "installNativeLibraryPath, folder %s is illegal", folder);
            return;
        }
        // android o sdk_int 26
        // for android o preview sdk_int 25
        if ((Build.VERSION.SDK_INT == 25 && Build.VERSION.PREVIEW_SDK_INT != 0)
                || Build.VERSION.SDK_INT > 25) {
            try {
                V25.install(classLoader, folder);
            } catch (Throwable throwable) {
                // install fail, try to treat it as v23
                // some preview N version may go here
                ShareTinkerLog.e(TAG, "installNativeLibraryPath, v25 fail, sdk: %d, error: %s, try to fallback to V23",
                        Build.VERSION.SDK_INT, throwable.getMessage());
                V23.install(classLoader, folder);
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            try {
                V23.install(classLoader, folder);
            } catch (Throwable throwable) {
                // install fail, try to treat it as v14
                ShareTinkerLog.e(TAG, "installNativeLibraryPath, v23 fail, sdk: %d, error: %s, try to fallback to V14",
                        Build.VERSION.SDK_INT, throwable.getMessage());

                V14.install(classLoader, folder);
            }
        } else if (Build.VERSION.SDK_INT >= 14) {
            V14.install(classLoader, folder);
        }
    }

    private static final class V14 {
        private static void install(ClassLoader classLoader, File folder) throws Throwable {
            Object dexPathList = ReflectUtils.reflect(classLoader).field("pathList").get();

            Field nativeLibDirField = ReflectUtils.reflect(dexPathList).getField("nativeLibraryDirectories");
            File[] origNativeLibDirs = (File[]) nativeLibDirField.get(dexPathList);
            final List<File> newNativeLibDirList = new ArrayList<>(origNativeLibDirs.length + 1);
            newNativeLibDirList.add(folder);
            for (File origNativeLibDir : origNativeLibDirs) {
                if (!folder.equals(origNativeLibDir)) {
                    newNativeLibDirList.add(origNativeLibDir);
                }
            }
            nativeLibDirField.set(dexPathList, newNativeLibDirList.toArray(new File[0]));
        }
    }

    private static final class V23 {
        private static void install(ClassLoader classLoader, File folder) throws Throwable {
            Object dexPathList = ReflectUtils.reflect(classLoader).field("pathList").get();
            List<File> origLibDirs = ReflectUtils.reflect(dexPathList).field("nativeLibraryDirectories").get();
            List<File> origSystemLibDirs = ReflectUtils.reflect(dexPathList).field("systemNativeLibraryDirectories").get();

            if (origLibDirs == null) {
                origLibDirs = new ArrayList<>(2);
            }
            final Iterator<File> libDirIt = origLibDirs.iterator();
            while (libDirIt.hasNext()) {
                final File libDir = libDirIt.next();
                if (folder.equals(libDir)) {
                    libDirIt.remove();
                    break;
                }
            }
            origLibDirs.add(0, folder);

            if (origSystemLibDirs == null) {
                origSystemLibDirs = new ArrayList<>(2);
            }
            final List<File> newLibDirs = new ArrayList<>(origLibDirs.size() + origSystemLibDirs.size() + 1);
            newLibDirs.addAll(origLibDirs);
            newLibDirs.addAll(origSystemLibDirs);

            Method makeElements = ReflectUtils.reflect(dexPathList).getMethod("makePathElements", List.class, File.class, List.class);
            final ArrayList<IOException> suppressedExceptions = new ArrayList<>();
            final Object[] elements = (Object[]) makeElements.invoke(dexPathList, newLibDirs, null, suppressedExceptions);

            ReflectUtils.reflect(dexPathList).field("nativeLibraryPathElements", elements);
        }
    }

    private static final class V25 {
        private static void install(ClassLoader classLoader, File folder) throws Throwable {
            Object dexPathList = ReflectUtils.reflect(classLoader).field("pathList").get();
            List<File> origLibDirs = ReflectUtils.reflect(dexPathList).field("nativeLibraryDirectories").get();
            List<File> origSystemLibDirs = ReflectUtils.reflect(dexPathList).field("systemNativeLibraryDirectories").get();

            if (origLibDirs == null) {
                origLibDirs = new ArrayList<>(2);
            }
            final Iterator<File> libDirIt = origLibDirs.iterator();
            while (libDirIt.hasNext()) {
                final File libDir = libDirIt.next();
                if (folder.equals(libDir)) {
                    libDirIt.remove();
                    break;
                }
            }
            origLibDirs.add(0, folder);
            if (origSystemLibDirs == null) {
                origSystemLibDirs = new ArrayList<>(2);
            }

            final List<File> newLibDirs = new ArrayList<>(origLibDirs.size() + origSystemLibDirs.size() + 1);
            newLibDirs.addAll(origLibDirs);
            newLibDirs.addAll(origSystemLibDirs);

            Method makeElements = ReflectUtils.reflect(dexPathList).getMethod("makePathElements", List.class);
            final Object[] elements = (Object[]) makeElements.invoke(dexPathList, newLibDirs);

            ReflectUtils.reflect(dexPathList).field("nativeLibraryPathElements", elements);
        }
    }
}
