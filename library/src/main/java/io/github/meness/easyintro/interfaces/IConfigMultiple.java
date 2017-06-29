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

package io.github.meness.easyintro.interfaces;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import io.github.meness.easyintro.EasyIntro;
import io.github.meness.easyintro.EasyIntroFragment;

/**
 * configuration on {@link EasyIntro} and/or {@link EasyIntroFragment}
 */
public interface IConfigMultiple {
    void withPageIndicatorVisibility(boolean b);

    // disable indicator for specific slide
    void withRightIndicatorDisabled(boolean b, @NonNull Class slide);

    // disable indicator for specific slide
    void withLeftIndicatorDisabled(boolean b, @NonNull Class slide);

    void withBothIndicatorsDisabled(boolean b, Class slide);

    void withNextSlide(boolean smoothScroll);

    void withPreviousSlide(boolean smoothScroll);

    void withSlideTo(int page, boolean smoothScroll);

    Fragment getCurrentSlide();

    void withSlideTo(Class slideClass, boolean smoothScroll);
}
