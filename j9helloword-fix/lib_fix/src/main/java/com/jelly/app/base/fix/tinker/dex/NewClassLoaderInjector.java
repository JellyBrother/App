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

package com.jelly.app.base.fix.tinker.dex;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.jelly.app.base.fix.utils.FileUtils;
import com.jelly.app.base.fix.utils.ReflectUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import dalvik.system.DelegateLastClassLoader;

/**
 * Created by tangyinsheng on 2019-10-31.
 */
final class NewClassLoaderInjector {
    public static ClassLoader inject(Application app, ClassLoader oldClassLoader, File dexOptDir,
                                     boolean useDLC, List<File> patchedDexes) throws Throwable {
        final String[] patchedDexPaths = new String[patchedDexes.size()];
        for (int i = 0; i < patchedDexPaths.length; ++i) {
            patchedDexPaths[i] = patchedDexes.get(i).getAbsolutePath();
        }
        final ClassLoader newClassLoader = createNewClassLoader(oldClassLoader,
                dexOptDir, useDLC, true, patchedDexPaths);
        doInject(app, newClassLoader);
        return newClassLoader;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("unchecked")
    private static ClassLoader createNewClassLoader(ClassLoader oldClassLoader,
                                                    File dexOptDir,
                                                    boolean useDLC,
                                                    boolean forActualLoading,
                                                    String... patchDexPaths) throws Throwable {
        Object oldPathList = ReflectUtils.reflect(Class.forName("dalvik.system.BaseDexClassLoader", false, oldClassLoader)).field("pathList").get();
        final StringBuilder dexPathBuilder = new StringBuilder();
        final boolean hasPatchDexPaths = patchDexPaths != null && patchDexPaths.length > 0;
        if (hasPatchDexPaths) {
            for (int i = 0; i < patchDexPaths.length; ++i) {
                if (i > 0) {
                    dexPathBuilder.append(File.pathSeparator);
                }
                dexPathBuilder.append(patchDexPaths[i]);
            }
        }
        final String combinedDexPath = dexPathBuilder.toString();

        Field nativeLibraryDirectoriesField = ReflectUtils.reflect(oldPathList).getField("nativeLibraryDirectories");
        List<File> oldNativeLibraryDirectories = null;
        if (nativeLibraryDirectoriesField.getType().isArray()) {
            oldNativeLibraryDirectories = Arrays.asList((File[]) nativeLibraryDirectoriesField.get(oldPathList));
        } else {
            oldNativeLibraryDirectories = (List<File>) nativeLibraryDirectoriesField.get(oldPathList);
        }
        final StringBuilder libraryPathBuilder = new StringBuilder();
        boolean isFirstItem = true;
        for (File libDir : oldNativeLibraryDirectories) {
            if (libDir == null) {
                continue;
            }
            if (isFirstItem) {
                isFirstItem = false;
            } else {
                libraryPathBuilder.append(File.pathSeparator);
            }
            libraryPathBuilder.append(libDir.getAbsolutePath());
        }
        final String combinedLibraryPath = libraryPathBuilder.toString();
        ClassLoader result = null;
        if (useDLC && FileUtils.isNewerOrEqualThanVersion(27, true)) {
            if (FileUtils.isNewerOrEqualThanVersion(31, true)) {
                result = new DelegateLastClassLoader(combinedDexPath, combinedLibraryPath, oldClassLoader);
            } else {
                result = new DelegateLastClassLoader(combinedDexPath, combinedLibraryPath, ClassLoader.getSystemClassLoader());
                ReflectUtils.reflect(result).field("parent", oldClassLoader);
            }
        } else {
            result = new TinkerClassLoader(combinedDexPath, dexOptDir, combinedLibraryPath, oldClassLoader);
        }
        // 'EnsureSameClassLoader' mechanism which is first introduced in Android O
        // may cause exception if we replace definingContext of old classloader.
        if (forActualLoading && !FileUtils.isNewerOrEqualThanVersion(26, true)) {
            ReflectUtils.reflect(oldPathList).field("definingContext", result);
        }
        return result;
    }

    public static void doInject(Application app, ClassLoader classLoader) throws Throwable {
        Thread.currentThread().setContextClassLoader(classLoader);
        Context baseContext = ReflectUtils.reflect(app).field("mBase").get();
        try {
            ReflectUtils.reflect(baseContext).field("mClassLoader", classLoader);
        } catch (Throwable ignored) {
            // There's no mClassLoader field in ContextImpl before Android O.
            // However we should try our best to replace this field in case some
            // customized system has one.
        }
        Object basePackageInfo = ReflectUtils.reflect(baseContext).field("mPackageInfo").get();
        ReflectUtils.reflect(basePackageInfo).field("mClassLoader", classLoader);
        final Resources res = app.getResources();
        try {
            ReflectUtils.reflect(res).field("mClassLoader", classLoader);
        } catch (Throwable ignored) {
            // Ignored.
        }
        try {
            Object drawableInflater = ReflectUtils.reflect(res).field("mDrawableInflater").get();
            if (drawableInflater != null) {
                ReflectUtils.reflect(drawableInflater).field("mClassLoader", classLoader);
            }
        } catch (Throwable ignored) {
            // Ignored.
        }
    }

    private NewClassLoaderInjector() {
        throw new UnsupportedOperationException();
    }
}