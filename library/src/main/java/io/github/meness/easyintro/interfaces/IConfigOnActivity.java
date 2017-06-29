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

import android.graphics.drawable.Drawable;
import android.support.annotation.AnimRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.RawRes;
import android.support.v4.app.Fragment;

import io.github.meness.easyintro.EasyIntro;
import io.github.meness.easyintro.enums.IndicatorContainer;
import io.github.meness.easyintro.enums.PageIndicator;
import io.github.meness.easyintro.enums.SlideTransformer;
import io.github.meness.easyintro.enums.ToggleIndicator;

/**
 * configuration on {@link EasyIntro}
 */
public interface IConfigOnActivity {
    void withTranslucentStatusBar(boolean b);

    void withStatusBarColor(@ColorInt int statusBarColor);

    void withOffScreenPageLimit(int limit);

    void withTransparentStatusBar(boolean b);

    void withTransparentNavigationBar(boolean b);

    void withFullscreen(boolean b);

    void withTranslucentNavigationBar(boolean b);

    void withSlideTransformer(SlideTransformer transformer);

    void withToggleIndicator(ToggleIndicator indicators);

    /**
     * enable vibration on slide
     * {@link android.Manifest.permission#VIBRATE} permission required
     *
     * @param intensity intensity
     */
    void withVibrateOnSlide(int intensity);

    /**
     * enable vibration on slide with default 20 intensity
     * {@link android.Manifest.permission#VIBRATE} permission required
     */
    void withVibrateOnSlide();

    /**
     * enable RTL support
     */
    void withRtlSupport();

    /**
     * @param marginPixels margin pixels
     * @see android.support.v4.view.ViewPager#setPageMargin(int)
     */
    void withPageMargin(int marginPixels);

    /**
     * @param d Drawable
     * @see android.support.v4.view.ViewPager#setPageMarginDrawable(Drawable)
     * @see IConfigOnActivity#setPageMarginDrawable(int)
     */
    void setPageMarginDrawable(Drawable d);

    /**
     * @param resId resource id
     * @see android.support.v4.view.ViewPager#setPageMarginDrawable(int)
     * @see IConfigOnActivity#setPageMarginDrawable(Drawable)
     */
    void setPageMarginDrawable(@DrawableRes int resId);

    /**
     * enable sound effects on pressing toggle indicators
     *
     * @param b Boolean
     * @see android.view.View#setSoundEffectsEnabled(boolean)
     */
    void withToggleIndicatorSoundEffects(boolean b);

    /**
     * play sound on slide
     * pass -1 for no sound (by default)
     *
     * @param resId Sound raw resource
     */
    void withSlideSound(@RawRes int resId);

    void withOverScrollMode(int mode);

    void withPageIndicator(PageIndicator pageIndicator);

    /**
     * use different indicator container style
     *
     * @param container IndicatorContainer
     * @see #withIndicatorContainer(int) for custom layout
     */
    void withIndicatorContainer(IndicatorContainer container);

    /**
     * use custom indicator container
     *
     * @param resId resource id
     * @see #withIndicatorContainer(IndicatorContainer) for predefined layouts
     */
    void withIndicatorContainer(@LayoutRes int resId);

    /**
     * disable/enable right indicator for whole slides
     *
     * @param b Boolean
     * @see IConfigMultiple#withRightIndicatorDisabled(boolean, Class) for disabling on specific slides
     */
    void withRightIndicatorDisabled(boolean b);

    /**
     * set different indicator container gravity (bottom and center horizontal by default)
     *
     * @param gravity Gravity
     */
    void withIndicatorContainerGravity(int gravity);

    void withSlide(Fragment slide);

    /**
     * disable/enable left indicator for whole slides
     *
     * @param b Boolean
     * @see IConfigMultiple#withLeftIndicatorDisabled(boolean, Class) for disabling on specific slides
     */
    void withLeftIndicatorDisabled(boolean b);

    /**
     * provide custom page indicator view
     *
     * @param resId resource id
     */
    void withPageIndicator(@LayoutRes int resId);

    /**
     * define overlay slides animations once instead of defining for each overlay slides
     *
     * @param enter    anim resource id
     * @param exit     anim resource id
     * @param popEnter anim resource id
     * @param popExit  anim resource id
     */
    void withOverlaySlideAnimation(@AnimRes int enter, @AnimRes int exit, @AnimRes int popEnter, @AnimRes int popExit);

    /**
     * support back button press on slides (enabled by default)
     *
     * @param b Boolean
     */
    void withSlideBackPressSupport(boolean b);
}
