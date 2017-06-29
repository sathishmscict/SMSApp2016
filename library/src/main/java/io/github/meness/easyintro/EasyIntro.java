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

package io.github.meness.easyintro;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.github.meness.easyintro.enums.IndicatorContainer;
import io.github.meness.easyintro.enums.PageIndicator;
import io.github.meness.easyintro.enums.SlideTransformer;
import io.github.meness.easyintro.enums.ToggleIndicator;
import io.github.meness.easyintro.interfaces.IConfigMultiple;
import io.github.meness.easyintro.interfaces.IConfigOnActivity;
import io.github.meness.easyintro.listeners.EasyIntroInteractionsListener;

public abstract class EasyIntro extends AppCompatActivity implements EasyIntroInteractionsListener, IConfigMultiple, IConfigOnActivity {
    public static final String TAG = EasyIntro.class.getSimpleName();
    private EasyIntroCarouselFragment carouselFragment;

    @Override
    public void onPreviousSlide() {
        // empty
    }

    @Override
    public void onNextSlide() {
        // empty
    }

    @Override
    public void onDonePressed() {
        // empty
    }

    @Override
    public void onSkipPressed() {
        // empty
    }

    protected final EasyIntroCarouselFragment getCarouselFragment() {
        return carouselFragment;
    }

    /**
     * Only Activity has this special callback method
     * Fragment doesn't have any onBackPressed callback
     * <p/>
     * Logic:
     * Each time when the back button is pressed, this Activity will propagate the call to the
     * container Fragment and that Fragment will propagate the call to its each tab Fragment
     * those Fragments will propagate this method call to their child Fragments and
     * eventually all the propagated calls will get back to this initial method
     * <p/>
     * If the container Fragment or any of its Tab Fragments and/or Tab child Fragments couldn't
     * handle the onBackPressed propagated call then this Activity will handle the callback itself
     */
    @Override
    @CallSuper
    public void onBackPressed() {
        if (!carouselFragment.onBackPressed()) {
            // container Fragment or its associates couldn't handle the back pressed task
            // delegating the task to super class
            super.onBackPressed();

        } // else: carousel handled the back pressed task
        // do not call super
    }

    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easyintro_main);

        initCarousel(savedInstanceState);
    }

    private void initCarousel(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            // withholding the previously created fragment from being created again
            // On orientation change, it will prevent fragment recreation
            // its necessary to reserve the fragment stack inside each tab
            // Creating the ViewPager container fragment once
            carouselFragment = (EasyIntroCarouselFragment) EasyIntroCarouselFragment.instantiate(getApplicationContext(), EasyIntroCarouselFragment.class.getName());
            carouselFragment.setInteractionsListener(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, carouselFragment)
                    .commit();
        } else {
            // restoring the previously created fragment
            // and getting the reference
            carouselFragment = (EasyIntroCarouselFragment) getSupportFragmentManager().getFragments().get(0);
        }
    }

    @Override
    @CallSuper
    public void onCarouselViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initIntro();
    }

    protected abstract void initIntro();

    @Override
    public void withTranslucentStatusBar(boolean b) {
        carouselFragment.withTranslucentStatusBar(b);
    }

    @Override
    public void withStatusBarColor(@ColorInt int statusBarColor) {
        carouselFragment.withStatusBarColor(statusBarColor);
    }

    @Override
    public void withOffScreenPageLimit(int limit) {
        carouselFragment.withOffScreenPageLimit(limit);
    }

    @Override
    public void withTransparentStatusBar(boolean b) {
        carouselFragment.withTransparentStatusBar(b);
    }

    @Override
    public void withTransparentNavigationBar(boolean b) {
        carouselFragment.withTransparentNavigationBar(b);
    }

    @Override
    public void withFullscreen(boolean b) {
        carouselFragment.withFullscreen(b);
    }

    @Override
    public void withTranslucentNavigationBar(boolean b) {
        carouselFragment.withTranslucentNavigationBar(b);
    }

    @Override
    public void withSlideTransformer(SlideTransformer transformer) {
        carouselFragment.withSlideTransformer(transformer);
    }

    @Override
    public void withToggleIndicator(ToggleIndicator indicators) {
        carouselFragment.withToggleIndicator(indicators);
    }

    @Override
    public void withVibrateOnSlide(int intensity) {
        carouselFragment.withVibrateOnSlide(intensity);
    }

    @Override
    public void withVibrateOnSlide() {
        carouselFragment.withVibrateOnSlide();
    }

    @Override
    public void withRtlSupport() {
        carouselFragment.withRtlSupport();
    }

    @Override
    public void withPageMargin(int marginPixels) {
        carouselFragment.withPageMargin(marginPixels);
    }

    @Override
    public void setPageMarginDrawable(Drawable d) {
        carouselFragment.setPageMarginDrawable(d);
    }

    @Override
    public void setPageMarginDrawable(@DrawableRes int resId) {
        carouselFragment.setPageMarginDrawable(resId);
    }

    @Override
    public void withToggleIndicatorSoundEffects(boolean b) {
        carouselFragment.withToggleIndicatorSoundEffects(b);
    }

    @Override
    public void withSlideSound(@RawRes int sound) {
        carouselFragment.withSlideSound(sound);
    }

    @Override
    public void withOverScrollMode(int mode) {
        carouselFragment.withOverScrollMode(mode);
    }

    @Override
    public void withPageIndicator(PageIndicator pageIndicator) {
        carouselFragment.withPageIndicator(pageIndicator);
    }

    @Override
    public void withIndicatorContainer(IndicatorContainer container) {
        carouselFragment.withIndicatorContainer(container);
    }

    @Override
    public void withIndicatorContainer(@LayoutRes int resId) {
        carouselFragment.withIndicatorContainer(resId);
    }

    @Override
    public void withRightIndicatorDisabled(boolean b) {
        carouselFragment.withRightIndicatorDisabled(b);
    }

    @Override
    public void withIndicatorContainerGravity(int gravity) {
        carouselFragment.withIndicatorContainerGravity(gravity);
    }

    @Override
    public void withSlide(Fragment slide) {
        carouselFragment.withSlide(slide);
    }

    @Override
    public void withLeftIndicatorDisabled(boolean b) {
        carouselFragment.withLeftIndicatorDisabled(b);
    }

    @Override
    public void withPageIndicator(@LayoutRes int resId) {
        carouselFragment.withPageIndicator(resId);
    }

    @Override
    public void withOverlaySlideAnimation(@AnimRes int enter, @AnimRes int exit, @AnimRes int popEnter, @AnimRes int popExit) {
        carouselFragment.withOverlaySlideAnimation(enter, exit, popEnter, popExit);
    }

    @Override
    public void withSlideBackPressSupport(boolean b) {
        carouselFragment.withSlideBackPressSupport(b);
    }

    @Override
    public void withPageIndicatorVisibility(boolean b) {
        carouselFragment.withPageIndicatorVisibility(b);
    }

    @Override
    public void withRightIndicatorDisabled(boolean b, @NonNull Class slide) {
        carouselFragment.withRightIndicatorDisabled(b, slide);
    }

    @Override
    public void withLeftIndicatorDisabled(boolean b, @NonNull Class slide) {
        carouselFragment.withLeftIndicatorDisabled(b, slide);
    }

    @Override
    public void withBothIndicatorsDisabled(boolean b, Class slide) {
        carouselFragment.withBothIndicatorsDisabled(b, slide);
    }

    @Override
    public void withNextSlide(boolean smoothScroll) {
        carouselFragment.withNextSlide(smoothScroll);
    }

    @Override
    public void withPreviousSlide(boolean smoothScroll) {
        carouselFragment.withPreviousSlide(smoothScroll);
    }

    @Override
    public void withSlideTo(int page, boolean smoothScroll) {
        carouselFragment.withSlideTo(page, smoothScroll);
    }

    @Override
    public Fragment getCurrentSlide() {
        return carouselFragment.getCurrentSlide();
    }

    @Override
    public void withSlideTo(Class slideClass, boolean smoothScroll) {
        carouselFragment.withSlideTo(slideClass, smoothScroll);
    }
}
