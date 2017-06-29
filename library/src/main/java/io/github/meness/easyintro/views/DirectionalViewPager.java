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

package io.github.meness.easyintro.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import io.github.meness.easyintro.enums.SwipeDirection;


public class DirectionalViewPager extends ViewPager {

    private float mInitialXValue;
    private SwipeDirection mSwipeDirection;

    public DirectionalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSwipeDirection = SwipeDirection.ALL;
    }

    public SwipeDirection getSwipeDirection() {
        return mSwipeDirection;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.IsSwipeAllowed(event) && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.IsSwipeAllowed(event) && super.onTouchEvent(event);

    }

    private boolean IsSwipeAllowed(MotionEvent event) {
        if (this.mSwipeDirection == SwipeDirection.ALL) return true;

        if (mSwipeDirection == SwipeDirection.NONE)//disable any swipe
            return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mInitialXValue = event.getX();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            try {
                float diffX = event.getX() - mInitialXValue;
                if (diffX > 0 && mSwipeDirection == SwipeDirection.LEFT) {
                    // swipe from left to right detected
                    return false;
                } else if (diffX < 0 && mSwipeDirection == SwipeDirection.RIGHT) {
                    // swipe from right to left detected
                    return false;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return true;
    }

    public void setAllowedSwipeDirection(SwipeDirection direction) {
        this.mSwipeDirection = direction;
    }
}
