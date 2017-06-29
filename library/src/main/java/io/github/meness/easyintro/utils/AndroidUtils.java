/*
 * Copyright 2016 Alireza Eskandarpour Shoferi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.meness.easyintro.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

public final class AndroidUtils {

    private AndroidUtils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static boolean hasVibratePermission(Context context) {
        int res = context.checkCallingOrSelfPermission(Manifest.permission.VIBRATE);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}
