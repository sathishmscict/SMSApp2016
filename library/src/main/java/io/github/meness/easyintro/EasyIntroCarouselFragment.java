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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.AnimRes;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ViewStubCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mikepenz.materialize.MaterializeBuilder;

import java.lang.reflect.InvocationTargetException;

import io.github.meness.easyintro.enums.IndicatorContainer;
import io.github.meness.easyintro.enums.PageIndicator;
import io.github.meness.easyintro.enums.SlideTransformer;
import io.github.meness.easyintro.enums.SwipeDirection;
import io.github.meness.easyintro.enums.ToggleIndicator;
import io.github.meness.easyintro.interfaces.ICheck;
import io.github.meness.easyintro.interfaces.IConfigMultiple;
import io.github.meness.easyintro.interfaces.IConfigOnActivity;
import io.github.meness.easyintro.interfaces.IConfigOnFragment;
import io.github.meness.easyintro.interfaces.ISlide;
import io.github.meness.easyintro.listeners.EasyIntroInteractionsListener;
import io.github.meness.easyintro.listeners.OnBackPressListener;
import io.github.meness.easyintro.listeners.OnToggleIndicatorsClickListener;
import io.github.meness.easyintro.utils.AndroidUtils;
import io.github.meness.easyintro.views.DirectionalViewPager;
import io.github.meness.easyintro.views.LeftToggleIndicator;
import io.github.meness.easyintro.views.RightToggleIndicator;

public class EasyIntroCarouselFragment extends Fragment implements ICheck, IConfigOnActivity, ISlide, OnBackPressListener, IConfigOnFragment, IConfigMultiple, OnToggleIndicatorsClickListener {
    private SparseArray<Class> mDisableLeftIndicatorOn = new SparseArray<>();
    private SparseArray<Class> mDisableRightIndicatorOn = new SparseArray<>();
    private EasyIntroPagerAdapter mAdapter;
    private DirectionalViewPager mPager;
    private View mIndicatorsContainer;
    private ToggleIndicator mToggleIndicator = ToggleIndicator.DEFAULT;
    private RightToggleIndicator mRightIndicator;
    private LeftToggleIndicator mLeftIndicator;
    @RawRes
    private int mSoundRes = -1; // no sound by default
    @LayoutRes
    private int mIndicatorRes = PageIndicator.CIRCLE.getLayout(); // circle page indicator by default
    private Vibrator mVibrator;
    private int mVibrateIntensity;
    private boolean mVibrate;
    private boolean mRtlSupport;
    private SwipeDirection mSwipeDirection = SwipeDirection.ALL;
    private MaterializeBuilder mMaterializeBuilder;
    private EasyIntroInteractionsListener mInteractionsListener;
    @LayoutRes
    private int mIndicatorContainer = IndicatorContainer.ARROW.getLayout(); // arrow layout by default
    private int mIndicatorContainerGravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
    private int[] mOverlaySlidesAnimations = new int[4]; // enter, exit, popEnter, popExit
    private boolean mSlideBackPressSupport = true;
    private boolean mLeftIndicatorEnabled = true;
    private boolean mRightIndicatorEnabled = true;
    private boolean mPageIndicatorVisibility = true;

    public void setInteractionsListener(EasyIntroInteractionsListener listener) {
        this.mInteractionsListener = listener;
    }

    @CallSuper
    protected void onSlideChanged(Fragment fragment) {
        if (mSoundRes != -1) {
            MediaPlayer.create(getContext(), mSoundRes).start();
        }
        if (mVibrate) {
            mVibrator.vibrate(mVibrateIntensity);
        }

        // disable left indicator for specific slide
        for (int i = 0; i < mDisableLeftIndicatorOn.size(); i++) {
            if (fragment.getClass().getName().equalsIgnoreCase(mDisableLeftIndicatorOn.get(i).getName())) {
                mLeftIndicator.withDisabled(true);
                mPager.setAllowedSwipeDirection(SwipeDirection.LEFT);
                break;
            } else {
                mLeftIndicator.withDisabled(false);
                withSwipeDirection(mSwipeDirection);
            }
        }
        // disable right indicator for specific slide
        for (int i = 0; i < mDisableRightIndicatorOn.size(); i++) {
            if (fragment.getClass().getName().equalsIgnoreCase(mDisableRightIndicatorOn.get(i).getName())) {
                mRightIndicator.withDisabled(true);
                mPager.setAllowedSwipeDirection(SwipeDirection.RIGHT);
                break;
            } else {
                mRightIndicator.withDisabled(false);
                withSwipeDirection(mSwipeDirection);
            }
        }

        updateToggleIndicators();
    }

    @Override
    public final void withTranslucentStatusBar(boolean b) {
        mMaterializeBuilder.withTranslucentStatusBarProgrammatically(b);
    }

    @Override
    public final void withStatusBarColor(@ColorInt int statusBarColor) {
        mMaterializeBuilder.withStatusBarColor(statusBarColor);
    }

    @Override
    public final void withOffScreenPageLimit(int limit) {
        mPager.setOffscreenPageLimit(limit);
    }

    @Override
    public final void withTransparentStatusBar(boolean b) {
        mMaterializeBuilder.withTransparentStatusBar(b);
    }

    @Override
    public final void withTransparentNavigationBar(boolean b) {
        mMaterializeBuilder.withTransparentNavigationBar(b);
    }

    @Override
    public final void withFullscreen(boolean b) {
        mMaterializeBuilder.withSystemUIHidden(b);
    }

    @Override
    public final void withTranslucentNavigationBar(boolean b) {
        mMaterializeBuilder.withTranslucentNavigationBarProgrammatically(b);
    }

    @Override
    public final void withSlideTransformer(SlideTransformer transformer) {
        mPager.setPageTransformer(true, transformer.getTransformer());
    }

    @Override
    public final void withToggleIndicator(ToggleIndicator indicators) {
        mToggleIndicator = indicators;

        // RTL swipe support
        if (mRtlSupport && indicators.getSwipeDirection() == SwipeDirection.LEFT) {
            withSwipeDirection(SwipeDirection.RIGHT);
        } else {
            withSwipeDirection(indicators.getSwipeDirection());
        }
    }

    private void withSwipeDirection(SwipeDirection direction) {
        mSwipeDirection = direction;
        mPager.setAllowedSwipeDirection(direction);
    }

    @Override
    public final void withVibrateOnSlide(int intensity) {
        setVibrateEnabled();
        mVibrateIntensity = intensity;
    }

    @Override
    public final void withVibrateOnSlide() {
        withVibrateOnSlide(20);
    }

    @Override
    public final void withRtlSupport() {
        mRtlSupport = true;
    }

    @Override
    public final void withPageMargin(int marginPixels) {
        mPager.setPageMargin(marginPixels);
    }

    @Override
    public final void setPageMarginDrawable(Drawable d) {
        mPager.setPageMarginDrawable(d);
    }

    @Override
    public final void setPageMarginDrawable(@DrawableRes int resId) {
        mPager.setPageMarginDrawable(resId);
    }

    @Override
    public final void withToggleIndicatorSoundEffects(boolean b) {
        mLeftIndicator.setSoundEffectsEnabled(b);
        mRightIndicator.setSoundEffectsEnabled(b);
    }

    @Override
    public final void withSlideSound(@RawRes int sound) {
        mSoundRes = sound;
    }

    @Override
    /**
     * @see android.view.View#setOverScrollMode(int)
     */
    public final void withOverScrollMode(int mode) {
        mPager.setOverScrollMode(mode);
    }

    @Override
    /**
     * set predefined indicator.
     *
     * @param pageIndicator Custom indicator
     */
    public final void withPageIndicator(PageIndicator pageIndicator) {
        mIndicatorRes = pageIndicator.getLayout();
    }

    @Override
    public void withIndicatorContainer(IndicatorContainer container) {
        mIndicatorContainer = container.getLayout();
    }

    @Override
    public void withIndicatorContainer(@LayoutRes int resId) {
        mIndicatorContainer = resId;
    }

    @Override
    public void withRightIndicatorDisabled(boolean b) {
        if (mRightIndicator != null) {
            mRightIndicator.withDisabled(b);
        } else {
            mRightIndicatorEnabled = b;
        }
    }

    @Override
    public void withIndicatorContainerGravity(int gravity) {
        mIndicatorContainerGravity = gravity;
    }

    @Override
    public final void withSlide(Fragment slide) {
        mAdapter.addFragment(slide);
    }

    @Override
    public void withLeftIndicatorDisabled(boolean b) {
        if (mLeftIndicator != null) {
            mLeftIndicator.withDisabled(b);
        } else {
            mLeftIndicatorEnabled = b;
        }
    }

    /**
     * set custom indicator. The indicator must have the `setViewPager(ViewPager)` method
     *
     * @param resId Custom indicator resource
     */
    @Override
    public final void withPageIndicator(@LayoutRes int resId) {
        mIndicatorRes = resId;
    }

    @Override
    public void withOverlaySlideAnimation(@AnimRes int enter, @AnimRes int exit, @AnimRes int popEnter, @AnimRes int popExit) {
        // define animations for whole overlay slides
        mOverlaySlidesAnimations[0] = enter;
        mOverlaySlidesAnimations[1] = exit;
        mOverlaySlidesAnimations[2] = popEnter;
        mOverlaySlidesAnimations[3] = popExit;
    }

    @Override
    public void withSlideBackPressSupport(boolean b) {
        mSlideBackPressSupport = b;
    }

    private void setVibrateEnabled() {
        if (!AndroidUtils.hasVibratePermission(getContext())) {
            Log.d(EasyIntro.TAG, getString(R.string.exception_permission_vibrate));
            return;
        }
        mVibrate = true;
    }

    @Override
    public final void withPageIndicatorVisibility(boolean b) {
        if (mIndicatorsContainer != null) {
            mIndicatorsContainer.findViewById(R.id.pageIndicator).setVisibility(b ? View.VISIBLE : View.GONE);
        } else {
            mPageIndicatorVisibility = b;
        }
    }

    @Override
    public final void withRightIndicatorDisabled(boolean b, @NonNull Class slide) {
        mDisableRightIndicatorOn.append(mDisableRightIndicatorOn.size(), slide);
    }

    @Override
    public final void withLeftIndicatorDisabled(boolean b, @NonNull Class slide) {
        mDisableLeftIndicatorOn.append(mDisableLeftIndicatorOn.size(), slide);
    }

    @Override
    public void withBothIndicatorsDisabled(boolean b, Class slide) {
        withLeftIndicatorDisabled(b, slide);
        withRightIndicatorDisabled(b, slide);
    }

    @Override
    public final void withNextSlide(boolean smoothScroll) {
        mPager.setCurrentItem(mPager.getCurrentItem() + 1, smoothScroll);
    }

    @Override
    public final void withPreviousSlide(boolean smoothScroll) {
        mPager.setCurrentItem(mPager.getCurrentItem() - 1, smoothScroll);
    }

    @Override
    public final void withSlideTo(int page, boolean smoothScroll) {
        mPager.setCurrentItem(page, smoothScroll);
    }

    @Override
    public final Fragment getCurrentSlide() {
        return mAdapter.getRegisteredFragment(mPager.getCurrentItem());
    }

    @Override
    public void withSlideTo(Class slideClass, boolean smoothScroll) {
        withSlideTo(mAdapter.getItemPosition(slideClass), smoothScroll);
    }

    @Override
    public void withOverlaySlide(Fragment slide, @IdRes int container, FragmentManager fragmentManager, boolean addToBackStack) {
        withOverlaySlide(slide, container, fragmentManager, mOverlaySlidesAnimations[0], mOverlaySlidesAnimations[1], mOverlaySlidesAnimations[2], mOverlaySlidesAnimations[3], null, null, addToBackStack);
    }

    @Override
    public void withOverlaySlide(Fragment slide, @IdRes int container, FragmentManager fragmentManager, @AnimRes int enter, @AnimRes int exit, boolean addToBackStack) {
        withOverlaySlide(slide, container, fragmentManager, enter, exit, mOverlaySlidesAnimations[2], mOverlaySlidesAnimations[3], null, null, addToBackStack);
    }

    @Override
    public void withOverlaySlide(Fragment slide, @IdRes int container, FragmentManager fragmentManager, @AnimRes int enter, @AnimRes int exit, @AnimRes int popEnter, @AnimRes int popExit, boolean addToBackStack) {
        withOverlaySlide(slide, container, fragmentManager, enter, exit, popEnter, popExit, null, null, addToBackStack);
    }

    @Override
    public void withOverlaySlide(Fragment slide, @IdRes int container, FragmentManager fragmentManager, @AnimRes int enter, @AnimRes int exit, @AnimRes int popEnter, @AnimRes int popExit, View sharedElement, String transitionName, boolean addToBackStack) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // add to back stack
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.setCustomAnimations(enter, exit, popEnter, popExit);
        if (sharedElement != null) {
            transaction.addSharedElement(sharedElement, transitionName);
        }
        transaction.replace(container, slide).commit();
    }

    private void updateToggleIndicators()
    {
        if (mToggleIndicator == ToggleIndicator.NONE) {
            hideLeftIndicator();
            hideRightIndicator();
            return;
        }

        int slidesCount = mAdapter.getCount() - 1;
        int totalSlides = mAdapter.getCount();
        int currentItem = mPager.getCurrentItem();

        if (totalSlides == 0) {
            hideLeftIndicator();
            hideRightIndicator();
        } else if (totalSlides == 1) {
            hideLeftIndicator();
            showRightIndicator();
            mRightIndicator.makeItDone();
        } else {
            showRightIndicator();
            if (mToggleIndicator == ToggleIndicator.NO_LEFT_INDICATOR) {
                hideLeftIndicator();
            } else {
                showLeftIndicator();
            }
        }

        if (currentItem == slidesCount) {
            if (mToggleIndicator == ToggleIndicator.WITHOUT_DONE) {
                hideRightIndicator();
            } else {
                mRightIndicator.makeItDone();
            }
            mLeftIndicator.makeItPrevious();
        } else if (currentItem < slidesCount) {
            if (currentItem == 0) {
                if (mToggleIndicator == ToggleIndicator.WITHOUT_SKIP || mToggleIndicator == ToggleIndicator.NO_LEFT_INDICATOR) {
                    hideLeftIndicator();

                } else {
                    mLeftIndicator.makeItSkip();
                    showLeftIndicator();
                }
            } else {
                if (mToggleIndicator == ToggleIndicator.NO_LEFT_INDICATOR) {
                    hideLeftIndicator();
                } else {
                    mLeftIndicator.makeItPrevious();
                    showLeftIndicator();
                }
            }
            mRightIndicator.makeItNext();
        }


        /**
         * Added By Sathish Gadde
         */
        mLeftIndicator.hide();
        mRightIndicator.hide();

    }

    private void hideLeftIndicator() {
        mLeftIndicator.hide();
    }

    private void hideRightIndicator() {
        mRightIndicator.hide();
    }

    private void showRightIndicator() {
        mRightIndicator.show();
    }

    private void showLeftIndicator() {
        mLeftIndicator.show();
    }

    @Override
    public final void onRightToggleClick() {
        int slidesCount = mAdapter.getCount() - 1;
        int currentItem = mPager.getCurrentItem();
        int nextItem = currentItem + 1;

        // we're on the last slide
        if (currentItem == slidesCount) {
            onDonePressed();
        }
        // we're going to the last slide
        else if (nextItem == slidesCount) {
            mLeftIndicator.makeItPrevious();
            mRightIndicator.makeItDone();
            onNextSlide();
        }
        // we're going to the next slide
        else {
            mLeftIndicator.makeItPrevious();
            onNextSlide();
        }
    }

    @Override
    public final void onLeftToggleClick() {
        int currentItem = mPager.getCurrentItem();
        int previousItem = currentItem - 1;

        // we're on the first slide
        if (currentItem == 0) {
            onSkipPressed();
        }
        // we're going to the first slide
        else if (previousItem == 0) {
            mRightIndicator.makeItNext();
            mLeftIndicator.makeItSkip();
            onPreviousSlide();
        }
        // we're going to the previous slide
        else {
            mRightIndicator.makeItNext();
            mLeftIndicator.makeItPrevious();
            onPreviousSlide();
        }
    }

    @Override
    public void onPreviousSlide() {
        // pass to current fragment
        Fragment currentFragment = mAdapter.getRegisteredFragment(mPager.getCurrentItem());
        if (currentFragment instanceof ISlide) {
            ((ISlide) currentFragment).onPreviousSlide();
        }
        // pass to EasyIntro activity
        mInteractionsListener.onPreviousSlide();
        withPreviousSlide(true);
    }

    @Override
    public void onNextSlide() {
        // pass to current fragment
        Fragment currentFragment = mAdapter.getRegisteredFragment(mPager.getCurrentItem());
        if (currentFragment instanceof ISlide) {
            ((ISlide) currentFragment).onNextSlide();
        }
        // pass to EasyIntro activity
        mInteractionsListener.onNextSlide();
        withNextSlide(true);
    }

    @Override
    public void onDonePressed() {
        // pass to current fragment
        Fragment currentFragment = mAdapter.getRegisteredFragment(mPager.getCurrentItem());
        if (currentFragment instanceof ISlide) {
            ((ISlide) currentFragment).onDonePressed();
        }
        // pass to EasyIntro activity
        mInteractionsListener.onDonePressed();
    }

    @Override
    public void onSkipPressed() {
        // pass to current fragment
        Fragment currentFragment = mAdapter.getRegisteredFragment(mPager.getCurrentItem());
        if (currentFragment instanceof ISlide) {
            ((ISlide) currentFragment).onSkipPressed();
        }
        // pass to EasyIntro activity
        mInteractionsListener.onSkipPressed();
    }

    @Override
    public boolean isLocked() {
        return getSwipeDirection() == SwipeDirection.NONE;
    }

    @Override
    public SwipeDirection getSwipeDirection() {
        return mPager.getSwipeDirection();
    }

    @Override
    public final int getPageMargin() {
        return mPager.getPageMargin();
    }

    @Override
    public final boolean isRightIndicatorVisible() {
        return mRightIndicator.isVisible();
    }

    @Override
    public final boolean isRightIndicatorDisabled() {
        return mRightIndicator.isDisabled();
    }

    @Override
    public final boolean isLeftIndicatorVisible() {
        return mLeftIndicator.isVisible();
    }

    @Override
    public final boolean isLeftIndicatorDisabled() {
        return mLeftIndicator.isDisabled();
    }

    @Override
    public boolean isToggleIndicatorSoundEffectsEnabled() {
        return mLeftIndicator.isSoundEffectsEnabled() && mRightIndicator.isSoundEffectsEnabled();
    }

    /**
     * Retrieve the currently visible Tab Fragment and propagate the onBackPressed callback
     *
     * @return true = if this fragment and/or one of its associates Fragment can handle the backPress
     */
    public boolean onBackPressed() {
        // currently visible tab Fragment
        OnBackPressListener currentFragment = (OnBackPressListener) mAdapter.getRegisteredFragment(mPager.getCurrentItem());

        if (currentFragment != null) {
            // lets see if the currentFragment or any of its childFragment can handle onBackPressed
            if (mSlideBackPressSupport) {
                if (!currentFragment.onBackPressed()) {
                    // close if first slide is visible
                    if (mPager.getCurrentItem() == 0) {
                        return false;
                    }
                    // swipe back on back pressed
                    withPreviousSlide(true);
                    return true;
                } else {
                    return true;
                }
            } else {
                return currentFragment.onBackPressed();
            }
        }

        // this Fragment couldn't handle the onBackPressed call
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new EasyIntroPagerAdapter(getChildFragmentManager());
        mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_easyintro_carousel, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init
        mPager = (DirectionalViewPager) view.findViewById(R.id.pager);
        mMaterializeBuilder = new MaterializeBuilder(getActivity());

        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                onSlideChanged(mAdapter.getRegisteredFragment(position));
            }
        });

        mInteractionsListener.onCarouselViewCreated(view, savedInstanceState);

        inflateIndicatorContainer(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMaterializeBuilder.build();
    }

    private void inflateIndicatorContainer(final View view) {
        final ViewStubCompat indicatorContainerStub = (ViewStubCompat) view.findViewById(R.id.indicatorContainer);

        // set gravity
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) indicatorContainerStub.getLayoutParams();
        params.gravity = mIndicatorContainerGravity;
        indicatorContainerStub.setLayoutParams(params);

        indicatorContainerStub.setLayoutResource(mIndicatorContainer);

        indicatorContainerStub.setOnInflateListener(new ViewStubCompat.OnInflateListener() {
            @Override
            public void onInflate(ViewStubCompat stub, View inflated) {
                // there must be predefined ids
                if (inflated.findViewById(R.id.leftIndicator) == null) {
                    throw new RuntimeException(getString(R.string.exception_left_indicator_id));
                } else if (inflated.findViewById(R.id.rightIndicator) == null) {
                    throw new RuntimeException(getString(R.string.exception_right_indicator_id));
                } else if (inflated.findViewById(R.id.pageIndicator) == null) {
                    throw new RuntimeException(getString(R.string.exception_page_indicator_id));
                }
                // check indicators instanceof
                else if (!(inflated.findViewById(R.id.leftIndicator) instanceof LeftToggleIndicator)) {
                    throw new RuntimeException(getString(R.string.exception_previous_indicator_instanceof));
                } else if (!(inflated.findViewById(R.id.rightIndicator) instanceof RightToggleIndicator)) {
                    throw new RuntimeException(getString(R.string.exception_next_indicator_instanceof));
                }

                mIndicatorsContainer = inflated;

                // must be initialized after inflating indicator container
                mRightIndicator = (RightToggleIndicator) mIndicatorsContainer.findViewById(R.id.rightIndicator);
                mLeftIndicator = (LeftToggleIndicator) mIndicatorsContainer.findViewById(R.id.leftIndicator);
                mRightIndicator.setListener(EasyIntroCarouselFragment.this);
                mRightIndicator.withDisabled(mRightIndicatorEnabled);
                mLeftIndicator.setListener(EasyIntroCarouselFragment.this);
                mLeftIndicator.withDisabled(mLeftIndicatorEnabled);

                addIndicator();
                updateToggleIndicators();
            }
        });
        indicatorContainerStub.inflate();
    }

    private void addIndicator() {
        if (mIndicatorRes != -1) {
            ViewStubCompat viewStub = (ViewStubCompat) mIndicatorsContainer.findViewById(R.id.pageIndicator);
            viewStub.setLayoutResource(mIndicatorRes);
            viewStub.setOnInflateListener(new ViewStubCompat.OnInflateListener() {
                @Override
                public void onInflate(ViewStubCompat stub, View inflated) {
                    setViewPagerToPageIndicator();
                }
            });
            View view = viewStub.inflate();
            view.setVisibility(mPageIndicatorVisibility ? View.VISIBLE : View.GONE);
        }
    }

    private void setViewPagerToPageIndicator() {
        try {
            View view = mIndicatorsContainer.findViewById(R.id.pageIndicator);
            // invoke indicator `setViewPager(ViewPager)` method
            view.getClass().getMethod("setViewPager", ViewPager.class).invoke(view, mPager);
        } catch (NoSuchMethodException e) {
            Log.e(EasyIntro.TAG, getString(R.string.exception_no_such_method_set_view_pager));
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e(EasyIntro.TAG, getString(R.string.exception_invocation_target_set_view_pager));
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(EasyIntro.TAG, getString(R.string.exception_illegal_access_set_view_pager));
            e.printStackTrace();
        }
    }
}