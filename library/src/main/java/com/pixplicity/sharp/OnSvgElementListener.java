/*
    Copyright 2011, 2015 Pixplicity, Larva Labs LLC and Google, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    Sharp is heavily based on prior work. It was originally forked from
        https://github.com/pents90/svg-android
    And changes from other forks have been consolidated:
        https://github.com/b2renger/svg-android
        https://github.com/mindon/svg-android
        https://github.com/josefpavlik/svg-android
 */

package com.pixplicity.sharp;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public interface OnSvgElementListener {

    void onSvgStart(@NonNull Canvas canvas,
                    @Nullable RectF bounds);

    void onSvgEnd(@NonNull Canvas canvas,
                  @Nullable RectF bounds);

    <T> T onSvgElement(@Nullable String id,
                       @NonNull String localName,
                       @NonNull T element,
                       @Nullable RectF elementBounds,
                       @NonNull Canvas canvas,
                       @Nullable RectF canvasBounds,
                       @Nullable Paint paint);

    <T> void onSvgElementDrawn(@Nullable String id,
                               @NonNull T element,
                               @NonNull Canvas canvas,
                               @Nullable Paint paint);

}
