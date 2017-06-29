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

import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import io.github.meness.easyintro.enums.SwipeDirection;
import io.github.meness.easyintro.interfaces.ICheck;
import io.github.meness.easyintro.interfaces.IConfigMultiple;
import io.github.meness.easyintro.interfaces.IConfigOnFragment;
import io.github.meness.easyintro.interfaces.ISlide;
import io.github.meness.easyintro.listeners.OnBackPressListener;
import io.github.meness.easyintro.utils.BackPressImpl;

public class EasyIntroFragment extends Fragment implements ICheck, ISlide, IConfigOnFragment, IConfigMultiple, OnBackPressListener {
    @Override
    public boolean isLocked() {
        return getBaseContext().isLocked();
    }

    @Override
    public SwipeDirection getSwipeDirection() {
        return getBaseContext().getSwipeDirection();
    }

    @Override
    public int getPageMargin() {
        return getBaseContext().getPageMargin();
    }

    @Override
    public boolean isRightIndicatorVisible() {
        return getBaseContext().isRightIndicatorVisible();
    }

    @Override
    public boolean isRightIndicatorDisabled() {
        return getBaseContext().isRightIndicatorDisabled();
    }

    @Override
    public boolean isLeftIndicatorVisible() {
        return getBaseContext().isLeftIndicatorVisible();
    }

    @Override
    public boolean isLeftIndicatorDisabled() {
        return getBaseContext().isLeftIndicatorDisabled();
    }

    @Override
    public boolean isToggleIndicatorSoundEffectsEnabled() {
        return getBaseContext().isToggleIndicatorSoundEffectsEnabled();
    }

    protected final EasyIntroCarouselFragment getBaseContext() {
        return ((EasyIntro) getContext()).getCarouselFragment();
    }

    @Override
    public boolean onBackPressed() {
        return new BackPressImpl(this).onBackPressed();
    }

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

    @Override
    public void withPageIndicatorVisibility(boolean b) {
        getBaseContext().withPageIndicatorVisibility(b);
    }

    @Override
    public void withRightIndicatorDisabled(boolean b, @NonNull Class slide) {
        getBaseContext().withRightIndicatorDisabled(b, slide);
    }

    @Override
    public void withLeftIndicatorDisabled(boolean b, @NonNull Class slide) {
        getBaseContext().withLeftIndicatorDisabled(b, slide);
    }

    @Override
    public void withBothIndicatorsDisabled(boolean b, Class slide) {
        getBaseContext().withBothIndicatorsDisabled(b, slide);
    }

    @Override
    public void withNextSlide(boolean smoothScroll) {
        getBaseContext().withNextSlide(smoothScroll);
    }

    @Override
    public void withPreviousSlide(boolean smoothScroll) {
        getBaseContext().withPreviousSlide(smoothScroll);
    }

    @Override
    public void withSlideTo(int page, boolean smoothScroll) {
        getBaseContext().withSlideTo(page, smoothScroll);
    }

    @Override
    public Fragment getCurrentSlide() {
        return getBaseContext().getCurrentSlide();
    }

    @Override
    public void withSlideTo(Class slideClass, boolean smoothScroll) {
        getBaseContext().withSlideTo(slideClass, smoothScroll);
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set view clickable property true to prevent passing click event to its parent
        if (!view.isClickable()) {
            view.setClickable(true);
        }
    }

    @Override
    public void withOverlaySlide(Fragment slide, @IdRes int container, FragmentManager fragmentManager, boolean addToBackStack) {
        getBaseContext().withOverlaySlide(slide, container, fragmentManager, addToBackStack);
    }

    @Override
    public void withOverlaySlide(Fragment slide, @IdRes int container, FragmentManager fragmentManager, @AnimRes int enter, @AnimRes int exit, boolean addToBackStack) {
        getBaseContext().withOverlaySlide(slide, container, fragmentManager, enter, exit, addToBackStack);
    }

    @Override
    public void withOverlaySlide(Fragment slide, @IdRes int container, FragmentManager fragmentManager, @AnimRes int enter, @AnimRes int exit, @AnimRes int popEnter, @AnimRes int popExit, boolean addToBackStack) {
        getBaseContext().withOverlaySlide(slide, container, fragmentManager, enter, exit, popEnter, popExit, addToBackStack);
    }

    @Override
    public void withOverlaySlide(Fragment slide, @IdRes int container, FragmentManager fragmentManager, @AnimRes int enter, @AnimRes int exit, @AnimRes int popEnter, @AnimRes int popExit, View sharedElement, String transitionName, boolean addToBackStack) {
        getBaseContext().withOverlaySlide(slide, container, fragmentManager, enter, exit, popEnter, popExit, sharedElement, transitionName, addToBackStack);
    }
}
